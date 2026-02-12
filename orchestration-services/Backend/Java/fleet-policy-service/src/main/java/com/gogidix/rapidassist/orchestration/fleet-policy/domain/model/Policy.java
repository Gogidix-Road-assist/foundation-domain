package com.gogidix.rapidassist.orchestration.fleet_policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing a fleet policy
 * Policies define rules and standards for fleet operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "policies")
public class Policy {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String policyCode;

    private String name;
    private String description;

    @Indexed
    private PolicyType policyType;

    private PolicySeverity severity;

    @Builder.Default
    private PolicyStatus status = PolicyStatus.DRAFT;

    @DBRef
    @Builder.Default
    private List<PolicyRule> rules = new ArrayList<>();

    @Builder.Default
    private Boolean requiresApproval = false;

    private String approvedBy;
    private LocalDateTime approvedAt;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer version = 1;

    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;

    private String parentPolicyId;

    @Builder.Default
    private List<String> applicableVehicleTypes = new ArrayList<>();

    @Builder.Default
    private List<String> applicableRegions = new ArrayList<>();

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private String notes;

    public enum PolicyType {
        SAFETY,
        MAINTENANCE,
        OPERATIONAL,
        COMPLIANCE,
        BEHAVIOR,
        ENVIRONMENTAL
    }

    public enum PolicySeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum PolicyStatus {
        DRAFT,
        PENDING_APPROVAL,
        ACTIVE,
        INACTIVE,
        ARCHIVED,
        UNDER_REVIEW
    }

    /**
     * Add a rule to this policy
     */
    public void addRule(PolicyRule rule) {
        if (this.rules == null) {
            this.rules = new ArrayList<>();
        }
        this.rules.add(rule);
        rule.setPolicyId(this.id);
    }

    /**
     * Remove a rule from this policy
     */
    public void removeRule(String ruleId) {
        if (this.rules != null) {
            this.rules.removeIf(rule -> rule.getId().equals(ruleId));
        }
    }

    /**
     * Check if policy is currently effective
     */
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        boolean afterStart = effectiveFrom == null || now.isAfter(effectiveFrom);
        boolean beforeEnd = effectiveTo == null || now.isBefore(effectiveTo);
        return afterStart && beforeEnd && isActive;
    }

    /**
     * Activate the policy
     */
    public void activate(String approvedBy) {
        this.status = PolicyStatus.ACTIVE;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * Deactivate the policy
     */
    public void deactivate() {
        this.status = PolicyStatus.INACTIVE;
        this.isActive = false;
    }

    /**
     * Archive the policy
     */
    public void archive() {
        this.status = PolicyStatus.ARCHIVED;
        this.isActive = false;
    }
}
