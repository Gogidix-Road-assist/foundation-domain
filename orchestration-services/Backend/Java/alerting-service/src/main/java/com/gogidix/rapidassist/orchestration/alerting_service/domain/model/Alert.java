package com.gogidix.rapidassist.orchestration.alerting_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing an Alert in the roadside assistance system.
 * Alerts are generated for emergency situations, breakdowns, accidents, etc.
 * This is the core domain entity with rich business logic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    private String id;
    private String alertId;
    private String requestId;
    private String tenantId;

    private AlertType type;
    private AlertSeverity severity;
    private String title;
    private String description;
    private String source;

    @Builder.Default
    private AlertStatus status = AlertStatus.PENDING;

    private String assignedTo;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;

    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Integer escalationLevel = 0;

    @Builder.Default
    private Boolean escalationRequired = false;

    @Builder.Default
    private List<AlertAction> actions = new ArrayList<>();

    private Location location;
    private VehicleInfo vehicleInfo;
    private CustomerInfo customerInfo;

    /**
     * Business logic: Acknowledge this alert
     */
    public void acknowledge(String acknowledgedBy, String assignedTo) {
        if (this.status == AlertStatus.ACKNOWLEDGED) {
            throw new IllegalStateException("Alert is already acknowledged: " + this.alertId);
        }
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.CLOSED) {
            throw new IllegalStateException("Cannot acknowledge resolved or closed alert: " + this.alertId);
        }

        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = LocalDateTime.now();
        this.assignedTo = assignedTo;
        this.updatedAt = LocalDateTime.now();

        addAction(AlertAction.ActionType.ACKNOWLEDGED, acknowledgedBy, "Alert acknowledged");
    }

    /**
     * Business logic: Start working on this alert
     */
    public void startProgress() {
        if (this.status != AlertStatus.ACKNOWLEDGED && this.status != AlertStatus.PENDING) {
            throw new IllegalStateException("Alert must be acknowledged or pending to start progress: " + this.alertId);
        }

        this.status = AlertStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();

        addAction(AlertAction.ActionType.STATUS_CHANGED, this.assignedTo, "Marked as in progress");
    }

    /**
     * Business logic: Resolve this alert
     */
    public void resolve(String resolvedBy, String resolutionNotes) {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.CLOSED) {
            throw new IllegalStateException("Alert is already resolved or closed: " + this.alertId);
        }

        this.status = AlertStatus.RESOLVED;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNotes = resolutionNotes;
        this.updatedAt = LocalDateTime.now();

        addAction(AlertAction.ActionType.RESOLVED, resolvedBy, resolutionNotes);
    }

    /**
     * Business logic: Escalate this alert
     */
    public void escalate(Integer escalationLevel, String assignedTo) {
        if (escalationLevel <= this.escalationLevel) {
            throw new IllegalArgumentException("Escalation level must be greater than current level");
        }

        this.escalationLevel = escalationLevel;
        this.status = AlertStatus.ESCALATED;
        this.assignedTo = assignedTo;
        this.escalationRequired = false;
        this.updatedAt = LocalDateTime.now();

        addAction(AlertAction.ActionType.ESCALATED, assignedTo, "Escalated to level " + escalationLevel);
    }

    /**
     * Business logic: Mark escalation as required
     */
    public void markEscalationRequired() {
        this.escalationRequired = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if alert requires escalation based on SLA
     */
    public boolean requiresEscalation() {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.CLOSED) {
            return false;
        }

        // Critical alerts escalate after 30 minutes if not acknowledged
        if (this.severity == AlertSeverity.CRITICAL && this.status == AlertStatus.PENDING) {
            return this.createdAt.plusMinutes(30).isBefore(LocalDateTime.now());
        }

        // High alerts escalate after 1 hour if not in progress
        if (this.severity == AlertSeverity.HIGH && this.status != AlertStatus.IN_PROGRESS) {
            return this.createdAt.plusHours(1).isBefore(LocalDateTime.now());
        }

        return this.escalationRequired;
    }

    /**
     * Business logic: Check if alert is critical and unattended
     */
    public boolean isCriticalAndUnattended() {
        return this.severity == AlertSeverity.CRITICAL
            && (this.status == AlertStatus.PENDING || this.status == AlertStatus.ACKNOWLEDGED);
    }

    /**
     * Business logic: Close alert (after resolution)
     */
    public void close() {
        if (this.status != AlertStatus.RESOLVED) {
            throw new IllegalStateException("Alert must be resolved before closing: " + this.alertId);
        }

        this.status = AlertStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();

        addAction(AlertAction.ActionType.CLOSED, this.resolvedBy, "Alert closed");
    }

    private void addAction(AlertAction.ActionType type, String performedBy, String notes) {
        this.actions.add(AlertAction.builder()
            .actionType(type)
            .performedBy(performedBy)
            .performedAt(LocalDateTime.now())
            .notes(notes)
            .build());
    }

    public enum AlertType {
        EMERGENCY, BREAKDOWN, ACCIDENT, TOW_REQUEST, FUEL_DELIVERY,
        TIRE_CHANGE, LOCKOUT, JUMP_START, OTHER
    }

    public enum AlertSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum AlertStatus {
        PENDING, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, ESCALATED, CLOSED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleInfo {
        private String vehicleId;
        private String make;
        private String model;
        private Integer year;
        private String color;
        private String licensePlate;
        private String vin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String customerId;
        private String name;
        private String phone;
        private String email;
        private String membershipId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertAction {
        private ActionType actionType;
        private String performedBy;
        private LocalDateTime performedAt;
        private String notes;

        public enum ActionType {
            CREATED, ACKNOWLEDGED, STATUS_CHANGED, ESCALATED, RESOLVED, CLOSED, UPDATED
        }
    }
}
