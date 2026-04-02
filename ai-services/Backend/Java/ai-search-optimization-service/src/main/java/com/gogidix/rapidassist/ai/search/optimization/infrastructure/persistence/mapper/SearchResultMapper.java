package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchResult;
import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity.SearchResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for SearchResult domain model and entity.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SearchResultMapper {

    @Mapping(source = "uuid", target = "id")
    SearchResult toDomain(SearchResultEntity entity);

    @Mapping(source = "id", target = "uuid")
    SearchResultEntity toEntity(SearchResult domain);

    @Mapping(source = "id", target = "uuid")
    void updateEntityFromDomain(SearchResult domain, @MappingTarget SearchResultEntity entity);
}
