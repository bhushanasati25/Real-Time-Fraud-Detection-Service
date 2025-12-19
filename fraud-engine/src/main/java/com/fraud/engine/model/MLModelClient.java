package com.fraud.engine.model;

import com.fraud.common.dto.MLScoreRequest;
import com.fraud.common.dto.MLScoreResponse;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.common.exception.MLServiceException;
import com.fraud.common.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

/**
 * ML Model Client - Client for calling the ML scoring service.
 * 
 * Handles communication with the Python ML service for
 * fraud probability scoring.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MLModelClient {

    private final WebClient mlServiceWebClient;

    @Value("${ml.service.timeout:5000}")
    private int timeout;

    @Value("${ml.service.enabled:true}")
    private boolean enabled;

    /**
     * Get fraud score from ML model.
     *
     * @param event The transaction to score
     * @return ML score response
     */
    public MLScoreResponse getScore(TransactionEvent event) {
        if (!enabled) {
            log.debug("ML service disabled, returning default score");
            return getDefaultScore(event.getTransactionId());
        }

        try {
            MLScoreRequest request = buildRequest(event);

            log.debug("Calling ML service for transaction {}", event.getTransactionId());

            MLScoreResponse response = mlServiceWebClient
                    .post()
                    .uri("/predict")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MLScoreResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            if (response != null) {
                log.info("ML score for transaction {}: probability={}, prediction={}",
                        event.getTransactionId(), response.getFraudProbability(), response.getPrediction());
                return response;
            } else {
                log.warn("Null response from ML service for transaction {}", event.getTransactionId());
                return getDefaultScore(event.getTransactionId());
            }

        } catch (WebClientResponseException e) {
            log.error("ML service error for transaction {}: {} - {}",
                    event.getTransactionId(), e.getStatusCode(), e.getMessage());
            throw new MLServiceException("ML service returned error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Error calling ML service for transaction {}: {}",
                    event.getTransactionId(), e.getMessage());
            // Return default score on error to not block the pipeline
            return getDefaultScore(event.getTransactionId());
        }
    }

    /**
     * Get fraud score asynchronously.
     *
     * @param event The transaction to score
     * @return Mono with ML score response
     */
    public Mono<MLScoreResponse> getScoreAsync(TransactionEvent event) {
        if (!enabled) {
            return Mono.just(getDefaultScore(event.getTransactionId()));
        }

        MLScoreRequest request = buildRequest(event);

        return mlServiceWebClient
                .post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MLScoreResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .onErrorResume(e -> {
                    log.error("Async ML service error for transaction {}: {}",
                            event.getTransactionId(), e.getMessage());
                    return Mono.just(getDefaultScore(event.getTransactionId()));
                });
    }

    /**
     * Build ML score request from transaction event.
     */
    private MLScoreRequest buildRequest(TransactionEvent event) {
        Instant timestamp = event.getTimestamp() != null ? event.getTimestamp() : Instant.now();

        return MLScoreRequest.builder()
                .transactionId(event.getTransactionId())
                .amount(event.getAmount())
                .userId(event.getUserId())
                .merchantId(event.getMerchantId())
                .merchantCategory(event.getMerchantCategory())
                .transactionType(event.getTransactionType())
                .channel(event.getChannel())
                .hourOfDay(DateTimeUtils.getHourOfDay(timestamp))
                .dayOfWeek(DateTimeUtils.getDayOfWeek(timestamp))
                .isWeekend(DateTimeUtils.isWeekend(timestamp))
                .isNightTime(DateTimeUtils.isNightTime(timestamp))
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .build();
    }

    /**
     * Get default score when ML service is unavailable.
     */
    private MLScoreResponse getDefaultScore(String transactionId) {
        return MLScoreResponse.builder()
                .transactionId(transactionId)
                .fraudProbability(new BigDecimal("0.0"))
                .prediction("LEGITIMATE")
                .isFraud(false)
                .confidence(new BigDecimal("0.0"))
                .modelName("default")
                .modelVersion("0.0.0")
                .processingTimeMs(0L)
                .build();
    }
}
