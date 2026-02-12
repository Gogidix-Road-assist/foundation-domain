package com.gogidix.rapidassist.orchestration.fleetorganization.application.mapper;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.CreateFleetUnitRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.UpdateFleetUnitRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.FleetUnitResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for FleetUnit entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FleetUnitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unitId", expression = "java(generateUnitId())")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "currentDriverId", ignore = true)
    @Mapping(target = "currentAssignmentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(source = "currentLocation", target = "currentLocation")
    FleetUnit toFleetUnit(CreateFleetUnitRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unitId", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "unitType", ignore = true)
    @Mapping(target = "currentDriverId", ignore = true)
    @Mapping(target = "currentAssignmentId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(source = "currentLocation", target = "currentLocation")
    void updateFleetUnitFromDto(UpdateFleetUnitRequestDto dto, @MappingTarget FleetUnit fleetUnit);

    @Mapping(source = "currentLocation", target = "currentLocation")
    FleetUnitResponseDto toResponseDto(FleetUnit fleetUnit);

    default String generateUnitId() {
        return "unit-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    default FleetUnit.Location mapLocation(CreateFleetUnitRequestDto.LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        return FleetUnit.Location.builder()
                .latitude(locationDto.getLatitude())
                .longitude(locationDto.getLongitude())
                .address(locationDto.getAddress())
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    default FleetUnitResponseDto.LocationDto mapLocationResponse(FleetUnit.Location location) {
        if (location == null) {
            return null;
        }
        return FleetUnitResponseDto.LocationDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .timestamp(location.getTimestamp())
                .build();
    }
}
