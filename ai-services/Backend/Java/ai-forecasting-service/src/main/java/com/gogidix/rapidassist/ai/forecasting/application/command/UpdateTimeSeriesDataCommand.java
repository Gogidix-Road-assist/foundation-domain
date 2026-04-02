package com.gogidix.rapidassist.ai.forecasting.application.command;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.FrequencyType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.TimeSeriesData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Command to submit time series data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTimeSeriesDataCommand {

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
    private String createdBy;
}
