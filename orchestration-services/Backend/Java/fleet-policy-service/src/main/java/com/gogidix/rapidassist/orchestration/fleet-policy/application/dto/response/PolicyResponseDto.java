package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for policy responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponseDto {

    private String id;

    private String tenantId;

    private String policyCode;

    private String name;

    private String description;

    private Policy.PolicyType policyType;

    private Policy.PolicySeverity severity;

    private Policy.PolicyStatus status;

    private List<PolicyRuleResponseDto> rules;

    private Boolean requiresApproval;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    private Integer version;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    private String parentPolicyId;

    private List<String> applicableVehicleTypes;

    private List<String> applicableRegions;

    private List<String> tags;

    private String notes;

    private Boolean effective;

    private Integer ruleCount;
}
