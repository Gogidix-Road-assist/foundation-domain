package com.gogidix.rapidassist.orchestration.fleet_policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain entity representing policy compliance tracking
 * Tracks compliance status for vehicles, drivers, or fleets
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "policy_compliance")
public class PolicyCompliance {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String policyId;

    private String policyCode;

    private String policyName;

    @Indexed
    private ComplianceEntityType entityType;

    @Indexed
    private String entityId;

    private String entityName;

    @Indexed
    private LocalDate complianceDate;

    @Indexed
    private ComplianceStatus status;

    @Builder.Default
    private Double complianceScore = 0.0;

    @Builder.Default
    private Integer totalChecks = 0;

    @Builder.Default
    private Integer passedChecks = 0;

    @Builder.Default
    private Integer failedChecks = 0;

    @Builder.Default
    private Integer warningChecks = 0;

    @Builder.Default
    private Integer violationCount = 0;

    @Builder.Default
    private Integer totalPoints = 0;

    private String lastCheckedBy;
    private LocalDateTime lastCheckedAt;

    private String checkedBy;
    private LocalDateTime checkedAt;

    private Map<String, Object> complianceDetails;

    private String notes;

    @Builder.Default
    private Boolean requiresAction = false;

    private String requiredAction;

    private LocalDateTime actionDueBy;

    @Builder.Default
    private Boolean actionCompleted = false;

    private String actionCompletedBy;
    private LocalDateTime actionCompletedAt;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;

    public enum ComplianceEntityType {
        VEHICLE,
        DRIVER,
        FLEET,
        TRAILER,
        EQUIPMENT
    }

    public enum ComplianceStatus {
        COMPLIANT,
        NON_COMPLIANT,
        PARTIALLY_COMPLIANT,
        PENDING_REVIEW,
        NOT_APPLICABLE,
        EXEMPTED
    }

    /**
     * Calculate compliance percentage
     */
    public double getCompliancePercentage() {
        if (totalChecks == null || totalChecks == 0) {
            return 0.0;
        }
        return (passedChecks.doubleValue() / totalChecks.doubleValue()) * 100.0;
    }

    /**
     * Update compliance status based on checks
     */
    public void updateStatus() {
        double percentage = getCompliancePercentage();

        if (totalChecks == 0) {
            this.status = ComplianceStatus.PENDING_REVIEW;
        } else if (percentage >= 100.0) {
            this.status = ComplianceStatus.COMPLIANT;
        } else if (percentage >= 70.0) {
            this.status = ComplianceStatus.PARTIALLY_COMPLIANT;
        } else {
            this.status = ComplianceStatus.NON_COMPLIANT;
        }

        this.complianceScore = percentage;
    }

    /**
     * Mark action as completed
     */
    public void completeAction(String completedBy) {
        this.actionCompleted = true;
        this.actionCompletedBy = completedBy;
        this.actionCompletedAt = LocalDateTime.now();
    }

    /**
     * Check if action is overdue
     */
    public boolean isActionOverdue() {
        return requiresAction && !actionCompleted &&
               actionDueBy != null && LocalDateTime.now().isAfter(actionDueBy);
    }
}
