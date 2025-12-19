package com.fraud.ingestion.service;

import com.fraud.common.dto.TransactionEvent;
import com.fraud.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Ingestion Service.
 * 
 * Handles transaction validation, enrichment, and publishing
 * to the fraud detection pipeline.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionIngestionService {

    private final KafkaProducerService kafkaProducerService;
    private final TransactionValidatorService validatorService;

    /**
     * Process and ingest a transaction.
     *
     * @param event The transaction event to ingest
     * @return The enriched transaction event
     */
    public TransactionEvent ingestTransaction(TransactionEvent event) {
        log.info("Ingesting transaction: {}", event.getTransactionId());

        // Validate the transaction
        validatorService.validateTransaction(event);

        // Enrich the transaction with additional data
        enrichTransaction(event);

        // Publish to Kafka
        kafkaProducerService.publishTransaction(event);

        log.info("Transaction {} successfully ingested", event.getTransactionId());
        return event;
    }

    /**
     * Process and ingest a batch of transactions.
     *
     * @param events List of transaction events
     * @return List of successfully ingested transactions
     */
    public List<TransactionEvent> ingestBatch(List<TransactionEvent> events) {
        log.info("Ingesting batch of {} transactions", events.size());

        List<TransactionEvent> successfulEvents = new ArrayList<>();
        List<String> failedTransactions = new ArrayList<>();

        for (TransactionEvent event : events) {
            try {
                TransactionEvent ingested = ingestTransaction(event);
                successfulEvents.add(ingested);
            } catch (Exception e) {
                log.error("Failed to ingest transaction {}: {}", 
                        event.getTransactionId(), e.getMessage());
                failedTransactions.add(event.getTransactionId());
            }
        }

        if (!failedTransactions.isEmpty()) {
            log.warn("Batch ingestion completed with {} failures: {}", 
                    failedTransactions.size(), failedTransactions);
        }

        log.info("Batch ingestion completed: {} successful, {} failed", 
                successfulEvents.size(), failedTransactions.size());
        return successfulEvents;
    }

    /**
     * Process and ingest a transaction asynchronously.
     *
     * @param event The transaction event to ingest
     */
    public void ingestTransactionAsync(TransactionEvent event) {
        log.debug("Async ingesting transaction: {}", event.getTransactionId());

        // Validate the transaction
        validatorService.validateTransaction(event);

        // Enrich the transaction
        enrichTransaction(event);

        // Publish asynchronously
        kafkaProducerService.publishTransactionAsync(event);
    }

    /**
     * Enrich the transaction with additional data.
     *
     * @param event The transaction to enrich
     */
    private void enrichTransaction(TransactionEvent event) {
        // Auto-generate transaction ID if not provided
        if (event.getTransactionId() == null || event.getTransactionId().isBlank()) {
            event.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        }

        // Set timestamp if not provided
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }

        // Set default currency if not provided
        if (event.getCurrency() == null || event.getCurrency().isBlank()) {
            event.setCurrency("USD");
        }

        // Set default transaction type if not provided
        if (event.getTransactionType() == null || event.getTransactionType().isBlank()) {
            event.setTransactionType("PURCHASE");
        }

        // Set default channel if not provided
        if (event.getChannel() == null || event.getChannel().isBlank()) {
            event.setChannel("ONLINE");
        }

        // Set received timestamp
        event.setReceivedAt(Instant.now());

        log.debug("Transaction {} enriched with defaults", event.getTransactionId());
    }
}
