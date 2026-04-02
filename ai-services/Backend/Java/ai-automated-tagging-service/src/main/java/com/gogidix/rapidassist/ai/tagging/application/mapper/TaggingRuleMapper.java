package com.gogidix.rapidassist.ai.tagging.application.mapper;

import com.gogidix.rapidassist.ai.tagging.application.dto.TaggingRuleDto;
import com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for TaggingRule entity and DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TagMapper.class}
)
public interface TaggingRuleMapper {

    TaggingRule toDomain(TaggingRuleDto dto);

    @Mapping(target = "tags", ignore = true)
    TaggingRuleDto toDto(TaggingRule domain);

    List<TaggingRuleDto> toDtoList(List<TaggingRule> domains);

    void updateDomainFromDto(TaggingRuleDto dto, @MappingTarget TaggingRule domain);
}
