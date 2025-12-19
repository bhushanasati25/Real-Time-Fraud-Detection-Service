package com.fraud.ingestion.service;

import com.fraud.common.constants.KafkaConstants;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.common.exception.MessagePublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service.
 * 
 * Handles publishing transaction events to Kafka with proper
 * error handling and logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    /**
     * Publish a transaction event to Kafka synchronously.
     *
     * @param event The transaction event to publish
     * @return The send result
     * @throws MessagePublishException if publishing fails
     */
    public SendResult<String, TransactionEvent> publishTransaction(TransactionEvent event) {
        try {
            // Set received timestamp if not already set
            if (event.getReceivedAt() == null) {
                event.setReceivedAt(Instant.now());
            }

            // Set source system
            event.setSourceSystem("ingestion-service");

            String key = event.getUserId() != null ? event.getUserId() : UUID.randomUUID().toString();
            
            log.info("Publishing transaction {} to Kafka topic {}", 
                    event.getTransactionId(), KafkaConstants.TOPIC_TRANSACTION_EVENTS);

            CompletableFuture<SendResult<String, TransactionEvent>> future = 
                    kafkaTemplate.send(KafkaConstants.TOPIC_TRANSACTION_EVENTS, key, event);

            SendResult<String, TransactionEvent> result = future.get();
            
            log.info("Successfully published transaction {} to partition {} at offset {}", 
                    event.getTransactionId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            return result;

        } catch (Exception e) {
            log.error("Failed to publish transaction {} to Kafka: {}", 
                    event.getTransactionId(), e.getMessage(), e);
            throw new MessagePublishException(
                    KafkaConstants.TOPIC_TRANSACTION_EVENTS,
                    "Failed to publish transaction: " + event.getTransactionId(),
                    e);
        }
    }

    /**
     * Publish a transaction event to Kafka asynchronously.
     *
     * @param event The transaction event to publish
     * @return CompletableFuture with the send result
     */
    @Async
    public CompletableFuture<SendResult<String, TransactionEvent>> publishTransactionAsync(TransactionEvent event) {
        try {
            // Set received timestamp if not already set
            if (event.getReceivedAt() == null) {
                event.setReceivedAt(Instant.now());
            }

            // Set source system
            event.setSourceSystem("ingestion-service");

            String key = event.getUserId() != null ? event.getUserId() : UUID.randomUUID().toString();

            log.debug("Publishing transaction {} to Kafka topic {} asynchronously", 
                    event.getTransactionId(), KafkaConstants.TOPIC_TRANSACTION_EVENTS);

            return kafkaTemplate.send(KafkaConstants.TOPIC_TRANSACTION_EVENTS, key, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Async publish failed for transaction {}: {}", 
                                    event.getTransactionId(), ex.getMessage());
                        } else {
                            log.info("Async publish successful for transaction {} to partition {} at offset {}", 
                                    event.getTransactionId(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });

        } catch (Exception e) {
            log.error("Failed to initiate async publish for transaction {}: {}", 
                    event.getTransactionId(), e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Publish a transaction event with a specific key.
     *
     * @param key   The message key (used for partitioning)
     * @param event The transaction event to publish
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, TransactionEvent>> publishWithKey(
            String key, TransactionEvent event) {
        
        if (event.getReceivedAt() == null) {
            event.setReceivedAt(Instant.now());
        }
        event.setSourceSystem("ingestion-service");

        log.debug("Publishing transaction {} with key {} to Kafka", 
                event.getTransactionId(), key);

        return kafkaTemplate.send(KafkaConstants.TOPIC_TRANSACTION_EVENTS, key, event);
    }
}
