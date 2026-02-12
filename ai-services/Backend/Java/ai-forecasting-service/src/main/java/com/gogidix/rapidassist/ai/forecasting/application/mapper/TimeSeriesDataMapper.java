package com.gogidix.rapidassist.ai.forecasting.application.mapper;

import com.gogidix.rapidassist.ai.forecasting.application.dto.TimeSeriesDataDto;
import com.gogidix.rapidassist.ai.forecasting.domain.model.TimeSeriesData;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for TimeSeriesData.
 */
@Mapper(componentModel = "spring")
public interface TimeSeriesDataMapper {

    TimeSeriesData toDomain(TimeSeriesDataDto dto);
    TimeSeriesDataDto toDto(TimeSeriesData domain);
}
