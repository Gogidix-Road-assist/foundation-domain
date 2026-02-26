package com.gogidix.rapidassist.ai.tagging.application.mapper;

import com.gogidix.rapidassist.ai.tagging.application.dto.ContentTagDto;
import com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for ContentTag entity and DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TagMapper.class}
)
public interface ContentTagMapper {

    ContentTag toDomain(ContentTagDto dto);

    @Mapping(target = "tag", ignore = true)
    ContentTagDto toDto(ContentTag domain);

    List<ContentTagDto> toDtoList(List<ContentTag> domains);

    void updateDomainFromDto(ContentTagDto dto, @MappingTarget ContentTag domain);
}
