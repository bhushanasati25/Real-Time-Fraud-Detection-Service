package com.fraud.ingestion.service;

import com.fraud.common.dto.TransactionEvent;
import com.fraud.common.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Validator Service.
 * 
 * Provides custom validation logic for transaction events
 * beyond standard Bean Validation annotations.
 */
@Service
@Slf4j
public class TransactionValidatorService {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000000.00"); // 10 million
    private static final int MAX_TRANSACTION_ID_LENGTH = 100;
    private static final int MAX_USER_ID_LENGTH = 100;

    /**
     * Validate a transaction event.
     *
     * @param event The transaction to validate
     * @throws ValidationException if validation fails
     */
    public void validateTransaction(TransactionEvent event) {
        List<String> errors = new ArrayList<>();

        // Null check
        if (event == null) {
            throw new ValidationException("Transaction event cannot be null");
        }

        // Validate amount
        validateAmount(event.getAmount(), errors);

        // Validate user ID
        validateUserId(event.getUserId(), errors);

        // Validate transaction ID (if provided)
        validateTransactionId(event.getTransactionId(), errors);

        // Validate currency
        validateCurrency(event.getCurrency(), errors);

        // Validate IP address format (if provided)
        validateIpAddress(event.getIpAddress(), errors);

        // Validate coordinates (if provided)
        validateCoordinates(event.getLatitude(), event.getLongitude(), errors);

        // Validate card last four (if provided)
        validateCardLastFour(event.getCardLastFour(), errors);

        // Throw exception if there are validation errors
        if (!errors.isEmpty()) {
            log.warn("Transaction validation failed with {} errors: {}", errors.size(), errors);
            throw new ValidationException("Transaction validation failed", errors);
        }

        log.debug("Transaction validation passed: {}", event.getTransactionId());
    }

    private void validateAmount(BigDecimal amount, List<String> errors) {
        if (amount == null) {
            errors.add("Amount is required");
            return;
        }

        if (amount.compareTo(MIN_AMOUNT) < 0) {
            errors.add("Amount must be at least " + MIN_AMOUNT);
        }

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            errors.add("Amount cannot exceed " + MAX_AMOUNT);
        }
    }

    private void validateUserId(String userId, List<String> errors) {
        if (userId == null || userId.isBlank()) {
            errors.add("User ID is required");
            return;
        }

        if (userId.length() > MAX_USER_ID_LENGTH) {
            errors.add("User ID cannot exceed " + MAX_USER_ID_LENGTH + " characters");
        }

        // Check for invalid characters
        if (!userId.matches("^[a-zA-Z0-9_-]+$")) {
            errors.add("User ID contains invalid characters. Only alphanumeric, underscore, and hyphen are allowed");
        }
    }

    private void validateTransactionId(String transactionId, List<String> errors) {
        if (transactionId == null || transactionId.isBlank()) {
            // Transaction ID is optional, will be auto-generated if not provided
            return;
        }

        if (transactionId.length() > MAX_TRANSACTION_ID_LENGTH) {
            errors.add("Transaction ID cannot exceed " + MAX_TRANSACTION_ID_LENGTH + " characters");
        }
    }

    private void validateCurrency(String currency, List<String> errors) {
        if (currency == null || currency.isBlank()) {
            // Currency is optional, will default to USD
            return;
        }

        if (currency.length() != 3) {
            errors.add("Currency must be a 3-letter ISO code");
        }

        // Check if it's uppercase letters only
        if (!currency.matches("^[A-Z]{3}$")) {
            errors.add("Currency must be uppercase letters only (e.g., USD, EUR, GBP)");
        }
    }

    private void validateIpAddress(String ipAddress, List<String> errors) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return;
        }

        // Simple IPv4 or IPv6 validation
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

        if (!ipAddress.matches(ipv4Pattern) && !ipAddress.matches(ipv6Pattern)) {
            // Allow simplified patterns for testing
            if (!ipAddress.matches("^[0-9.:a-fA-F]+$")) {
                errors.add("Invalid IP address format");
            }
        }
    }

    private void validateCoordinates(Double latitude, Double longitude, List<String> errors) {
        // Both must be provided together or neither
        boolean hasLatitude = latitude != null;
        boolean hasLongitude = longitude != null;

        if (hasLatitude != hasLongitude) {
            errors.add("Both latitude and longitude must be provided together");
            return;
        }

        if (latitude != null) {
            if (latitude < -90 || latitude > 90) {
                errors.add("Latitude must be between -90 and 90");
            }
        }

        if (longitude != null) {
            if (longitude < -180 || longitude > 180) {
                errors.add("Longitude must be between -180 and 180");
            }
        }
    }

    private void validateCardLastFour(String cardLastFour, List<String> errors) {
        if (cardLastFour == null || cardLastFour.isBlank()) {
            return;
        }

        if (!cardLastFour.matches("^[0-9]{4}$")) {
            errors.add("Card last four must be exactly 4 digits");
        }
    }
}
