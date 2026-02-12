package com.gogidix.rapidassist.ai.forecasting.application.mapper;

import com.gogidix.rapidassist.ai.forecasting.application.dto.ForecastConfigurationDto;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastConfiguration;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for ForecastConfiguration.
 */
@Mapper(componentModel = "spring")
public interface ForecastConfigurationMapper {

    ForecastConfiguration toDomain(ForecastConfigurationDto dto);
    ForecastConfigurationDto toDto(ForecastConfiguration domain);
}
