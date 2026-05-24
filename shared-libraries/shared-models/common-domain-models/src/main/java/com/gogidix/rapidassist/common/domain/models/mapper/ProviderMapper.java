package com.gogidix.rapidassist.common.domain.models.mapper;

import com.gogidix.rapidassist.common.domain.models.business.Provider;
import com.gogidix.rapidassist.common.domain.models.dto.request.ProviderCreateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.response.ProviderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for Provider entity and DTOs.
 * Provides automatic mapping between entities and DTOs with Spring integration.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface ProviderMapper {

    /**
     * Converts ProviderCreateRequest to Provider entity.
     * @param request the create request DTO
     * @return the Provider entity
     */
    Provider toEntity(ProviderCreateRequest request);

    /**
     * Converts Provider entity to ProviderResponse DTO.
     * @param provider the Provider entity
     * @return the ProviderResponse DTO
     */
    ProviderResponse toResponse(Provider provider);

    /**
     * Updates an existing Provider entity with values from ProviderCreateRequest.
     * Null values in the request are ignored (not applied).
     * @param request the create/update request DTO
     * @param provider the existing Provider entity to update
     */
    void updateEntityFromDto(ProviderCreateRequest request, @MappingTarget Provider provider);
}
