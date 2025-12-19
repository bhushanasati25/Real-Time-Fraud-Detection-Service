package com.fraud.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MLScoreRequest DTO - Request payload for ML model scoring.
 * 
 * Contains the features extracted from a transaction that are
 * sent to the ML model service for fraud probability scoring.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLScoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("merchantId")
    private String merchantId;

    @JsonProperty("merchantCategory")
    private String merchantCategory;

    @JsonProperty("transactionType")
    private String transactionType;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("hourOfDay")
    private Integer hourOfDay;

    @JsonProperty("dayOfWeek")
    private Integer dayOfWeek;

    @JsonProperty("isWeekend")
    private Boolean isWeekend;

    @JsonProperty("isNightTime")
    private Boolean isNightTime;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("distanceFromLastTransaction")
    private Double distanceFromLastTransaction;

    @JsonProperty("timeSinceLastTransaction")
    private Long timeSinceLastTransaction;

    @JsonProperty("transactionCountLast24h")
    private Integer transactionCountLast24h;

    @JsonProperty("totalAmountLast24h")
    private BigDecimal totalAmountLast24h;

    @JsonProperty("averageTransactionAmount")
    private BigDecimal averageTransactionAmount;

    @JsonProperty("amountDeviation")
    private Double amountDeviation;

    @JsonProperty("isNewDevice")
    private Boolean isNewDevice;

    @JsonProperty("isNewLocation")
    private Boolean isNewLocation;

    @JsonProperty("isNewMerchant")
    private Boolean isNewMerchant;
}
