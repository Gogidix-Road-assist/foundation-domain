package com.gogidix.rapidassist.ai.forecasting.application.dto;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastConfiguration;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ForecastConfiguration.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastConfigurationDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String configName;
    private String description;
    private ForecastModelType modelType;
    private Integer forecastHorizon;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
