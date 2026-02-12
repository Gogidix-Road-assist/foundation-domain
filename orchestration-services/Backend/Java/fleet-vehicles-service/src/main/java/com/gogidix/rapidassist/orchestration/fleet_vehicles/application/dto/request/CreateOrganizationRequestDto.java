package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for creating a new organization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrganizationRequestDto {

    @NotBlank(message = "Organization name is required")
    private String name;

    private String description;

    private String parentId;

    @NotNull(message = "Organization type is required")
    private OrganizationType organizationType;

    private Integer level;

    private String path;

    private String managerId;

    private String contactEmail;

    private String contactPhone;

    private LocationDto location;

    private Map<String, Object> metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private Double latitude;
        private Double longitude;
        private String address;
    }
}
