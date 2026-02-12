package com.gogidix.rapidassist.ai.inference.application.mapper;

import com.gogidix.rapidassist.ai.inference.application.dto.InferenceResultDto;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for InferenceResult
 */
@Mapper(componentModel = "spring")
public interface InferenceResultMapper {

    @Mapping(target = "inferenceRequestId", source = "inferenceRequestId")
    InferenceResultDto toDto(InferenceResult inferenceResult);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    InferenceResult toEntity(InferenceResultDto dto);
}
