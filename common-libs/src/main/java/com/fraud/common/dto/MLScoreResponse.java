package com.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * MLScoreResponse DTO - Response from ML model scoring.
 * 
 * Contains the fraud probability and prediction details
 * returned by the ML model service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLScoreResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("fraudProbability")
    private BigDecimal fraudProbability;

    @JsonProperty("prediction")
    private String prediction;

    @JsonProperty("isFraud")
    private Boolean isFraud;

    @JsonProperty("confidence")
    private BigDecimal confidence;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("modelVersion")
    private String modelVersion;

    @JsonProperty("processingTimeMs")
    private Long processingTimeMs;

    @JsonProperty("topFeatures")
    private List<FeatureImportance> topFeatures;

    @JsonProperty("threshold")
    private BigDecimal threshold;

    /**
     * Feature importance details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureImportance implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("featureName")
        private String featureName;

        @JsonProperty("importance")
        private BigDecimal importance;

        @JsonProperty("value")
        private Object value;
    }
}
