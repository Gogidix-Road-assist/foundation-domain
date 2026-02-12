package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for updating an existing organization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrganizationRequestDto {

    @NotBlank(message = "Organization name is required")
    private String name;

    private String description;

    private String managerId;

    private String contactEmail;

    private String contactPhone;

    private CreateOrganizationRequestDto.LocationDto location;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
