package com.gogidix.rapidassist.ai.dataquality.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain Model representing data quality metrics and KPIs.
 * Tracks quality metrics over time for trend analysis.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityMetric {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String metricName;
    private String metricType;
    private String entityType;
    private Double metricValue;
    private String unit;
    private LocalDateTime metricTimestamp;
    private Map<String, Object> metricDimensions;
    private Map<String, Object> metadata;
    private LocalDateTime calculatedAt;
    private String calculatedBy;

    public enum MetricType {
        COMPLETENESS_RATE,
        ACCURACY_RATE,
        CONSISTENCY_RATE,
        VALIDITY_RATE,
        UNIQUENESS_RATE,
        TIMELINESS_RATE,
        INTEGRITY_RATE,
        OVERALL_QUALITY_SCORE,
        ISSUE_COUNT,
        PASS_RATE,
        FAILURE_RATE,
        CUSTOM
    }

    /**
     * Business logic: Check if metric is good (above threshold)
     */
    public boolean isGood(double threshold) {
        return this.metricValue != null && this.metricValue >= threshold;
    }

    /**
     * Business logic: Check if metric is warning level (within range)
     */
    public boolean isWarning(double lowerThreshold, double upperThreshold) {
        return this.metricValue != null &&
               this.metricValue >= lowerThreshold &&
               this.metricValue < upperThreshold;
    }

    /**
     * Business logic: Check if metric is critical (below threshold)
     */
    public boolean isCritical(double threshold) {
        return this.metricValue != null && this.metricValue < threshold;
    }

    /**
     * Business logic: Get metric value as percentage
     */
    public double getPercentage() {
        if (metricValue == null) {
            return 0.0;
        }
        if (metricValue > 1.0) {
            return metricValue; // Already in percentage form
        }
        return metricValue * 100.0;
    }

    /**
     * Business logic: Get trend direction (requires comparison with previous)
     */
    public TrendDirection getTrend(DataQualityMetric previous) {
        if (previous == null || previous.metricValue == null || this.metricValue == null) {
            return TrendDirection.UNKNOWN;
        }
        if (this.metricValue > previous.metricValue) {
            return TrendDirection.IMPROVING;
        } else if (this.metricValue < previous.metricValue) {
            return TrendDirection.DECLINING;
        } else {
            return TrendDirection.STABLE;
        }
    }

    public enum TrendDirection {
        IMPROVING,
        DECLINING,
        STABLE,
        UNKNOWN
    }

    /**
     * Business logic: Check if metric is recent (within specified hours)
     */
    public boolean isRecent(int hoursThreshold) {
        if (metricTimestamp == null) {
            return false;
        }
        long hoursDiff = java.time.Duration.between(metricTimestamp, LocalDateTime.now()).toHours();
        return hoursDiff <= hoursThreshold;
    }

    /**
     * Business logic: Get formatted metric value
     */
    public String getFormattedValue() {
        if (metricValue == null) {
            return "N/A";
        }
        if (unit != null && unit.equals("%")) {
            return String.format("%.2f%%", metricValue);
        }
        return String.format("%.2f %s", metricValue, unit != null ? unit : "");
    }
}
