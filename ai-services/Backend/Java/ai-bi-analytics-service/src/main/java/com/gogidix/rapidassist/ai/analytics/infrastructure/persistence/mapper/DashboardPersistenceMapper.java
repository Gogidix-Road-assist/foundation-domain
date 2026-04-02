package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.DashboardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Dashboard persistence
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DashboardPersistenceMapper {

    DashboardEntity toEntity(Dashboard domain);

    @Mapping(target = "chartIds", expression = "java(entity.getChartIds() != null ? entity.getChartIds().stream().map(java.util.UUID::fromString).toList() : null)")
    Dashboard toDomain(DashboardEntity entity);

    List<DashboardEntity> toEntityList(List<Dashboard> domains);

    List<Dashboard> toDomainList(List<DashboardEntity> entities);
}
