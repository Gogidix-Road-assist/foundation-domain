package com.gogidix.rapidassist.ai.moderation.application.mapper;

import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationRuleDto;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for ModerationRule
 */
@Mapper(componentModel = "spring")
public interface ModerationRuleMapper {

    ModerationRuleDto toDto(ModerationRule entity);

    ModerationRule toEntity(ModerationRuleDto dto);

    List<ModerationRuleDto> toDtoList(List<ModerationRule> entities);

    void updateEntityFromDto(ModerationRuleDto dto, @MappingTarget ModerationRule entity);
}
