package com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.mapper;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.request.CreateFleetRequestDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.response.FleetRequestResponseDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for FleetRequest
 */
@Mapper(componentModel = "spring")
public interface FleetRequestMapper {

    @Mapping(target = "location", source = "location")
    FleetRequestResponseDto toResponseDto(FleetRequest entity);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FleetRequest toEntity(CreateFleetRequestDto dto);

    default FleetRequest.Location mapLocation(CreateFleetRequestDto.LocationDto dto) {
        if (dto == null) return null;
        return FleetRequest.Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .build();
    }

    default FleetRequestResponseDto.LocationDto mapLocation(FleetRequest.Location location) {
        if (location == null) return null;
        return FleetRequestResponseDto.LocationDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .build();
    }
}
