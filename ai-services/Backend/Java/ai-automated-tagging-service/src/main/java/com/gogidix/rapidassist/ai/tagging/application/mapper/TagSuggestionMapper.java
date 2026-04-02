package com.gogidix.rapidassist.ai.tagging.application.mapper;

import com.gogidix.rapidassist.ai.tagging.application.dto.TagSuggestionDto;
import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for TagSuggestion entity and DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TagMapper.class}
)
public interface TagSuggestionMapper {

    TagSuggestion toDomain(TagSuggestionDto dto);

    @Mapping(target = "suggestedTag", ignore = true)
    TagSuggestionDto toDto(TagSuggestion domain);

    List<TagSuggestionDto> toDtoList(List<TagSuggestion> domains);

    void updateDomainFromDto(TagSuggestionDto dto, @MappingTarget TagSuggestion domain);
}
