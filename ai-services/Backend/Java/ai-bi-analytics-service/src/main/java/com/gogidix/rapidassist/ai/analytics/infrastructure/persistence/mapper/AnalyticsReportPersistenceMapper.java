package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.AnalyticsReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for AnalyticsReport persistence
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AnalyticsReportPersistenceMapper {

    AnalyticsReportEntity toEntity(AnalyticsReport domain);

    @Mapping(target = "metricIds", expression = "java(entity.getMetricIds() != null ? entity.getMetricIds().stream().map(java.util.UUID::fromString).toList() : null)")
    @Mapping(target = "dashboardIds", expression = "java(entity.getDashboardIds() != null ? entity.getDashboardIds().stream().map(java.util.UUID::fromString).toList() : null)")
    AnalyticsReport toDomain(AnalyticsReportEntity entity);

    List<AnalyticsReportEntity> toEntityList(List<AnalyticsReport> domains);

    List<AnalyticsReport> toDomainList(List<AnalyticsReportEntity> entities);
}
