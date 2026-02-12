package com.gogidix.rapidassist.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a trend analysis.
 * Contains trend data and predictions for metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trend {

    private UUID id;
    private String tenantId;
    private String trendName;
    private String metricName;
    private String trendType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<TrendDataPoint> dataPoints;
    private String trendDirection;
    private Double trendStrength;
    private Double predictedValue;
    private Double confidenceInterval;
    private String seasonality;
    private Map<String, Object> trendParameters;
    private String status;
    private String description;
    private String algorithm;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested class for trend data points
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPoint {
        private LocalDateTime timestamp;
        private Double value;
        private Double predictedValue;
        private Double lowerBound;
        private Double upperBound;
        private Map<String, Object> attributes;
    }

    /**
     * Business logic: Check if trend is upward
     */
    public boolean isUpward() {
        return "UPWARD".equalsIgnoreCase(trendDirection);
    }

    /**
     * Business logic: Check if trend is downward
     */
    public boolean isDownward() {
        return "DOWNWARD".equalsIgnoreCase(trendDirection);
    }

    /**
     * Business logic: Check if trend is stable
     */
    public boolean isStable() {
        return "STABLE".equalsIgnoreCase(trendDirection);
    }

    /**
     * Business logic: Check if prediction is reliable
     */
    public boolean isPredictionReliable() {
        return confidenceInterval != null && confidenceInterval > 0.7;
    }

    /**
     * Business logic: Get trend duration in days
     */
    public long getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.Duration.between(startDate, endDate).toDays();
    }

    /**
     * Business logic: Check if trend needs refresh
     */
    public boolean needsRefresh() {
        return updatedAt != null && LocalDateTime.now().isAfter(updatedAt.plusDays(7));
    }

    /**
     * Business logic: Calculate growth rate
     */
    public Double calculateGrowthRate() {
        if (dataPoints == null || dataPoints.size() < 2) {
            return null;
        }
        TrendDataPoint first = dataPoints.get(0);
        TrendDataPoint last = dataPoints.get(dataPoints.size() - 1);
        if (first.getValue() == null || last.getValue() == null || first.getValue() == 0) {
            return null;
        }
        return ((last.getValue() - first.getValue()) / first.getValue()) * 100;
    }
}
