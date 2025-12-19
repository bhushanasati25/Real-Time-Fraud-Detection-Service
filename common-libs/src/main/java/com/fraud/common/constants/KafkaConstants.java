package com.fraud.common.constants;

/**
 * Kafka topic names and configuration constants.
 */
public final class KafkaConstants {

    private KafkaConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // ============================================
    // TOPIC NAMES
    // ============================================

    /**
     * Topic for incoming transaction events
     */
    public static final String TOPIC_TRANSACTION_EVENTS = "transaction-events";

    /**
     * Topic for fraud alerts
     */
    public static final String TOPIC_FRAUD_ALERTS = "fraud-alerts";

    /**
     * Topic for processed transactions (non-fraud)
     */
    public static final String TOPIC_PROCESSED_TRANSACTIONS = "processed-transactions";

    /**
     * Dead letter topic for failed messages
     */
    public static final String TOPIC_DLQ = "fraud-detection-dlq";

    // ============================================
    // CONSUMER GROUPS
    // ============================================

    /**
     * Consumer group for fraud engine
     */
    public static final String GROUP_FRAUD_ENGINE = "fraud-engine-group";

    /**
     * Consumer group for notification service
     */
    public static final String GROUP_NOTIFICATION = "notification-service-group";

    // ============================================
    // MESSAGE HEADERS
    // ============================================

    /**
     * Header for correlation/trace ID
     */
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    /**
     * Header for message timestamp
     */
    public static final String HEADER_TIMESTAMP = "X-Timestamp";

    /**
     * Header for source service
     */
    public static final String HEADER_SOURCE_SERVICE = "X-Source-Service";
}
