package com.gogidix.rapidassist.ai.analytics.application.mapper;

import com.gogidix.rapidassist.ai.analytics.application.dto.DataQueryDto;
import com.gogidix.rapidassist.ai.analytics.domain.model.DataQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for DataQuery
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DataQueryMapper {

    DataQueryDto toDto(DataQuery query);

    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(dto.getId()))")
    DataQuery toDomain(DataQueryDto dto);

    List<DataQueryDto> toDtoList(List<DataQuery> queries);

    List<DataQuery> toDomainList(List<DataQueryDto> dtos);

    void updateEntityFromDto(DataQueryDto dto, @MappingTarget DataQuery entity);
}
