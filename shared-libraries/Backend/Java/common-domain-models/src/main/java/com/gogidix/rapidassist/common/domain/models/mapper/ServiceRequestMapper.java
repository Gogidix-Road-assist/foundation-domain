package com.gogidix.rapidassist.common.domain.models.mapper;

import com.gogidix.rapidassist.common.domain.models.business.ServiceRequest;
import com.gogidix.rapidassist.common.domain.models.dto.request.ServiceRequestCreateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.request.ServiceRequestUpdateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.response.ServiceRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for ServiceRequest entity and DTOs.
 * Provides automatic mapping between entities and DTOs with Spring integration.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface ServiceRequestMapper {

    /**
     * Converts ServiceRequestCreateRequest to ServiceRequest entity.
     * @param request the create request DTO
     * @return the ServiceRequest entity
     */
    ServiceRequest toEntity(ServiceRequestCreateRequest request);

    /**
     * Converts ServiceRequest entity to ServiceRequestResponse DTO.
     * @param serviceRequest the ServiceRequest entity
     * @return the ServiceRequestResponse DTO
     */
    ServiceRequestResponse toResponse(ServiceRequest serviceRequest);

    /**
     * Updates an existing ServiceRequest entity with values from ServiceRequestUpdateRequest.
     * Null values in the request are ignored (not applied).
     * @param request the update request DTO
     * @param serviceRequest the existing ServiceRequest entity to update
     */
    void updateEntityFromDto(ServiceRequestUpdateRequest request, @MappingTarget ServiceRequest serviceRequest);
}
