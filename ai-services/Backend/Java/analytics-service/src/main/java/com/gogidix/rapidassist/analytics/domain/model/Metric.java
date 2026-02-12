package com.gogidix.rapidassist.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a metric entry.
 * Tracks individual metrics for monitoring and analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric {

    private UUID id;
    private String tenantId;
    private String metricName;
    private String metricCategory;
    private Double metricValue;
    private String metricUnit;
    private String metricType;
    private Map<String, Object> dimensions;
    private LocalDateTime timestamp;
    private String source;
    private String granularity;
    private Map<String, Object> tags;
    private Double threshold;
    private String status;
    private String description;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if metric exceeds threshold
     */
    public boolean exceedsThreshold() {
        return threshold != null && metricValue != null && metricValue > threshold;
    }

    /**
     * Business logic: Check if metric is below threshold
     */
    public boolean isBelowThreshold() {
        return threshold != null && metricValue != null && metricValue < threshold;
    }

    /**
     * Business logic: Get metric age in hours
     */
    public long getAgeInHours() {
        return timestamp != null ? java.time.Duration.between(timestamp, LocalDateTime.now()).toHours() : 0;
    }

    /**
     * Business logic: Check if metric is stale
     */
    public boolean isStale(int staleHours) {
        return getAgeInHours() > staleHours;
    }

    /**
     * Business logic: Update metric value
     */
    public void updateValue(Double newValue, String updatedBy) {
        this.metricValue = newValue;
        this.updatedAt = LocalDateTime.now();
        this.status = "UPDATED";
        if (this.metadata == null) {
            this.metadata = Map.of();
        }
        this.metadata = Map.of("lastUpdatedBy", updatedBy, "previousValue", this.metricValue);
    }
}
