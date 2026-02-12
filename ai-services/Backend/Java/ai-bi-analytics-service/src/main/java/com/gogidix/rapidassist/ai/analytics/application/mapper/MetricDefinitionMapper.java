package com.gogidix.rapidassist.ai.analytics.application.mapper;

import com.gogidix.rapidassist.ai.analytics.application.dto.MetricDefinitionDto;
import com.gogidix.rapidassist.ai.analytics.domain.model.MetricDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for MetricDefinition
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MetricDefinitionMapper {

    MetricDefinitionDto toDto(MetricDefinition metric);

    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(dto.getId()))")
    MetricDefinition toDomain(MetricDefinitionDto dto);

    List<MetricDefinitionDto> toDtoList(List<MetricDefinition> metrics);

    List<MetricDefinition> toDomainList(List<MetricDefinitionDto> dtos);

    void updateEntityFromDto(MetricDefinitionDto dto, @MappingTarget MetricDefinition entity);
}
