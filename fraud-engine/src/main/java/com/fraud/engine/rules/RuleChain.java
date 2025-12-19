package com.fraud.engine.rules;

import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Rule Chain - Orchestrates the execution of all fraud detection rules.
 * 
 * Implements the Chain of Responsibility pattern by executing rules
 * in priority order and aggregating their results.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RuleChain {

    private final List<Rule> rules;

    private static final BigDecimal FRAUD_THRESHOLD = new BigDecimal("0.5");

    /**
     * Execute all rules against a transaction.
     *
     * @param event The transaction to evaluate
     * @return FraudResult with aggregated findings
     */
    public FraudResult executeRules(TransactionEvent event) {
        long startTime = System.currentTimeMillis();

        log.info("Starting rule evaluation for transaction {}", event.getTransactionId());

        FraudResult.FraudResultBuilder resultBuilder = FraudResult.builder()
                .transactionId(event.getTransactionId());

        List<FraudResult.RuleEvaluation> evaluations = new ArrayList<>();
        List<String> triggeredRules = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;

        // Sort rules by priority and execute
        List<Rule> sortedRules = rules.stream()
                .filter(Rule::isEnabled)
                .sorted(Comparator.comparingInt(Rule::getPriority))
                .toList();

        for (Rule rule : sortedRules) {
            try {
                log.debug("Executing rule {} ({}) for transaction {}",
                        rule.getRuleId(), rule.getRuleName(), event.getTransactionId());

                FraudResult.RuleEvaluation evaluation = rule.evaluate(event, resultBuilder);
                evaluations.add(evaluation);

                if (evaluation.isTriggered()) {
                    triggeredRules.add(rule.getRuleId());
                    totalScore = totalScore.add(evaluation.getScore());
                    log.debug("Rule {} triggered with score {}", rule.getRuleId(), evaluation.getScore());
                }

            } catch (Exception e) {
                log.error("Error executing rule {}: {}", rule.getRuleId(), e.getMessage(), e);
                // Continue with other rules
            }
        }

        // Normalize score to 0-1 range
        BigDecimal normalizedScore = normalizeScore(totalScore, sortedRules.size());

        // Determine if fraud based on threshold
        boolean isFraud = normalizedScore.compareTo(FRAUD_THRESHOLD) >= 0;

        // Determine risk level
        FraudResult.RiskLevel riskLevel = determineRiskLevel(normalizedScore);

        // Determine recommended action
        FraudResult.RecommendedAction action = determineAction(isFraud, riskLevel);

        // Build reason string
        String reason = buildReason(triggeredRules, isFraud);

        long processingTime = System.currentTimeMillis() - startTime;

        FraudResult result = resultBuilder
                .isFraud(isFraud)
                .fraudScore(normalizedScore)
                .riskLevel(riskLevel)
                .triggeredRules(triggeredRules)
                .ruleDetails(evaluations)
                .reason(reason)
                .recommendedAction(action)
                .processingTimeMs(processingTime)
                .analyzedAt(Instant.now())
                .build();

        log.info("Rule evaluation complete for transaction {}: fraud={}, score={}, rules={}, time={}ms",
                event.getTransactionId(), isFraud, normalizedScore, triggeredRules.size(), processingTime);

        return result;
    }

    private BigDecimal normalizeScore(BigDecimal totalScore, int ruleCount) {
        if (ruleCount == 0 || totalScore.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Cap at 1.0
        BigDecimal normalized = totalScore.min(BigDecimal.ONE);
        return normalized.setScale(4, RoundingMode.HALF_UP);
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
        } else {
            return switch (riskLevel) {
                case HIGH, MEDIUM -> FraudResult.RecommendedAction.REVIEW;
                default -> FraudResult.RecommendedAction.APPROVE;
            };
        }
    }

    private String buildReason(List<String> triggeredRules, boolean isFraud) {
        if (triggeredRules.isEmpty()) {
            return "No fraud indicators detected";
        }

        StringBuilder reason = new StringBuilder();
        if (isFraud) {
            reason.append("FRAUD DETECTED: ");
        } else {
            reason.append("SUSPICIOUS ACTIVITY: ");
        }
        reason.append("Triggered rules: ").append(String.join(", ", triggeredRules));
        return reason.toString();
    }
}
