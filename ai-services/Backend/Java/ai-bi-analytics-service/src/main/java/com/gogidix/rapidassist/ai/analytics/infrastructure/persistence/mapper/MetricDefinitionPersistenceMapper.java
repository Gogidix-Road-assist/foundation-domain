package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.analytics.domain.model.MetricDefinition;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.MetricDefinitionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for MetricDefinition persistence
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MetricDefinitionPersistenceMapper {

    MetricDefinitionEntity toEntity(MetricDefinition domain);

    MetricDefinition toDomain(MetricDefinitionEntity entity);

    List<MetricDefinitionEntity> toEntityList(List<MetricDefinition> domains);

    List<MetricDefinition> toDomainList(List<MetricDefinitionEntity> entities);
}
