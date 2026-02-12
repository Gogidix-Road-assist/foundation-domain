package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyScope;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyType;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for creating a new fleet policy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFleetPolicyRequestDto {

    @NotBlank(message = "Organization ID is required")
    private String organizationId;

    @NotBlank(message = "Policy name is required")
    private String name;

    private String description;

    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @NotNull(message = "Policy scope is required")
    private PolicyScope scope;

    private String scopeValue;

    @NotEmpty(message = "Policy must have at least one rule")
    private List<PolicyRule> rules;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveUntil;

    private Integer priority;

    private Map<String, Object> metadata;
}
