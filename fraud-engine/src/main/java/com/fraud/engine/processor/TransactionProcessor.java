package com.fraud.engine.processor;

import com.fraud.common.constants.KafkaConstants;
import com.fraud.common.dto.*;
import com.fraud.engine.entity.Transaction;
import com.fraud.engine.entity.UserProfile;
import com.fraud.engine.model.MLModelClient;
import com.fraud.engine.repository.TransactionRepository;
import com.fraud.engine.repository.UserProfileRepository;
import com.fraud.engine.rules.RuleChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Processor - Core processing logic for fraud detection.
 * 
 * Orchestrates the complete fraud detection pipeline:
 * 1. Apply rule-based checks
 * 2. Get ML model score
 * 3. Combine results and make decision
 * 4. Persist transaction and publish alerts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessor {

    private final RuleChain ruleChain;
    private final MLModelClient mlModelClient;
    private final TransactionRepository transactionRepository;
    private final UserProfileRepository userProfileRepository;
    private final KafkaTemplate<String, FraudAlert> alertKafkaTemplate;

    private static final BigDecimal ML_WEIGHT = new BigDecimal("0.4");
    private static final BigDecimal RULES_WEIGHT = new BigDecimal("0.6");

    /**
     * Process a transaction through the fraud detection pipeline.
     *
     * @param event The transaction event to process
     * @return FraudResult with the detection outcome
     */
    @Transactional
    public FraudResult processTransaction(TransactionEvent event) {
        long startTime = System.currentTimeMillis();
        
        log.info("Processing transaction: {}", event.getTransactionId());

        try {
            // Step 1: Execute rule-based checks
            FraudResult ruleResult = ruleChain.executeRules(event);

            // Step 2: Get ML model score
            MLScoreResponse mlScore = mlModelClient.getScore(event);

            // Step 3: Combine results
            FraudResult finalResult = combineResults(event, ruleResult, mlScore, startTime);

            // Step 4: Persist transaction
            Transaction transaction = persistTransaction(event, finalResult);

            // Step 5: Update user profile
            updateUserProfile(event);

            // Step 6: Publish alert if fraud detected
            if (finalResult.isFraud()) {
                publishFraudAlert(event, finalResult);
            }

            log.info("Transaction {} processed: fraud={}, score={}, time={}ms",
                    event.getTransactionId(),
                    finalResult.isFraud(),
                    finalResult.getFraudScore(),
                    finalResult.getProcessingTimeMs());

            return finalResult;

        } catch (Exception e) {
            log.error("Error processing transaction {}: {}", event.getTransactionId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Combine rule-based and ML results.
     */
    private FraudResult combineResults(TransactionEvent event, FraudResult ruleResult,
                                        MLScoreResponse mlScore, long startTime) {
        
        BigDecimal ruleScore = ruleResult.getFraudScore();
        BigDecimal mlProbability = mlScore.getFraudProbability() != null 
                ? mlScore.getFraudProbability() : BigDecimal.ZERO;

        // Weighted combination
        BigDecimal combinedScore = ruleScore.multiply(RULES_WEIGHT)
                .add(mlProbability.multiply(ML_WEIGHT));

        // Determine if fraud (using 0.5 threshold)
        boolean isFraud = combinedScore.compareTo(new BigDecimal("0.5")) >= 0 
                || ruleResult.isFraud() 
                || (mlScore.getIsFraud() != null && mlScore.getIsFraud());

        // Determine risk level
        FraudResult.RiskLevel riskLevel = determineRiskLevel(combinedScore);

        // Create ML prediction summary
        FraudResult.MLPrediction mlPrediction = FraudResult.MLPrediction.builder()
                .modelName(mlScore.getModelName())
                .modelVersion(mlScore.getModelVersion())
                .probability(mlProbability)
                .prediction(mlScore.getPrediction())
                .confidence(mlScore.getConfidence())
                .build();

        return FraudResult.builder()
                .transactionId(event.getTransactionId())
                .isFraud(isFraud)
                .fraudScore(combinedScore)
                .riskLevel(riskLevel)
                .triggeredRules(ruleResult.getTriggeredRules())
                .ruleDetails(ruleResult.getRuleDetails())
                .mlPrediction(mlPrediction)
                .reason(buildReason(ruleResult, mlScore, isFraud))
                .recommendedAction(determineAction(isFraud, riskLevel))
                .processingTimeMs(System.currentTimeMillis() - startTime)
                .analyzedAt(Instant.now())
                .build();
    }

    private FraudResult.RiskLevel determineRiskLevel(BigDecimal score) {
        if (score.compareTo(new BigDecimal("0.8")) >= 0) {
            return FraudResult.RiskLevel.CRITICAL;
        } else if (score.compareTo(new BigDecimal("0.6")) >= 0) {
            return FraudResult.RiskLevel.HIGH;
        } else if (score.compareTo(new BigDecimal("0.3")) >= 0) {
            return FraudResult.RiskLevel.MEDIUM;
        } else {
            return FraudResult.RiskLevel.LOW;
        }
    }

    private FraudResult.RecommendedAction determineAction(boolean isFraud, FraudResult.RiskLevel riskLevel) {
        if (isFraud) {
            return switch (riskLevel) {
                case CRITICAL -> FraudResult.RecommendedAction.BLOCK_USER;
                case HIGH -> FraudResult.RecommendedAction.DECLINE;
                default -> FraudResult.RecommendedAction.REVIEW;
            };
        }
        return riskLevel == FraudResult.RiskLevel.MEDIUM 
                ? FraudResult.RecommendedAction.REVIEW 
                : FraudResult.RecommendedAction.APPROVE;
    }

    private String buildReason(FraudResult ruleResult, MLScoreResponse mlScore, boolean isFraud) {
        StringBuilder reason = new StringBuilder();
        
        if (isFraud) {
            reason.append("FRAUD DETECTED: ");
        } else {
            reason.append("Transaction analyzed: ");
        }

        if (!ruleResult.getTriggeredRules().isEmpty()) {
            reason.append("Rules triggered: ").append(String.join(", ", ruleResult.getTriggeredRules())).append(". ");
        }

        if (mlScore.getFraudProbability() != null && mlScore.getFraudProbability().compareTo(new BigDecimal("0.3")) > 0) {
            reason.append("ML model flagged with ").append(mlScore.getFraudProbability()).append(" probability.");
        }

        return reason.toString().trim();
    }

    /**
     * Persist transaction to database.
     */
    private Transaction persistTransaction(TransactionEvent event, FraudResult result) {
        Transaction transaction = Transaction.builder()
                .transactionId(event.getTransactionId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .userId(event.getUserId())
                .merchantId(event.getMerchantId())
                .merchantName(event.getMerchantName())
                .merchantCategory(event.getMerchantCategory())
                .location(event.getLocation())
                .ipAddress(event.getIpAddress())
                .deviceId(event.getDeviceId())
                .cardType(event.getCardType())
                .cardLastFour(event.getCardLastFour())
                .transactionType(event.getTransactionType())
                .channel(event.getChannel())
                .status(result.isFraud() ? "FLAGGED" : "APPROVED")
                .isFraud(result.isFraud())
                .fraudScore(result.getFraudScore())
                .fraudReason(result.getReason())
                .rulesTriggered(result.getTriggeredRules().toArray(new String[0]))
                .processingTimeMs(result.getProcessingTimeMs().intValue())
                .processedAt(Instant.now())
                .build();

        return transactionRepository.save(transaction);
    }

    /**
     * Update user profile with latest transaction data.
     */
    private void updateUserProfile(TransactionEvent event) {
        UserProfile profile = userProfileRepository.findByUserId(event.getUserId())
                .orElseGet(() -> UserProfile.builder()
                        .userId(event.getUserId())
                        .transactionCount24h(0)
                        .totalAmount24h(BigDecimal.ZERO)
                        .riskScore(BigDecimal.ZERO)
                        .build());

        profile.setLastKnownIp(event.getIpAddress());
        profile.setLastKnownLocation(event.getLocation());
        profile.setLastTransactionAt(Instant.now());
        profile.setTransactionCount24h(profile.getTransactionCount24h() + 1);
        profile.setTotalAmount24h(profile.getTotalAmount24h().add(event.getAmount()));

        userProfileRepository.save(profile);
    }

    /**
     * Publish fraud alert to notification service.
     */
    private void publishFraudAlert(TransactionEvent event, FraudResult result) {
        FraudAlert alert = FraudAlert.builder()
                .alertId("ALERT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .transactionId(event.getTransactionId())
                .userId(event.getUserId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .alertType(determineAlertType(result))
                .severity(mapSeverity(result.getRiskLevel()))
                .fraudScore(result.getFraudScore())
                .triggeredRules(result.getTriggeredRules())
                .description(result.getReason())
                .recommendedAction(result.getRecommendedAction().name())
                .location(event.getLocation())
                .ipAddress(event.getIpAddress())
                .merchantName(event.getMerchantName())
                .transactionTimestamp(event.getTimestamp())
                .alertTimestamp(Instant.now())
                .status(FraudAlert.AlertStatus.OPEN)
                .notificationChannels(List.of(
                        FraudAlert.NotificationChannel.EMAIL,
                        FraudAlert.NotificationChannel.WEBHOOK
                ))
                .build();

        alertKafkaTemplate.send(KafkaConstants.TOPIC_FRAUD_ALERTS, event.getUserId(), alert);
        log.info("Published fraud alert {} for transaction {}", alert.getAlertId(), event.getTransactionId());
    }

    private FraudAlert.AlertType determineAlertType(FraudResult result) {
        if (result.getMlPrediction() != null && 
            result.getMlPrediction().getProbability().compareTo(new BigDecimal("0.5")) > 0) {
            return FraudAlert.AlertType.ML_DETECTION;
        }
        if (!result.getTriggeredRules().isEmpty()) {
            if (result.getTriggeredRules().contains("RULE_001")) {
                return FraudAlert.AlertType.HIGH_AMOUNT;
            }
            if (result.getTriggeredRules().contains("RULE_003")) {
                return FraudAlert.AlertType.VELOCITY_BREACH;
            }
            if (result.getTriggeredRules().contains("RULE_005")) {
                return FraudAlert.AlertType.LOCATION_ANOMALY;
            }
        }
        return FraudAlert.AlertType.RULE_BASED;
    }

    private FraudAlert.Severity mapSeverity(FraudResult.RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CRITICAL -> FraudAlert.Severity.CRITICAL;
            case HIGH -> FraudAlert.Severity.HIGH;
            case MEDIUM -> FraudAlert.Severity.MEDIUM;
            default -> FraudAlert.Severity.LOW;
        };
    }
}
