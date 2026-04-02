package com.gogidix.rapidassist.ai.inference.application.mapper;

import com.gogidix.rapidassist.ai.inference.application.dto.InferenceRequestDto;
import com.gogidix.rapidassist.ai.inference.domain.aggregate.InferenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * MapStruct mapper for InferenceRequest
 */
@Mapper(componentModel = "spring")
public interface InferenceRequestMapper {

    InferenceRequestDto toDto(InferenceRequest inferenceRequest);

    @Mappings({
        @Mapping(target = "createdBy", ignore = true),
        @Mapping(target = "updatedBy", ignore = true),
        @Mapping(target = "results", ignore = true),
        @Mapping(target = "metrics", ignore = true),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "requestId", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "startedAt", ignore = true),
        @Mapping(target = "completedAt", ignore = true),
        @Mapping(target = "errorMessage", ignore = true),
        @Mapping(target = "retryCount", ignore = true)
    })
    InferenceRequest toEntity(InferenceRequestDto dto);
}
