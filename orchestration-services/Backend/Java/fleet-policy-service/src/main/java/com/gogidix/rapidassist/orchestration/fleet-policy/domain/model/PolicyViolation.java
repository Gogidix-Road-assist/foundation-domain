package com.gogidix.rapidassist.orchestration.fleet_policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain entity representing a policy violation
 * Records when policies or rules are broken
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "policy_violations")
public class PolicyViolation {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String policyId;

    private String policyCode;

    private String policyName;

    @Indexed
    private String ruleId;

    private String ruleCode;

    private String ruleName;

    @Indexed
    private ViolationEntityType entityType;

    @Indexed
    private String entityId;

    private String entityName;

    @Indexed
    private ViolationSeverity severity;

    @Indexed
    private ViolationStatus status;

    @Builder.Default
    private Integer points = 0;

    private String violationMessage;

    private String detectedBy;

    @Indexed
    private LocalDateTime detectedAt;

    private String location;

    private Double latitude;

    private Double longitude;

    private Object actualValue;

    private Object expectedValue;

    private String unit;

    private Map<String, Object> context;

    private String acknowledgedBy;
    private LocalDateTime acknowledgedAt;

    private String resolvedBy;
    private LocalDateTime resolvedAt;

    private String resolutionNotes;

    @Builder.Default
    private Boolean requiresImmediateAction = false;

    private String requiredAction;

    @Indexed
    private LocalDateTime actionDueBy;

    @Builder.Default
    private Boolean actionCompleted = false;

    private String actionCompletedBy;
    private LocalDateTime actionCompletedAt;

    private String notes;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;

    private String reportedTo;

    private LocalDateTime reportedAt;

    @Builder.Default
    private Integer escalationLevel = 0;

    private String escalatedTo;

    private LocalDateTime escalatedAt;

    public enum ViolationEntityType {
        VEHICLE,
        DRIVER,
        FLEET,
        TRAILER,
        EQUIPMENT
    }

    public enum ViolationSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum ViolationStatus {
        OPEN,
        ACKNOWLEDGED,
        IN_PROGRESS,
        RESOLVED,
        CLOSED,
        ESCALATED,
        IGNORED
    }

    /**
     * Acknowledge the violation
     */
    public void acknowledge(String acknowledgedBy) {
        this.status = ViolationStatus.ACKNOWLEDGED;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = LocalDateTime.now();
    }

    /**
     * Start resolving the violation
     */
    public void startResolution() {
        this.status = ViolationStatus.IN_PROGRESS;
    }

    /**
     * Resolve the violation
     */
    public void resolve(String resolvedBy, String resolutionNotes) {
        this.status = ViolationStatus.RESOLVED;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNotes = resolutionNotes;
        this.actionCompleted = true;
        this.actionCompletedBy = resolvedBy;
        this.actionCompletedAt = LocalDateTime.now();
    }

    /**
     * Close the violation
     */
    public void close(String closedBy) {
        this.status = ViolationStatus.CLOSED;
        this.updatedBy = closedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Escalate the violation
     */
    public void escalate(String escalatedTo, int escalationLevel) {
        this.status = ViolationStatus.ESCALATED;
        this.escalatedTo = escalatedTo;
        this.escalationLevel = escalationLevel;
        this.escalatedAt = LocalDateTime.now();
    }

    /**
     * Check if action is overdue
     */
    public boolean isActionOverdue() {
        return actionDueBy != null &&
               (status == ViolationStatus.OPEN || status == ViolationStatus.ACKNOWLEDGED) &&
               LocalDateTime.now().isAfter(actionDueBy);
    }

    /**
     * Check if violation requires escalation
     */
    public boolean requiresEscalation() {
        if (actionDueBy == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        long hoursOverdue = java.time.Duration.between(actionDueBy, now).toHours();

        return switch (severity) {
            case CRITICAL -> hoursOverdue > 1;
            case HIGH -> hoursOverdue > 4;
            case MEDIUM -> hoursOverdue > 24;
            case LOW -> hoursOverdue > 72;
        };
    }
}
