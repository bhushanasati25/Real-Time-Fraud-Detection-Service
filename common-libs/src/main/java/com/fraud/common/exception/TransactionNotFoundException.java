package com.fraud.common.exception;

/**
 * Exception thrown when a transaction is not found.
 */
public class TransactionNotFoundException extends FraudDetectionException {

    private final String transactionId;

    public TransactionNotFoundException(String transactionId) {
        super("TRANSACTION_NOT_FOUND", "Transaction not found: " + transactionId);
        this.transactionId = transactionId;
    }

    public TransactionNotFoundException(String transactionId, Throwable cause) {
        super("TRANSACTION_NOT_FOUND", "Transaction not found: " + transactionId, cause);
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
