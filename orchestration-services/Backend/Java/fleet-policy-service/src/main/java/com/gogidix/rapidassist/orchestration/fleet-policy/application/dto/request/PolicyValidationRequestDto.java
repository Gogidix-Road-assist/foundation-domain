package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for policy validation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyValidationRequestDto {

    @NotBlank(message = "Policy ID is required")
    private String policyId;

    @NotNull(message = "Entity type is required")
    private PolicyViolation.ViolationEntityType entityType;

    @NotBlank(message = "Entity ID is required")
    private String entityId;

    private String entityName;

    @NotNull(message = "Validation data is required")
    private Map<String, Object> data;

    private String location;

    private Double latitude;

    private Double longitude;
}
