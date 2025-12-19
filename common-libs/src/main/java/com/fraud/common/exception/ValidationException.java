package com.fraud.common.exception;

import java.util.List;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends FraudDetectionException {

    private final List<String> validationErrors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = List.of(message);
    }

    public ValidationException(String message, List<String> errors) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = errors != null ? errors : List.of();
    }

    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
        this.validationErrors = List.of(message);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
