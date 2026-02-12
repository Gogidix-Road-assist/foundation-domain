package com.gogidix.rapidassist.tenancy.configuration.service.application.mapper;

import com.gogidix.rapidassist.tenancy.configuration.service.application.dto.response.TenantConfigResponseDto;
import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between TenantConfig entities and DTOs.
 *
 * <p>This mapper uses compile-time code generation to create efficient
 * mapping code between domain entities and DTOs.
 *
 * <p>MapStruct will generate the implementation at compile time.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TenantConfigMapper {

    /**
     * Converts a domain TenantConfig to a Response DTO.
     *
     * @param tenantConfig the domain entity
     * @return the response DTO
     */
    TenantConfigResponseDto toResponseDto(TenantConfig tenantConfig);

    /**
     * Converts a list of domain TenantConfigs to Response DTOs.
     *
     * @param tenantConfigs the list of domain entities
     * @return the list of response DTOs
     */
    List<TenantConfigResponseDto> toResponseDtoList(List<TenantConfig> tenantConfigs);
}
