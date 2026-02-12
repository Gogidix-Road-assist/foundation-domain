package com.gogidix.rapidassist.ai.forecasting.interfaces.rest.request;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for creating a forecast configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateForecastConfigurationRequest {

    @NotBlank
    private String configName;

    private String description;

    @NotNull
    private ForecastModelType modelType;

    @NotNull
    private Integer forecastHorizon;

    @NotNull
    private DataGranularity granularity;

    private BigDecimal confidenceLevel;

    private Boolean enableSeasonality;

    private Integer seasonalityPeriod;

    private Boolean enableTrend;

    private Map<String, Object> modelParameters;

    private Map<String, Object> preprocessingConfig;

    private Map<String, Object> postprocessingConfig;

    private Integer maxHistoryDataPoints;

    private Integer minHistoryDataPoints;

    private Boolean enableOutlierDetection;

    private BigDecimal outlierThreshold;

    private Boolean enableAnomalyDetection;

    private Map<String, Object> metadata;

    private Boolean isActive;
}
