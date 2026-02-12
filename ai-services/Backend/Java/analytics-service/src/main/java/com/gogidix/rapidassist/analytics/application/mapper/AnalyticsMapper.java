package com.gogidix.rapidassist.analytics.application.mapper;

import com.gogidix.rapidassist.analytics.application.dto.AnalyticsDto;
import com.gogidix.rapidassist.analytics.domain.model.Analytics;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Analytics.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AnalyticsMapper {

    AnalyticsDto toDto(Analytics domain);

    Analytics toDomain(AnalyticsDto dto);

    List<AnalyticsDto> toDtoList(List<Analytics> domains);

    void updateDomainFromDto(AnalyticsDto dto, @MappingTarget Analytics domain);
}
