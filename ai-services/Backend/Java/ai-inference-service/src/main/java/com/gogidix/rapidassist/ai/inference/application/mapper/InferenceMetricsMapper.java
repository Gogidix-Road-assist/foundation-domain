package com.gogidix.rapidassist.ai.inference.application.mapper;

import com.gogidix.rapidassist.ai.inference.application.dto.InferenceMetricsDto;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for InferenceMetrics
 */
@Mapper(componentModel = "spring")
public interface InferenceMetricsMapper {

    InferenceMetricsDto toDto(InferenceMetrics metrics);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    InferenceMetrics toEntity(InferenceMetricsDto dto);
}
