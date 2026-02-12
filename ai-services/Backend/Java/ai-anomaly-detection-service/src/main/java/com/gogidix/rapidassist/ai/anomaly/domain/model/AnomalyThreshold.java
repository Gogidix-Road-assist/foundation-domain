package com.gogidix.rapidassist.ai.anomaly.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an anomaly threshold.
 * Contains threshold configuration for anomaly detection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyThreshold {

    private UUID id;
    private String tenantId;
    private String thresholdName;
    private String metricName;
    private ThresholdType thresholdType;
    private Double minValue;
    private Double maxValue;
    private Double thresholdValue;
    private Double standardDeviationMultiplier;
    private Double percentile;
    private String aggregationMethod;
    private Integer windowSize;
    private String windowUnit;
    private ThresholdScope scope;
    private Map<String, Object> conditions;
    private AnomalySeverity severity;
    private Boolean adaptive;
    private Double adaptationRate;
    private Boolean enabled;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastEvaluatedAt;

    /**
     * Business logic: Check if threshold is enabled
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.enabled);
    }

    /**
     * Business logic: Check if threshold is adaptive
     */
    public boolean isAdaptiveThreshold() {
        return Boolean.TRUE.equals(this.adaptive);
    }

    /**
     * Business logic: Check if value exceeds threshold
     */
    public boolean exceedsThreshold(Double value) {
        if (!isEnabled() || value == null) {
            return false;
        }

        if (thresholdType == ThresholdType.MIN_MAX) {
            return (minValue != null && value < minValue) ||
                   (maxValue != null && value > maxValue);
        } else if (thresholdType == ThresholdType.UPPER && maxValue != null) {
            return value > maxValue;
        } else if (thresholdType == ThresholdType.LOWER && minValue != null) {
            return value < minValue;
        } else if (thresholdType == ThresholdType.STANDARD_DEVIATION && thresholdValue != null) {
            return Math.abs(value - thresholdValue) > (standardDeviationMultiplier != null ? standardDeviationMultiplier : 3.0);
        } else if (thresholdType == ThresholdType.PERCENTILE) {
            return value > (percentile != null ? percentile : 95.0);
        }

        return false;
    }

    /**
     * Business logic: Calculate threshold violation
     */
    public Double calculateViolation(Double value) {
        if (!isEnabled() || value == null || thresholdValue == null) {
            return 0.0;
        }

        if (thresholdType == ThresholdType.MIN_MAX) {
            if (maxValue != null && value > maxValue) {
                return value - maxValue;
            }
            if (minValue != null && value < minValue) {
                return minValue - value;
            }
        } else if (thresholdType == ThresholdType.UPPER && maxValue != null) {
            return Math.max(0, value - maxValue);
        } else if (thresholdType == ThresholdType.LOWER && minValue != null) {
            return Math.max(0, minValue - value);
        } else if (thresholdType == ThresholdType.STANDARD_DEVIATION) {
            Double deviation = Math.abs(value - thresholdValue);
            Double limit = thresholdValue * (standardDeviationMultiplier != null ? standardDeviationMultiplier : 3.0);
            return Math.max(0, deviation - limit);
        }

        return 0.0;
    }

    /**
     * Business logic: Update adaptive threshold
     */
    public void updateAdaptiveThreshold(Double newValue) {
        if (!isAdaptiveThreshold() || newValue == null) {
            return;
        }

        if (thresholdValue == null) {
            thresholdValue = newValue;
        } else {
            Double rate = adaptationRate != null ? adaptationRate : 0.1;
            thresholdValue = thresholdValue * (1 - rate) + newValue * rate;
        }

        this.lastEvaluatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Enable threshold
     */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Disable threshold
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
}
