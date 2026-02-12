package com.gogidix.rapidassist.analytics.application.mapper;

import com.gogidix.rapidassist.analytics.application.dto.MetricDto;
import com.gogidix.rapidassist.analytics.domain.model.Metric;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Metric.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MetricMapper {

    MetricDto toDto(Metric domain);

    Metric toDomain(MetricDto dto);

    List<MetricDto> toDtoList(List<Metric> domains);

    void updateDomainFromDto(MetricDto dto, @MappingTarget Metric domain);
}
