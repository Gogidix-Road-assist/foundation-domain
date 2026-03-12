package com.gogidix.rapidassist.ai.predictive.analytics.application.dto;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Forecast;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Forecast
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
    private LocalDateTime forecastStartDate;
    private LocalDateTime forecastEndDate;
    private Integer forecastHorizon;
    private List<ForecastDataPointDto> forecastData;
    private Double meanAbsoluteError;
    private Double meanAbsolutePercentageError;
    private String status;
    private String frequency;
    private LocalDateTime createdAt;
    private LocalDateTime generatedAt;

    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastDataPointDto {
        private LocalDateTime timestamp;
        private Double value;
        private Double confidence;
    }
}
