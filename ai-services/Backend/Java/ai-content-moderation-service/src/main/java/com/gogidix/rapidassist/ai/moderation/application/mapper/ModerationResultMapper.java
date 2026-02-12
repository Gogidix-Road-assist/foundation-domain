package com.gogidix.rapidassist.ai.moderation.application.mapper;

import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationResultDto;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for ModerationResult
 */
@Mapper(componentModel = "spring")
public interface ModerationResultMapper {

    ModerationResultDto toDto(ModerationResult entity);

    ModerationResult toEntity(ModerationResultDto dto);

    List<ModerationResultDto> toDtoList(List<ModerationResult> entities);

    void updateEntityFromDto(ModerationResultDto dto, @MappingTarget ModerationResult entity);
}
