package com.gogidix.rapidassist.ai.anomaly.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an anomaly detection result.
 * Contains information about detected anomalies, severity, and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnomalyDetection {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String dataSource;
    private String dataPoint;
    private AnomalySeverity severity;
    private AnomalyStatus status;
    private Double anomalyScore;
    private Double confidence;
    private String detectionMethod;
    private Map<String, Object> data;
    private Map<String, Object> anomalyFeatures;
    private String patternId;
    private String ruleId;
    private LocalDateTime detectedAt;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String description;
    private String recommendation;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * Business logic: Check if anomaly is critical
     */
    public boolean isCritical() {
        return AnomalySeverity.CRITICAL.equals(this.severity);
    }

    /**
     * Business logic: Check if anomaly is acknowledged
     */
    public boolean isAcknowledged() {
        return AnomalyStatus.ACKNOWLEDGED.equals(this.status);
    }

    /**
     * Business logic: Check if anomaly is resolved
     */
    public boolean isResolved() {
        return AnomalyStatus.RESOLVED.equals(this.status);
    }

    /**
     * Business logic: Check if anomaly is pending
     */
    public boolean isPending() {
        return AnomalyStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Mark as acknowledged
     */
    public void acknowledge(String acknowledgedBy) {
        if (this.status == AnomalyStatus.PENDING) {
            this.status = AnomalyStatus.ACKNOWLEDGED;
            this.acknowledgedAt = LocalDateTime.now();
            this.acknowledgedBy = acknowledgedBy;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot acknowledge anomaly in status: " + this.status);
        }
    }

    /**
     * Business logic: Mark as resolved
     */
    public void resolve(String resolvedBy) {
        if (this.status == AnomalyStatus.ACKNOWLEDGED || this.status == AnomalyStatus.PENDING) {
            this.status = AnomalyStatus.RESOLVED;
            this.resolvedAt = LocalDateTime.now();
            this.resolvedBy = resolvedBy;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot resolve anomaly in status: " + this.status);
        }
    }

    /**
     * Business logic: Get time since detection in hours
     */
    public long getHoursSinceDetection() {
        if (detectedAt == null) {
            return 0;
        }
        return java.time.Duration.between(detectedAt, LocalDateTime.now()).toHours();
    }

    /**
     * Business logic: Check if requires immediate attention
     */
    public boolean requiresImmediateAttention() {
        return isCritical() && isPending() && getHoursSinceDetection() < 1;
    }
}
