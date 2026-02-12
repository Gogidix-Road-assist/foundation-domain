package com.gogidix.rapidassist.orchestration.fleetorganization.application.mapper;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.CreateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.UpdateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.OrganizationResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for Organization entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", expression = "java(generateOrganizationId())")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(source = "location", target = "location")
    Organization toOrganization(CreateOrganizationRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "organizationType", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(source = "location", target = "location")
    void updateOrganizationFromDto(UpdateOrganizationRequestDto dto, @MappingTarget Organization organization);

    @Mapping(source = "location", target = "location")
    OrganizationResponseDto toResponseDto(Organization organization);

    default String generateOrganizationId() {
        return "org-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    default Organization.Location mapLocation(CreateOrganizationRequestDto.LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        return Organization.Location.builder()
                .latitude(locationDto.getLatitude())
                .longitude(locationDto.getLongitude())
                .address(locationDto.getAddress())
                .build();
    }

    default OrganizationResponseDto.LocationDto mapLocationResponse(Organization.Location location) {
        if (location == null) {
            return null;
        }
        return OrganizationResponseDto.LocationDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .build();
    }
}
