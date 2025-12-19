package com.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * FraudAlert DTO - Alert notification payload.
 * 
 * This object is published to the fraud-alerts Kafka topic when
 * a transaction is flagged as potentially fraudulent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique alert identifier
     */
    @JsonProperty("alertId")
    private String alertId;

    /**
     * Transaction ID that triggered the alert
     */
    @JsonProperty("transactionId")
    private String transactionId;

    /**
     * User ID associated with the transaction
     */
    @JsonProperty("userId")
    private String userId;

    /**
     * Transaction amount
     */
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * Currency code
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * Alert type/category
     */
    @JsonProperty("alertType")
    private AlertType alertType;

    /**
     * Alert severity level
     */
    @JsonProperty("severity")
    private Severity severity;

    /**
     * Fraud score from detection engine
     */
    @JsonProperty("fraudScore")
    private BigDecimal fraudScore;

    /**
     * List of triggered rules
     */
    @JsonProperty("triggeredRules")
    private List<String> triggeredRules;

    /**
     * Human-readable alert description
     */
    @JsonProperty("description")
    private String description;

    /**
     * Recommended action for analysts
     */
    @JsonProperty("recommendedAction")
    private String recommendedAction;

    /**
     * Transaction location
     */
    @JsonProperty("location")
    private String location;

    /**
     * IP address of transaction origin
     */
    @JsonProperty("ipAddress")
    private String ipAddress;

    /**
     * Merchant information
     */
    @JsonProperty("merchantName")
    private String merchantName;

    /**
     * Original transaction timestamp
     */
    @JsonProperty("transactionTimestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant transactionTimestamp;

    /**
     * Alert creation timestamp
     */
    @JsonProperty("alertTimestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant alertTimestamp;

    /**
     * Alert status
     */
    @JsonProperty("status")
    @Builder.Default
    private AlertStatus status = AlertStatus.OPEN;

    /**
     * Assigned analyst ID
     */
    @JsonProperty("assignedTo")
    private String assignedTo;

    /**
     * Additional context/metadata
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Notification channels to use
     */
    @JsonProperty("notificationChannels")
    private List<NotificationChannel> notificationChannels;

    /**
     * Alert type enumeration
     */
    public enum AlertType {
        HIGH_AMOUNT,
        VELOCITY_BREACH,
        LOCATION_ANOMALY,
        IP_MISMATCH,
        ML_DETECTION,
        RULE_BASED,
        PATTERN_MATCH,
        BLACKLIST_HIT
    }

    /**
     * Severity level enumeration
     */
    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Alert status enumeration
     */
    public enum AlertStatus {
        OPEN,
        IN_REVIEW,
        CONFIRMED_FRAUD,
        FALSE_POSITIVE,
        RESOLVED
    }

    /**
     * Notification channel enumeration
     */
    public enum NotificationChannel {
        EMAIL,
        SMS,
        WEBHOOK,
        SLACK,
        PUSH_NOTIFICATION
    }
}
