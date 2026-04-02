package com.gogidix.rapidassist.ai.forecasting.application.dto;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.Forecast;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Forecast.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID modelId;
    private String forecastName;
    private String description;
    private ForecastStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer forecastHorizon;
    private DataGranularity granularity;
    private List<Forecast.ForecastDataPoint> dataPoints;
    private Map<String, Object> parameters;
    private BigDecimal confidenceIntervalLower;
    private BigDecimal confidenceIntervalUpper;
    private BigDecimal meanAbsoluteError;
    private BigDecimal meanSquaredError;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
