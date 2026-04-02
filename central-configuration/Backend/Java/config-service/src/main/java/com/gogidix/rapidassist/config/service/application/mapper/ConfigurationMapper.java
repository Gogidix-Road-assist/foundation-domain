package com.gogidix.rapidassist.config.service.application.mapper;

import com.gogidix.rapidassist.config.service.application.dto.response.ConfigurationResponseDto;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between Configuration entities and DTOs.
 *
 * <p>This mapper uses compile-time code generation to create efficient
 * mapping code between domain entities and DTOs.
 *
 * <p>MapStruct will generate the implementation at compile time.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {ConfigurationDataTypeMapper.class}
)
public interface ConfigurationMapper {

    /**
     * Converts a domain Configuration to a Response DTO.
     *
     * @param configuration the domain entity
     * @return the response DTO
     */
    @Mapping(target = "dataType", source = "dataType")
    ConfigurationResponseDto toResponseDto(Configuration configuration);

    /**
     * Converts a list of domain Configurations to Response DTOs.
     *
     * @param configurations the list of domain entities
     * @return the list of response DTOs
     */
    List<ConfigurationResponseDto> toResponseDtoList(List<Configuration> configurations);
}
