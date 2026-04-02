package com.gogidix.rapidassist.ai.tagging.application.mapper;

import com.gogidix.rapidassist.ai.tagging.application.dto.TagCategoryDto;
import com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for TagCategory entity and DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TagCategoryMapper {

    TagCategory toDomain(TagCategoryDto dto);

    TagCategoryDto toDto(TagCategory domain);

    List<TagCategoryDto> toDtoList(List<TagCategory> domains);

    void updateDomainFromDto(TagCategoryDto dto, @MappingTarget TagCategory domain);
}
