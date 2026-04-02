package com.gogidix.rapidassist.ai.anomaly.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an alert for detected anomalies.
 * Manages alert lifecycle, notifications, and acknowledgment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnomalyAlert {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String alertId;
    private UUID detectionId;
    private UUID ruleId;
    private String title;
    private String message;
    private AlertSeverity severity;
    private AlertStatus status;
    private List<String> notificationChannels;
    private Map<String, Object> context;
    private String assignedTo;
    private LocalDateTime triggeredAt;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private Integer escalationLevel;
    private LocalDateTime lastEscalatedAt;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * Business logic: Acknowledge alert
     */
    public void acknowledge(String acknowledgedBy) {
        if (this.status == AlertStatus.OPEN) {
            this.status = AlertStatus.ACKNOWLEDGED;
            this.acknowledgedAt = LocalDateTime.now();
            this.acknowledgedBy = acknowledgedBy;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot acknowledge alert in status: " + this.status);
        }
    }

    /**
     * Business logic: Resolve alert
     */
    public void resolve(String resolvedBy, String resolutionNotes) {
        if (this.status == AlertStatus.OPEN || this.status == AlertStatus.ACKNOWLEDGED) {
            this.status = AlertStatus.RESOLVED;
            this.resolvedAt = LocalDateTime.now();
            this.resolvedBy = resolvedBy;
            this.resolutionNotes = resolutionNotes;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot resolve alert in status: " + this.status);
        }
    }

    /**
     * Business logic: Escalate alert
     */
    public void escalate() {
        if (this.escalationLevel == null) {
            this.escalationLevel = 1;
        } else {
            this.escalationLevel++;
        }
        this.lastEscalatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if alert is open
     */
    public boolean isOpen() {
        return AlertStatus.OPEN.equals(this.status);
    }

    /**
     * Business logic: Check if alert is acknowledged
     */
    public boolean isAcknowledged() {
        return AlertStatus.ACKNOWLEDGED.equals(this.status);
    }

    /**
     * Business logic: Check if alert is resolved
     */
    public boolean isResolved() {
        return AlertStatus.RESOLVED.equals(this.status);
    }

    /**
     * Business logic: Check if alert is critical
     */
    public boolean isCritical() {
        return AlertSeverity.CRITICAL.equals(this.severity);
    }

    /**
     * Business logic: Get time since trigger in minutes
     */
    public long getMinutesSinceTriggered() {
        if (triggeredAt == null) {
            return 0;
        }
        return java.time.Duration.between(triggeredAt, LocalDateTime.now()).toMinutes();
    }

    /**
     * Business logic: Check if requires escalation
     */
    public boolean requiresEscalation(int escalationThresholdMinutes) {
        return isOpen() && getMinutesSinceTriggered() > escalationThresholdMinutes;
    }
}
