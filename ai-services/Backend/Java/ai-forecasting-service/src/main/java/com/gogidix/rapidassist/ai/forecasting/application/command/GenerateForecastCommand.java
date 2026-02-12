package com.gogidix.rapidassist.ai.forecasting.application.command;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Command to generate a forecast.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateForecastCommand {

    private String tenantId;
    private String forecastName;
    private String description;
    private UUID modelId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer forecastHorizon;
    private DataGranularity granularity;
    private Map<String, Object> parameters;
    private Map<String, Object> metadata;
    private String createdBy;
}
