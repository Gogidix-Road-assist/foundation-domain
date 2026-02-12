package com.gogidix.rapidassist.ai.forecasting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing ForecastConfiguration.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastConfiguration {

    private UUID id;
    private String tenantId;
    private String configName;
    private String description;
    private ForecastModelType modelType;
    private Integer forecastHorizon;
    private DataGranularity granularity;
    private BigDecimal confidenceLevel;
    private Boolean enableSeasonality;
    private Integer seasonalityPeriod;
    private Boolean enableTrend;
    private Map<String, Object> modelParameters;
    private Map<String, Object> preprocessingConfig;
    private Map<String, Object> postprocessingConfig;
    private Integer maxHistoryDataPoints;
    private Integer minHistoryDataPoints;
    private Boolean enableOutlierDetection;
    private BigDecimal outlierThreshold;
    private Boolean enableAnomalyDetection;
    private Map<String, Object> metadata;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Check if configuration is active
     */
    public boolean isActiveConfiguration() {
        return isActive != null && isActive;
    }

    /**
     * Business logic: Check if seasonality is enabled
     */
    public boolean isSeasonalityEnabled() {
        return enableSeasonality != null && enableSeasonality;
    }

    /**
     * Business logic: Check if trend is enabled
     */
    public boolean isTrendEnabled() {
        return enableTrend != null && enableTrend;
    }

    /**
     * Business logic: Validate configuration has required parameters
     */
    public boolean isValidConfiguration() {
        return modelType != null && forecastHorizon != null && granularity != null;
    }

    /**
     * Business logic: Check if outlier detection is enabled
     */
    public boolean isOutlierDetectionEnabled() {
        return enableOutlierDetection != null && enableOutlierDetection;
    }

    /**
     * Business logic: Check if anomaly detection is enabled
     */
    public boolean isAnomalyDetectionEnabled() {
        return enableAnomalyDetection != null && enableAnomalyDetection;
    }

    /**
     * Business logic: Get confidence level percentage
     */
    public BigDecimal getConfidenceLevelPercentage() {
        if (confidenceLevel != null) {
            return confidenceLevel.multiply(new BigDecimal("100"));
        }
        return new BigDecimal("95"); // Default 95%
    }
}
