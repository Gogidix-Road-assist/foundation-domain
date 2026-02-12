package com.gogidix.rapidassist.common.domain.models.mapper;

import com.gogidix.rapidassist.common.domain.models.business.Customer;
import com.gogidix.rapidassist.common.domain.models.dto.request.CustomerCreateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.request.CustomerUpdateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for Customer entity and DTOs.
 * Provides automatic mapping between entities and DTOs with Spring integration.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerMapper {

    /**
     * Converts CustomerCreateRequest to Customer entity.
     * @param request the create request DTO
     * @return the Customer entity
     */
    Customer toEntity(CustomerCreateRequest request);

    /**
     * Converts Customer entity to CustomerResponse DTO.
     * @param customer the Customer entity
     * @return the CustomerResponse DTO
     */
    CustomerResponse toResponse(Customer customer);

    /**
     * Updates an existing Customer entity with values from CustomerUpdateRequest.
     * Null values in the request are ignored (not applied).
     * @param request the update request DTO
     * @param customer the existing Customer entity to update
     */
    void updateEntityFromDto(CustomerUpdateRequest request, @MappingTarget Customer customer);
}
