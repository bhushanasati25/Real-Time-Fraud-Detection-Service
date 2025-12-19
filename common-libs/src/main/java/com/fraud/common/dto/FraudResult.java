package com.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * FraudResult DTO - Result of fraud detection analysis.
 * 
 * This object contains the outcome of running a transaction through
 * the fraud detection pipeline (rules + ML model).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Transaction ID being analyzed
     */
    @JsonProperty("transactionId")
    private String transactionId;

    /**
     * Whether the transaction is flagged as fraud
     */
    @JsonProperty("isFraud")
    private boolean isFraud;

    /**
     * Fraud probability score (0.0 to 1.0)
     */
    @JsonProperty("fraudScore")
    private BigDecimal fraudScore;

    /**
     * Risk level classification
     */
    @JsonProperty("riskLevel")
    private RiskLevel riskLevel;

    /**
     * List of rules that were triggered
     */
    @JsonProperty("triggeredRules")
    private List<String> triggeredRules;

    /**
     * Human-readable explanation of why this was flagged
     */
    @JsonProperty("reason")
    private String reason;

    /**
     * Detailed breakdown of rule evaluations
     */
    @JsonProperty("ruleDetails")
    private List<RuleEvaluation> ruleDetails;

    /**
     * ML model prediction details
     */
    @JsonProperty("mlPrediction")
    private MLPrediction mlPrediction;

    /**
     * Processing time in milliseconds
     */
    @JsonProperty("processingTimeMs")
    private Long processingTimeMs;

    /**
     * Timestamp when analysis was completed
     */
    @JsonProperty("analyzedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant analyzedAt;

    /**
     * Recommended action
     */
    @JsonProperty("recommendedAction")
    private RecommendedAction recommendedAction;

    /**
     * Risk level enumeration
     */
    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Recommended action enumeration
     */
    public enum RecommendedAction {
        APPROVE,
        REVIEW,
        DECLINE,
        BLOCK_USER
    }

    /**
     * Rule evaluation result
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleEvaluation implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("ruleId")
        private String ruleId;

        @JsonProperty("ruleName")
        private String ruleName;

        @JsonProperty("triggered")
        private boolean triggered;

        @JsonProperty("score")
        private BigDecimal score;

        @JsonProperty("message")
        private String message;
    }

    /**
     * ML model prediction details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MLPrediction implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("modelName")
        private String modelName;

        @JsonProperty("modelVersion")
        private String modelVersion;

        @JsonProperty("probability")
        private BigDecimal probability;

        @JsonProperty("prediction")
        private String prediction;

        @JsonProperty("confidence")
        private BigDecimal confidence;

        @JsonProperty("features")
        private List<String> topFeatures;
    }
}
