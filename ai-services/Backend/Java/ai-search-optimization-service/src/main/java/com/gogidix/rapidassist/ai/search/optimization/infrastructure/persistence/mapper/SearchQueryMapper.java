package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchQuery;
import com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity.SearchQueryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for SearchQuery domain model and entity.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SearchQueryMapper {

    @Mapping(source = "uuid", target = "id")
    SearchQuery toDomain(SearchQueryEntity entity);

    @Mapping(source = "id", target = "uuid")
    SearchQueryEntity toEntity(SearchQuery domain);

    @Mapping(source = "id", target = "uuid")
    void updateEntityFromDomain(SearchQuery domain, @MappingTarget SearchQueryEntity entity);
}
