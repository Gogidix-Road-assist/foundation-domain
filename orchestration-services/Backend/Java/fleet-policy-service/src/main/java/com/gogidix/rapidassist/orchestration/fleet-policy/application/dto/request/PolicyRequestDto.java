package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for creating/updating policies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequestDto {

    @NotBlank(message = "Policy code is required")
    private String policyCode;

    @NotBlank(message = "Policy name is required")
    private String name;

    private String description;

    @NotNull(message = "Policy type is required")
    private Policy.PolicyType policyType;

    private Policy.PolicySeverity severity;

    private Policy.PolicyStatus status;

    @Valid
    private List<PolicyRuleRequestDto> rules;

    private Boolean requiresApproval;

    private Boolean isActive;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    private String parentPolicyId;

    private List<String> applicableVehicleTypes;

    private List<String> applicableRegions;

    private List<String> tags;

    private String notes;
}
