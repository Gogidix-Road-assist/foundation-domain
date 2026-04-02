package com.gogidix.rapidassist.ai.analytics.application.mapper;

import com.gogidix.rapidassist.ai.analytics.application.dto.ChartConfigurationDto;
import com.gogidix.rapidassist.ai.analytics.domain.model.ChartConfiguration;
import com.gogidix.rapidassist.ai.analytics.domain.model.ChartType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for ChartConfiguration
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {UuidMapper.class}
)
public interface ChartConfigurationMapper {

    @Mapping(target = "chartType", source = "chartType", qualifiedByName = "chartTypeToString")
    @Mapping(target = "xAxis", ignore = true)
    @Mapping(target = "yAxis", ignore = true)
    ChartConfigurationDto toDto(ChartConfiguration chart);

    @Mapping(target = "chartType", source = "chartType", qualifiedByName = "stringToChartType")
    ChartConfiguration toDomain(ChartConfigurationDto dto);

    List<ChartConfigurationDto> toDtoList(List<ChartConfiguration> charts);

    List<ChartConfiguration> toDomainList(List<ChartConfigurationDto> dtos);

    void updateEntityFromDto(ChartConfigurationDto dto, @MappingTarget ChartConfiguration entity);

    @Named("chartTypeToString")
    default String chartTypeToString(ChartType chartType) {
        return chartType != null ? chartType.name() : null;
    }

    @Named("stringToChartType")
    default ChartType stringToChartType(String chartType) {
        return chartType != null ? ChartType.valueOf(chartType) : null;
    }
}
