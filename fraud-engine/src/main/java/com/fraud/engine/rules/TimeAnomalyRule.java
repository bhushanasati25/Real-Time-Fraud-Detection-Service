package com.fraud.engine.rules;

import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Time Anomaly Rule - Flags transactions during unusual hours.
 */
@Component
@Slf4j
public class TimeAnomalyRule implements Rule {

    @Override
    public String getRuleId() {
        return "RULE_007";
    }

    @Override
    public String getRuleName() {
        return "Time Anomaly Check";
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public FraudResult.RuleEvaluation evaluate(TransactionEvent event, FraudResult.FraudResultBuilder resultBuilder) {
        boolean triggered = false;
        BigDecimal score = BigDecimal.ZERO;
        String message = "Transaction time within normal hours";

        Instant timestamp = event.getTimestamp();
        if (timestamp == null) {
            timestamp = Instant.now();
        }

        try {
            // Check if transaction is during night time (1 AM - 5 AM)
            if (DateTimeUtils.isNightTime(timestamp)) {
                triggered = true;
                score = new BigDecimal("0.2");
                int hour = DateTimeUtils.getHourOfDay(timestamp);
                message = String.format("Transaction at unusual hour: %02d:00 UTC (1-5 AM window)", hour);
                log.info("Rule {} triggered: Night-time transaction at {} UTC", getRuleId(), hour);
            }

            // Additional flag for weekend+night combo (higher risk)
            if (triggered && DateTimeUtils.isWeekend(timestamp)) {
                score = score.add(new BigDecimal("0.1"));
                message += " on weekend";
                log.info("Rule {} increased risk: Weekend night-time transaction", getRuleId());
            }

        } catch (Exception e) {
            log.error("Error evaluating time rule: {}", e.getMessage());
            message = "Unable to evaluate transaction time";
        }

        return buildEvaluation(triggered, score, message);
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
