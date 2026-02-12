package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyRule;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyScope;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for fleet policy response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetPolicyResponseDto {

    private String id;
    private String policyId;
    private String organizationId;
    private String name;
    private String description;
    private PolicyType policyType;
    private PolicyScope scope;
    private String scopeValue;
    private List<PolicyRule> rules;
    private Boolean isActive;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveUntil;
    private Integer priority;
    private Map<String, Object> metadata;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
