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
 * Domain model representing TimeSeriesData.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesData {

    private UUID id;
    private String tenantId;
    private String dataSourceName;
    private String description;
    private DataGranularity granularity;
    private List<DataPoint> dataPoints;
    private FrequencyType frequencyType;
    private Boolean hasSeasonality;
    private Boolean hasTrend;
    private Integer seasonalityPeriod;
    private Map<String, Object> metadata;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalDataPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Check if data has seasonality
     */
    public boolean hasSeasonalityPattern() {
        return hasSeasonality != null && hasSeasonality;
    }

    /**
     * Business logic: Check if data has trend
     */
    public boolean hasTrendPattern() {
        return hasTrend != null && hasTrend;
    }

    /**
     * Business logic: Validate time series has data points
     */
    public boolean hasDataPoints() {
        return dataPoints != null && !dataPoints.isEmpty();
    }

    /**
     * Business logic: Get data points count
     */
    public int getDataPointsCount() {
        return dataPoints != null ? dataPoints.size() : 0;
    }

    /**
     * Business logic: Get time series duration in days
     */
    public long getTimeSeriesDurationDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        }
        return 0;
    }

    /**
     * Nested class for data points
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private LocalDateTime timestamp;
        private BigDecimal value;
        private Map<String, Object> features;
        private String category;
        private Boolean isOutlier;
    }
}
