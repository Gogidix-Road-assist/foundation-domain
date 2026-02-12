package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastStatus;
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
 * MongoDB Entity for Forecast.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "forecast")
public class ForecastEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    private String forecastName;
    private String description;
    private ForecastStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer forecastHorizon;
    private DataGranularity granularity;
    private String dataPointsJson;
    private String parametersJson;
    private BigDecimal confidenceIntervalLower;
    private BigDecimal confidenceIntervalUpper;
    private BigDecimal meanAbsoluteError;
    private BigDecimal meanSquaredError;
    private String metadataJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
