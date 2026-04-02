package com.gogidix.rapidassist.ai.inference.application.mapper;

import com.gogidix.rapidassist.ai.inference.application.dto.ModelVersionDto;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for ModelVersion
 */
@Mapper(componentModel = "spring")
public interface ModelVersionMapper {

    ModelVersionDto toDto(ModelVersion modelVersion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deployedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    ModelVersion toEntity(ModelVersionDto dto);
}
