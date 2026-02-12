package com.gogidix.rapidassist.ai.forecasting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ForecastAccuracy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastAccuracyDto {

    private UUID id;
    private String tenantId;
    private UUID forecastId;
    private UUID modelId;
    private BigDecimal meanAbsoluteError;
    private BigDecimal meanSquaredError;
    private BigDecimal rootMeanSquaredError;
    private BigDecimal meanAbsolutePercentageError;
    private BigDecimal symmetricMeanAbsolutePercentageError;
    private BigDecimal meanBiasError;
    private BigDecimal trackingSignal;
    private BigDecimal theilsUStatistic;
    private BigDecimal rSquared;
    private Integer correctDirectionPredictions;
    private Integer totalPredictions;
    private BigDecimal directionAccuracy;
    private Map<String, BigDecimal> accuracyByHorizon;
    private Map<String, Object> metadata;
    private LocalDateTime calculatedAt;
    private LocalDateTime createdAt;
    private String createdBy;
}
