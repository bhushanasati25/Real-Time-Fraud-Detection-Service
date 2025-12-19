package com.fraud.engine.repository;

import com.fraud.engine.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Transaction Repository - Data access for transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    boolean existsByTransactionId(String transactionId);

    List<Transaction> findByUserId(String userId);

    List<Transaction> findByUserIdAndIsFraudTrue(String userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByUser(
            @Param("userId") String userId,
            @Param("since") Instant since);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    Long countTransactionsByUserSince(
            @Param("userId") String userId,
            @Param("since") Instant since);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    java.math.BigDecimal sumAmountByUserSince(
            @Param("userId") String userId,
            @Param("since") Instant since);

    @Query("SELECT t FROM Transaction t WHERE t.isFraud = true ORDER BY t.createdAt DESC")
    List<Transaction> findFraudulentTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' ORDER BY t.createdAt ASC")
    List<Transaction> findPendingTransactions();
}
