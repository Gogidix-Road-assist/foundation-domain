package com.gogidix.rapidassist.ai.forecasting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a Forecast.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    private UUID id;
    private String tenantId;
    private UUID modelId;
    private String forecastName;
    private String description;
    private ForecastStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer forecastHorizon;
    private DataGranularity granularity;
    private List<ForecastDataPoint> dataPoints;
    private Map<String, Object> parameters;
    private BigDecimal confidenceIntervalLower;
    private BigDecimal confidenceIntervalUpper;
    private BigDecimal meanAbsoluteError;
    private BigDecimal meanSquaredError;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Check if forecast is completed
     */
    public boolean isCompleted() {
        return ForecastStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if forecast is failed
     */
    public boolean isFailed() {
        return ForecastStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if forecast is in progress
     */
    public boolean isInProgress() {
        return ForecastStatus.GENERATING.equals(this.status);
    }

    /**
     * Business logic: Get forecast duration in days
     */
    public long getForecastDurationDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        }
        return 0;
    }

    /**
     * Business logic: Validate forecast has data points
     */
    public boolean hasDataPoints() {
        return dataPoints != null && !dataPoints.isEmpty();
    }

    /**
     * Business logic: Get total data points count
     */
    public int getDataPointsCount() {
        return dataPoints != null ? dataPoints.size() : 0;
    }

    /**
     * Nested class for forecast data points
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastDataPoint {
        private LocalDateTime timestamp;
        private BigDecimal predictedValue;
        private BigDecimal lowerBound;
        private BigDecimal upperBound;
        private BigDecimal actualValue;
        private BigDecimal error;
    }
}
