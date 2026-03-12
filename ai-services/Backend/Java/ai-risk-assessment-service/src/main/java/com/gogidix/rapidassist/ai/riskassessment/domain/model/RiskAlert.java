package com.gogidix.rapidassist.ai.riskassessment.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Risk Alert Domain Model
 * Represents an alert generated for high-risk situations
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAlert {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID riskAssessmentId;
    private String title;
    private String description;
    private AlertPriority priority;
    private AlertStatus status;
    private double riskScore;
    private RiskLevel riskLevel;
    private RiskCategory category;
    private String assignedTo;
    private String acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String resolution;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Acknowledge alert
     */
    public void acknowledge(String acknowledgedBy) {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.DISMISSED) {
            throw new IllegalStateException("Cannot acknowledge an alert that is " + this.status);
        }
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = acknowledgedBy;
    }

    /**
     * Business logic: Start progress on alert
     */
    public void startProgress(String updatedBy) {
        if (this.status != AlertStatus.ACTIVE && this.status != AlertStatus.ACKNOWLEDGED) {
            throw new IllegalStateException("Cannot start progress on alert with status: " + this.status);
        }
        this.status = AlertStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Business logic: Resolve alert
     */
    public void resolve(String resolution, String resolvedBy) {
        if (this.status != AlertStatus.IN_PROGRESS && this.status != AlertStatus.ACKNOWLEDGED) {
            throw new IllegalStateException("Cannot resolve alert with status: " + this.status);
        }
        this.status = AlertStatus.RESOLVED;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = resolvedBy;
    }

    /**
     * Business logic: Dismiss alert
     */
    public void dismiss(String dismissedBy) {
        if (this.status == AlertStatus.RESOLVED) {
            throw new IllegalStateException("Cannot dismiss a resolved alert");
        }
        this.status = AlertStatus.DISMISSED;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = dismissedBy;
    }

    /**
     * Business logic: Escalate alert
     */
    public void escalate(String escalatedBy) {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.DISMISSED) {
            throw new IllegalStateException("Cannot escalate an alert that is " + this.status);
        }
        this.status = AlertStatus.ESCALATED;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = escalatedBy;
    }

    /**
     * Business logic: Check if alert is active
     */
    public boolean isActive() {
        return this.status == AlertStatus.ACTIVE;
    }

    /**
     * Business logic: Check if alert is overdue (not resolved within hours)
     */
    public boolean isOverdue(int hoursThreshold) {
        if (this.status == AlertStatus.RESOLVED || this.resolvedAt != null) {
            return false;
        }
        LocalDateTime threshold = this.createdAt.plusHours(hoursThreshold);
        return LocalDateTime.now().isAfter(threshold);
    }

    /**
     * Business logic: Get time since creation in hours
     */
    public long getTimeSinceCreationHours() {
        if (this.createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(this.createdAt, LocalDateTime.now()).toHours();
    }
}
