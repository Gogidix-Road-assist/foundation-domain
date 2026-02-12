package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.analytics.domain.model.DataQuery;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.DataQueryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for DataQuery persistence
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DataQueryPersistenceMapper {

    DataQueryEntity toEntity(DataQuery domain);

    DataQuery toDomain(DataQueryEntity entity);

    List<DataQueryEntity> toEntityList(List<DataQuery> domains);

    List<DataQuery> toDomainList(List<DataQueryEntity> entities);
}
