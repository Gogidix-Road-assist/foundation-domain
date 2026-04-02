package com.gogidix.rapidassist.ai.forecasting.application.dto;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.FrequencyType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.TimeSeriesData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for TimeSeriesData.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDataDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String dataSourceName;
    private String description;
    private DataGranularity granularity;
    private List<TimeSeriesData.DataPoint> dataPoints;
    private FrequencyType frequencyType;
    private Boolean hasSeasonality;
    private Boolean hasTrend;
    private Integer seasonalityPeriod;
    private Map<String, Object> metadata;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalDataPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
