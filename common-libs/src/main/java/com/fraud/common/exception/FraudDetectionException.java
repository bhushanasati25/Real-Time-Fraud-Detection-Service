package com.fraud.common.exception;

/**
 * Base exception for the fraud detection system.
 * 
 * All custom exceptions in the system should extend this class.
 */
public class FraudDetectionException extends RuntimeException {

    private final String errorCode;

    public FraudDetectionException(String message) {
        super(message);
        this.errorCode = "FRAUD_ERROR";
    }

    public FraudDetectionException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FraudDetectionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FRAUD_ERROR";
    }

    public FraudDetectionException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
