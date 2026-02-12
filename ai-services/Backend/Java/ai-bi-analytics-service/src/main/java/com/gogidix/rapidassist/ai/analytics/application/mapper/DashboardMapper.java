package com.gogidix.rapidassist.ai.analytics.application.mapper;

import com.gogidix.rapidassist.ai.analytics.application.dto.DashboardDto;
import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Dashboard
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DashboardMapper {

    DashboardDto toDto(Dashboard dashboard);

    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(dto.getId()))")
    @Mapping(target = "chartIds", expression = "java(dto.getChartIds() != null ? dto.getChartIds().stream().map(java.util.UUID::fromString).toList() : null)")
    Dashboard toDomain(DashboardDto dto);

    List<DashboardDto> toDtoList(List<Dashboard> dashboards);

    List<Dashboard> toDomainList(List<DashboardDto> dtos);

    void updateEntityFromDto(DashboardDto dto, @MappingTarget Dashboard entity);
}
