package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization.OrganizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for organization response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponseDto {

    private String id;
    private String organizationId;
    private String name;
    private String description;
    private String parentId;
    private OrganizationType organizationType;
    private Integer level;
    private String path;
    private String managerId;
    private String contactEmail;
    private String contactPhone;
    private LocationDto location;
    private Boolean isActive;
    private Map<String, Object> metadata;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
