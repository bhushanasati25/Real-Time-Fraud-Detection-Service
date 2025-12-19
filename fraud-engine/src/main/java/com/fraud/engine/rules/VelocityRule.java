package com.fraud.engine.rules;

import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.engine.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Velocity Rule - Flags users with unusual transaction frequency.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VelocityRule implements Rule {

    private final TransactionRepository transactionRepository;

    @Value("${fraud.rules.velocity.max-transactions-per-hour:10}")
    private int maxTransactionsPerHour;

    @Value("${fraud.rules.velocity.max-amount-24h:25000}")
    private BigDecimal maxAmount24h;

    @Override
    public String getRuleId() {
        return "RULE_003";
    }

    @Override
    public String getRuleName() {
        return "Velocity Check";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public FraudResult.RuleEvaluation evaluate(TransactionEvent event, FraudResult.FraudResultBuilder resultBuilder) {
        String userId = event.getUserId();
        boolean triggered = false;
        BigDecimal score = BigDecimal.ZERO;
        StringBuilder messageBuilder = new StringBuilder();

        if (userId == null || userId.isBlank()) {
            return buildEvaluation(false, BigDecimal.ZERO, "User ID not provided");
        }

        try {
            // Check transaction count in last hour
            Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
            Long transactionCount = transactionRepository.countTransactionsByUserSince(userId, oneHourAgo);

            if (transactionCount >= maxTransactionsPerHour) {
                triggered = true;
                score = new BigDecimal("0.6");
                messageBuilder.append(String.format("Velocity breach: %d transactions in last hour (max: %d). ",
                        transactionCount, maxTransactionsPerHour));
                log.warn("Rule {} triggered: User {} has {} transactions in last hour",
                        getRuleId(), userId, transactionCount);
            }

            // Check total amount in last 24 hours
            Instant twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS);
            BigDecimal totalAmount = transactionRepository.sumAmountByUserSince(userId, twentyFourHoursAgo);

            if (totalAmount != null && totalAmount.add(event.getAmount()).compareTo(maxAmount24h) > 0) {
                triggered = true;
                score = score.max(new BigDecimal("0.5"));
                messageBuilder.append(String.format("24h amount breach: $%s + $%s exceeds max $%s. ",
                        totalAmount, event.getAmount(), maxAmount24h));
                log.warn("Rule {} triggered: User {} 24h total ${} exceeds threshold",
                        getRuleId(), userId, totalAmount.add(event.getAmount()));
            }

            if (!triggered) {
                messageBuilder.append("Transaction velocity within normal limits");
            }

        } catch (Exception e) {
            log.error("Error evaluating velocity rule for user {}: {}", userId, e.getMessage());
            messageBuilder.append("Unable to evaluate velocity - proceeding with caution");
        }

        return buildEvaluation(triggered, score, messageBuilder.toString().trim());
    }

    private FraudResult.RuleEvaluation buildEvaluation(boolean triggered, BigDecimal score, String message) {
        return FraudResult.RuleEvaluation.builder()
                .ruleId(getRuleId())
                .ruleName(getRuleName())
                .triggered(triggered)
                .score(score)
                .message(message)
                .build();
    }
}
