package com.gogidix.rapidassist.common.domain.models.mapper;

import com.gogidix.rapidassist.common.domain.models.business.Vehicle;
import com.gogidix.rapidassist.common.domain.models.dto.request.VehicleCreateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.response.VehicleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for Vehicle entity and DTOs.
 * Provides automatic mapping between entities and DTOs with Spring integration.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface VehicleMapper {

    /**
     * Converts VehicleCreateRequest to Vehicle entity.
     * @param request the create request DTO
     * @return the Vehicle entity
     */
    Vehicle toEntity(VehicleCreateRequest request);

    /**
     * Converts Vehicle entity to VehicleResponse DTO.
     * @param vehicle the Vehicle entity
     * @return the VehicleResponse DTO
     */
    VehicleResponse toResponse(Vehicle vehicle);

    /**
     * Updates an existing Vehicle entity with values from VehicleCreateRequest.
     * Null values in the request are ignored (not applied).
     * @param request the create/update request DTO
     * @param vehicle the existing Vehicle entity to update
     */
    void updateEntityFromDto(VehicleCreateRequest request, @MappingTarget Vehicle vehicle);
}
