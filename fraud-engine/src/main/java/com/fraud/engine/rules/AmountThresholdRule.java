package com.fraud.engine.rules;

import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Amount Threshold Rule - Flags transactions exceeding threshold amounts.
 */
@Component
@Slf4j
public class AmountThresholdRule implements Rule {

    @Value("${fraud.rules.amount.high-threshold:10000}")
    private BigDecimal highThreshold;

    @Value("${fraud.rules.amount.critical-threshold:50000}")
    private BigDecimal criticalThreshold;

    @Override
    public String getRuleId() {
        return "RULE_001";
    }

    @Override
    public String getRuleName() {
        return "Amount Threshold Check";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public FraudResult.RuleEvaluation evaluate(TransactionEvent event, FraudResult.FraudResultBuilder resultBuilder) {
        BigDecimal amount = event.getAmount();
        boolean triggered = false;
        BigDecimal score = BigDecimal.ZERO;
        String message = "Transaction amount within normal range";

        if (amount == null) {
            return buildEvaluation(false, BigDecimal.ZERO, "Amount not provided");
        }

        if (amount.compareTo(criticalThreshold) >= 0) {
            triggered = true;
            score = new BigDecimal("0.8");
            message = String.format("CRITICAL: Transaction amount $%s exceeds critical threshold of $%s",
                    amount, criticalThreshold);
            log.warn("Rule {} triggered: {}", getRuleId(), message);
        } else if (amount.compareTo(highThreshold) >= 0) {
            triggered = true;
            score = new BigDecimal("0.5");
            message = String.format("HIGH: Transaction amount $%s exceeds threshold of $%s",
                    amount, highThreshold);
            log.info("Rule {} triggered: {}", getRuleId(), message);
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
