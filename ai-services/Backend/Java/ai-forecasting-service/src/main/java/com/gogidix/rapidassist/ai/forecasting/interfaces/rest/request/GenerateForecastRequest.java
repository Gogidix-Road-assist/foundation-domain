package com.gogidix.rapidassist.ai.forecasting.interfaces.rest.request;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for generating a forecast.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateForecastRequest {

    @NotNull
    private String forecastName;

    private String description;

    @NotNull
    private UUID modelId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    private Integer forecastHorizon;

    @NotNull
    private DataGranularity granularity;

    private Map<String, Object> parameters;

    private Map<String, Object> metadata;
}
