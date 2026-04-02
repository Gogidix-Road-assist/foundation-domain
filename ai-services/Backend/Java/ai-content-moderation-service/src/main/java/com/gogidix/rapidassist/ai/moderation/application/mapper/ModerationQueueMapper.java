package com.gogidix.rapidassist.ai.moderation.application.mapper;

import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationQueueDto;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for ModerationQueue
 */
@Mapper(componentModel = "spring")
public interface ModerationQueueMapper {

    ModerationQueueDto toDto(ModerationQueue entity);

    ModerationQueue toEntity(ModerationQueueDto dto);

    List<ModerationQueueDto> toDtoList(List<ModerationQueue> entities);

    void updateEntityFromDto(ModerationQueueDto dto, @MappingTarget ModerationQueue entity);
}
