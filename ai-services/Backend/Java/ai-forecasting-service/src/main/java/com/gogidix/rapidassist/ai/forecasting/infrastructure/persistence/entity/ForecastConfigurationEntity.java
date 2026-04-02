package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Entity for ForecastConfiguration.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "forecast_configuration")
public class ForecastConfigurationEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
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
    private String modelParametersJson;
    private String preprocessingConfigJson;
    private String postprocessingConfigJson;
    private Integer maxHistoryDataPoints;
    private Integer minHistoryDataPoints;
    private Boolean enableOutlierDetection;
    private BigDecimal outlierThreshold;
    private Boolean enableAnomalyDetection;
    private String metadataJson;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
