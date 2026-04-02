package com.gogidix.rapidassist.ai.forecasting.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing ForecastAccuracy.
 * Pure domain model without MongoDB annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastAccuracy {

    @EqualsAndHashCode.Include


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

    /**
     * Business logic: Calculate MAPE (avoiding division by zero)
     */
    public BigDecimal getSafeMape() {
        if (meanAbsolutePercentageError == null) {
            return BigDecimal.ZERO;
        }
        return meanAbsolutePercentageError;
    }

    /**
     * Business logic: Check if forecast is accurate (MAPE < 10%)
     */
    public boolean isHighlyAccurate() {
        BigDecimal threshold = new BigDecimal("0.10");
        return getSafeMape().compareTo(threshold) < 0;
    }

    /**
     * Business logic: Check if forecast is moderately accurate (MAPE < 20%)
     */
    public boolean isModeratelyAccurate() {
        BigDecimal threshold = new BigDecimal("0.20");
        return getSafeMape().compareTo(threshold) < 0;
    }

    /**
     * Business logic: Get overall accuracy score (0-100)
     */
    public BigDecimal getOverallAccuracyScore() {
        if (directionAccuracy != null) {
            return directionAccuracy.multiply(new BigDecimal("100"));
        }
        if (rSquared != null) {
            return rSquared.multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Business logic: Validate accuracy metrics are present
     */
    public boolean hasAccuracyMetrics() {
        return meanAbsoluteError != null || meanSquaredError != null ||
               meanAbsolutePercentageError != null || rootMeanSquaredError != null;
    }

    /**
     * Business logic: Get prediction accuracy percentage
     */
    public BigDecimal getPredictionAccuracyPercentage() {
        if (directionAccuracy != null) {
            return directionAccuracy.multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
}
