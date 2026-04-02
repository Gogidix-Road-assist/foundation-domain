package com.gogidix.rapidassist.ai.tagging.application.mapper;

import com.gogidix.rapidassist.ai.tagging.application.dto.TagDto;
import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Tag entity and DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TagCategoryMapper.class}
)
public interface TagMapper {

    Tag toDomain(TagDto dto);

    TagDto toDto(Tag domain);

    List<TagDto> toDtoList(List<Tag> domains);

    void updateDomainFromDto(TagDto dto, @MappingTarget Tag domain);
}
