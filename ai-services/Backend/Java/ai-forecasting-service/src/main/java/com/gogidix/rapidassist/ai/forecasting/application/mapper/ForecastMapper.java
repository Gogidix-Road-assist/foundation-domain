package com.gogidix.rapidassist.ai.forecasting.application.mapper;

import com.gogidix.rapidassist.ai.forecasting.application.dto.ForecastDto;
import com.gogidix.rapidassist.ai.forecasting.domain.model.Forecast;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Forecast.
 */
@Mapper(componentModel = "spring")
public interface ForecastMapper {

    Forecast toDomain(ForecastDto dto);
    ForecastDto toDto(Forecast domain);
}
