package com.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * TransactionEvent DTO - Core data contract for transaction processing.
 * 
 * This object represents a financial transaction that flows through the
 * fraud detection pipeline from ingestion to processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Unique transaction identifier
     */
    @NotBlank(message = "Transaction ID is required")
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    @JsonProperty("transactionId")
    private String transactionId;

    /**
     * Transaction amount
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * Currency code (ISO 4217)
     */
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @JsonProperty("currency")
    @Builder.Default
    private String currency = "USD";

    /**
     * User/Customer identifier
     */
    @NotBlank(message = "User ID is required")
    @Size(max = 100, message = "User ID must not exceed 100 characters")
    @JsonProperty("userId")
    private String userId;

    /**
     * Merchant identifier
     */
    @Size(max = 100, message = "Merchant ID must not exceed 100 characters")
    @JsonProperty("merchantId")
    private String merchantId;

    /**
     * Merchant name
     */
    @Size(max = 255, message = "Merchant name must not exceed 255 characters")
    @JsonProperty("merchantName")
    private String merchantName;

    /**
     * Merchant category code (MCC)
     */
    @Size(max = 100, message = "Merchant category must not exceed 100 characters")
    @JsonProperty("merchantCategory")
    private String merchantCategory;

    /**
     * Transaction location (city, country)
     */
    @Size(max = 255, message = "Location must not exceed 255 characters")
    @JsonProperty("location")
    private String location;

    /**
     * Latitude coordinate
     */
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * Longitude coordinate
     */
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @JsonProperty("longitude")
    private Double longitude;

    /**
     * IP address of the transaction origin
     */
    @Size(max = 45, message = "IP address must not exceed 45 characters")
    @JsonProperty("ipAddress")
    private String ipAddress;

    /**
     * Device identifier
     */
    @Size(max = 100, message = "Device ID must not exceed 100 characters")
    @JsonProperty("deviceId")
    private String deviceId;

    /**
     * Card type (VISA, MASTERCARD, AMEX, etc.)
     */
    @Size(max = 50, message = "Card type must not exceed 50 characters")
    @JsonProperty("cardType")
    private String cardType;

    /**
     * Last 4 digits of the card
     */
    @Size(min = 4, max = 4, message = "Card last four must be exactly 4 digits")
    @Pattern(regexp = "^[0-9]{4}$", message = "Card last four must contain only digits")
    @JsonProperty("cardLastFour")
    private String cardLastFour;

    /**
     * Transaction type (PURCHASE, WITHDRAWAL, TRANSFER, etc.)
     */
    @Size(max = 50, message = "Transaction type must not exceed 50 characters")
    @JsonProperty("transactionType")
    @Builder.Default
    private String transactionType = "PURCHASE";

    /**
     * Transaction channel (ONLINE, POS, ATM, MOBILE)
     */
    @Size(max = 50, message = "Channel must not exceed 50 characters")
    @JsonProperty("channel")
    @Builder.Default
    private String channel = "ONLINE";

    /**
     * Timestamp of the transaction
     */
    @NotNull(message = "Timestamp is required")
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    /**
     * Additional metadata/custom fields
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Timestamp when the event was received by the system
     */
    @JsonProperty("receivedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant receivedAt;

    /**
     * Source system that originated the transaction
     */
    @JsonProperty("sourceSystem")
    private String sourceSystem;
}
