package com.gogidix.rapidassist.orchestration.fleetorganization.application.mapper;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.CreateFleetPolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.UpdateFleetPolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.FleetPolicyResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for FleetPolicy entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FleetPolicyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", expression = "java(generatePolicyId())")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    FleetPolicy toFleetPolicy(CreateFleetPolicyRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "policyType", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "scopeValue", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateFleetPolicyFromDto(UpdateFleetPolicyRequestDto dto, @MappingTarget FleetPolicy policy);

    FleetPolicyResponseDto toResponseDto(FleetPolicy policy);

    default String generatePolicyId() {
        return "policy-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}
