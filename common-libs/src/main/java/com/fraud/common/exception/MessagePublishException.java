package com.fraud.common.exception;

/**
 * Exception thrown when Kafka message publishing fails.
 */
public class MessagePublishException extends FraudDetectionException {

    private final String topic;

    public MessagePublishException(String topic, String message) {
        super("MESSAGE_PUBLISH_ERROR", message);
        this.topic = topic;
    }

    public MessagePublishException(String topic, String message, Throwable cause) {
        super("MESSAGE_PUBLISH_ERROR", message, cause);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
