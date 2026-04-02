package com.gogidix.rapidassist.ai.forecasting.application.mapper;

import com.gogidix.rapidassist.ai.forecasting.application.dto.ForecastModelDto;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModel;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for ForecastModel.
 */
@Mapper(componentModel = "spring")
public interface ForecastModelMapper {

    ForecastModel toDomain(ForecastModelDto dto);
    ForecastModelDto toDto(ForecastModel domain);
}
