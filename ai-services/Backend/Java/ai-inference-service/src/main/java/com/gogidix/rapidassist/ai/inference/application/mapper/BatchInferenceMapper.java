package com.gogidix.rapidassist.ai.inference.application.mapper;

import com.gogidix.rapidassist.ai.inference.application.dto.BatchInferenceRequestDto;
import com.gogidix.rapidassist.ai.inference.domain.model.BatchInferenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for BatchInferenceRequest
 */
@Mapper(componentModel = "spring")
public interface BatchInferenceMapper {

    BatchInferenceRequestDto toDto(BatchInferenceRequest batchRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batchId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "progress", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "completedItems", ignore = true)
    @Mapping(target = "failedItems", ignore = true)
    BatchInferenceRequest toEntity(BatchInferenceRequestDto dto);
}
