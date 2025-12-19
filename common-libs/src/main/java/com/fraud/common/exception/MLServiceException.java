package com.fraud.common.exception;

/**
 * Exception thrown when ML model service is unavailable or returns an error.
 */
public class MLServiceException extends FraudDetectionException {

    public MLServiceException(String message) {
        super("ML_SERVICE_ERROR", message);
    }

    public MLServiceException(String message, Throwable cause) {
        super("ML_SERVICE_ERROR", message, cause);
    }
}
