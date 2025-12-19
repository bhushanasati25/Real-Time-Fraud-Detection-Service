package com.fraud.engine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * User Profile Entity - Stores user behavioral data for fraud detection.
 */
@Entity
@Table(name = "user_profiles", schema = "fraud")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "typical_transaction_amount", precision = 19, scale = 4)
    private BigDecimal typicalTransactionAmount;

    @Column(name = "last_known_ip", length = 45)
    private String lastKnownIp;

    @Column(name = "last_known_location", length = 255)
    private String lastKnownLocation;

    @Column(name = "last_transaction_at")
    private Instant lastTransactionAt;

    @Column(name = "transaction_count_24h")
    @Builder.Default
    private Integer transactionCount24h = 0;

    @Column(name = "total_amount_24h", precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalAmount24h = BigDecimal.ZERO;

    @Column(name = "risk_score", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal riskScore = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
