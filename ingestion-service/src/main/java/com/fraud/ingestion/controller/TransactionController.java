package com.fraud.ingestion.controller;

import com.fraud.common.dto.ApiResponse;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.ingestion.service.TransactionIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Transaction Controller.
 * 
 * REST API endpoints for transaction ingestion into the
 * fraud detection pipeline.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction ingestion endpoints")
public class TransactionController {

    private final TransactionIngestionService ingestionService;

    /**
     * Ingest a single transaction.
     *
     * @param event The transaction event
     * @return Response with ingested transaction
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Ingest a transaction",
            description = "Submit a financial transaction for fraud detection analysis. " +
                    "The transaction will be validated and published to the fraud detection pipeline."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "202",
                    description = "Transaction accepted for processing",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid transaction data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<TransactionEvent>> ingestTransaction(
            @Valid @RequestBody TransactionEvent event) {
        
        log.info("Received transaction ingestion request: {}", 
                event.getTransactionId() != null ? event.getTransactionId() : "NEW");

        TransactionEvent ingestedEvent = ingestionService.ingestTransaction(event);

        ApiResponse<TransactionEvent> response = ApiResponse.accepted(
                ingestedEvent,
                "Transaction accepted for fraud detection processing"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Ingest multiple transactions in batch.
     *
     * @param events List of transaction events
     * @return Response with ingested transactions
     */
    @PostMapping(
            path = "/batch",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Ingest transactions in batch",
            description = "Submit multiple transactions for fraud detection analysis. " +
                    "Each transaction will be validated and published independently."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "202",
                    description = "Batch accepted for processing",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid batch data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> ingestBatch(
            @Valid @RequestBody List<TransactionEvent> events) {
        
        log.info("Received batch ingestion request with {} transactions", events.size());

        List<TransactionEvent> ingestedEvents = ingestionService.ingestBatch(events);

        Map<String, Object> responseData = Map.of(
                "totalReceived", events.size(),
                "successfullyIngested", ingestedEvents.size(),
                "transactions", ingestedEvents
        );

        ApiResponse<Map<String, Object>> response = ApiResponse.accepted(
                responseData,
                "Batch accepted for fraud detection processing"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Ingest a transaction asynchronously (fire and forget).
     *
     * @param event The transaction event
     * @return Acknowledgment response
     */
    @PostMapping(
            path = "/async",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Ingest a transaction asynchronously",
            description = "Submit a transaction for async processing. " +
                    "Returns immediately after validation without waiting for Kafka acknowledgment."
    )
    public ResponseEntity<ApiResponse<Map<String, String>>> ingestTransactionAsync(
            @Valid @RequestBody TransactionEvent event) {
        
        log.info("Received async transaction ingestion request: {}", 
                event.getTransactionId() != null ? event.getTransactionId() : "NEW");

        ingestionService.ingestTransactionAsync(event);

        Map<String, String> responseData = Map.of(
                "transactionId", event.getTransactionId(),
                "status", "ACCEPTED"
        );

        ApiResponse<Map<String, String>> response = ApiResponse.accepted(
                responseData,
                "Transaction accepted for async processing"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Health check endpoint for the ingestion service.
     *
     * @return Health status
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the ingestion service is healthy")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ingestion-service"
        ));
    }
}
