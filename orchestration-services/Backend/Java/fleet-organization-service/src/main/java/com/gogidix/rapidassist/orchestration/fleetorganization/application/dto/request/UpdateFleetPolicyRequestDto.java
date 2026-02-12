package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for updating an existing fleet policy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFleetPolicyRequestDto {

    @NotBlank(message = "Policy name is required")
    private String name;

    private String description;

    @NotEmpty(message = "Policy must have at least one rule")
    private List<PolicyRule> rules;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveUntil;

    private Integer priority;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
