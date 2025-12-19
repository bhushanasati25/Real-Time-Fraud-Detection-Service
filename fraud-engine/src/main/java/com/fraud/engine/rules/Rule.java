package com.fraud.engine.rules;

import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;

/**
 * Rule Interface - Contract for fraud detection rules.
 * 
 * Implements the Chain of Responsibility pattern where each rule
 * evaluates a transaction and contributes to the fraud score.
 */
public interface Rule {

    /**
     * Get the unique identifier of this rule.
     *
     * @return Rule ID
     */
    String getRuleId();

    /**
     * Get the human-readable name of this rule.
     *
     * @return Rule name
     */
    String getRuleName();

    /**
     * Get the priority of this rule (lower = higher priority).
     *
     * @return Priority value
     */
    int getPriority();

    /**
     * Check if this rule is enabled.
     *
     * @return true if the rule should be evaluated
     */
    boolean isEnabled();

    /**
     * Evaluate the transaction against this rule.
     *
     * @param event         The transaction to evaluate
     * @param resultBuilder The result builder to accumulate findings
     * @return RuleEvaluation with the result of this rule check
     */
    FraudResult.RuleEvaluation evaluate(TransactionEvent event, FraudResult.FraudResultBuilder resultBuilder);
}
