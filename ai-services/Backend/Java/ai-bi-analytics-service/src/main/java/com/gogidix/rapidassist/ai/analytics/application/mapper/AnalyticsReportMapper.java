package com.gogidix.rapidassist.ai.analytics.application.mapper;

import com.gogidix.rapidassist.ai.analytics.application.dto.AnalyticsReportDto;
import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for AnalyticsReport
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {UuidMapper.class}
)
public interface AnalyticsReportMapper {

    AnalyticsReportDto toDto(AnalyticsReport report);

    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(dto.getId()))")
    @Mapping(target = "metricIds", expression = "java(dto.getMetricIds() != null ? dto.getMetricIds().stream().map(java.util.UUID::fromString).toList() : null)")
    @Mapping(target = "dashboardIds", expression = "java(dto.getDashboardIds() != null ? dto.getDashboardIds().stream().map(java.util.UUID::fromString).toList() : null)")
    AnalyticsReport toDomain(AnalyticsReportDto dto);

    List<AnalyticsReportDto> toDtoList(List<AnalyticsReport> reports);

    List<AnalyticsReport> toDomainList(List<AnalyticsReportDto> dtos);

    void updateEntityFromDto(AnalyticsReportDto dto, @MappingTarget AnalyticsReport entity);
}
