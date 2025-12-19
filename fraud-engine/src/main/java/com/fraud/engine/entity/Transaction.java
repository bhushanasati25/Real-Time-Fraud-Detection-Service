package com.fraud.engine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transaction Entity - JPA entity for persisting transactions.
 */
@Entity
@Table(name = "transactions", schema = "fraud")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "merchant_id", length = 100)
    private String merchantId;

    @Column(name = "merchant_name", length = 255)
    private String merchantName;

    @Column(name = "merchant_category", length = 100)
    private String merchantCategory;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "card_type", length = 50)
    private String cardType;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "transaction_type", length = 50)
    private String transactionType;

    @Column(name = "channel", length = 50)
    private String channel;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "is_fraud")
    @Builder.Default
    private Boolean isFraud = false;

    @Column(name = "fraud_score", precision = 5, scale = 4)
    private BigDecimal fraudScore;

    @Column(name = "fraud_reason", columnDefinition = "TEXT")
    private String fraudReason;

    @Column(name = "rules_triggered", columnDefinition = "TEXT[]")
    private String[] rulesTriggered;

    @Column(name = "processing_time_ms")
    private Integer processingTimeMs;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "processed_at")
    private Instant processedAt;
}
