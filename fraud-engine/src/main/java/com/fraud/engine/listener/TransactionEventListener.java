package com.fraud.engine.listener;

import com.fraud.common.constants.KafkaConstants;
import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.engine.processor.TransactionProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Transaction Event Listener - Consumes transactions from Kafka.
 * 
 * Listens to the transaction-events topic and delegates processing
 * to the TransactionProcessor.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventListener {

    private final TransactionProcessor transactionProcessor;

    @KafkaListener(
            topics = KafkaConstants.TOPIC_TRANSACTION_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTransactionReceived(
            @Payload TransactionEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {
        
        log.info("Received transaction {} from partition {} at offset {} with key {}",
                event.getTransactionId(), partition, offset, key);

        try {
            // Process the transaction
            FraudResult result = transactionProcessor.processTransaction(event);

            // Acknowledge successful processing
            acknowledgment.acknowledge();

            log.info("Successfully processed transaction {}: fraud={}, score={}",
                    event.getTransactionId(), result.isFraud(), result.getFraudScore());

        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", 
                    event.getTransactionId(), e.getMessage(), e);
            
            // In a production system, you might:
            // 1. Send to a dead letter topic
            // 2. Retry with exponential backoff
            // 3. Alert operations team
            
            // For now, acknowledge to prevent infinite reprocessing
            // In production, implement proper error handling
            acknowledgment.acknowledge();
        }
    }
}
