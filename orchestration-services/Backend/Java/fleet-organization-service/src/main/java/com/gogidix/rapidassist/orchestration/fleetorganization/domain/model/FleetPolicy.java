package com.gogidix.rapidassist.orchestration.fleetorganization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * FleetPolicy entity representing organization-level policies.
 * Policies define rules, procedures, and compliance requirements for fleet operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fleet_policies")
@CompoundIndex(name = "tenant_org_idx", def = "{'tenantId': 1, 'organizationId': 1}")
public class FleetPolicy {

    @Id
    private String id;

    @Indexed(unique = true)
    private String policyId;

    @Indexed
    private String organizationId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private PolicyType policyType;

    @Indexed
    private PolicyScope scope;

    private String scopeValue; // Specific entity ID if scope is INDIVIDUAL

    private List<PolicyRule> rules;

    @Indexed
    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveUntil;

    @Indexed
    private Integer priority; // Higher priority policies override lower ones

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * Domain logic: Check if policy is currently effective
     */
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        if (!isActive) {
            return false;
        }
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) {
            return false;
        }
        if (effectiveUntil != null && now.isAfter(effectiveUntil)) {
            return false;
        }
        return true;
    }

    /**
     * Domain logic: Validate policy
     */
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Policy name cannot be blank");
        }
        if (policyType == null) {
            throw new IllegalArgumentException("Policy type is required");
        }
        if (scope == null) {
            throw new IllegalArgumentException("Policy scope is required");
        }
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Policy must have at least one rule");
        }
    }

    /**
     * Domain logic: Soft delete policy
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PolicyRule {
        private String ruleId;
        private String name;
        private String description;
        private RuleType ruleType;
        private String condition;
        private String action;
        private Map<String, Object> parameters;

        public enum RuleType {
            VALIDATION,
            CONSTRAINT,
            NOTIFICATION,
            AUTOMATION,
            COMPLIANCE
        }
    }

    public enum PolicyType {
        SAFETY,
        MAINTENANCE,
        OPERATIONAL,
        COMPLIANCE,
        BEHAVIOR,
        ENVIRONMENTAL,
        ACCESS_CONTROL
    }

    public enum PolicyScope {
        GLOBAL,        // Apply to all units in organization
        VEHICLE_TYPE,  // Apply to specific vehicle type
        INDIVIDUAL     // Apply to specific unit (scopeValue)
    }
}
