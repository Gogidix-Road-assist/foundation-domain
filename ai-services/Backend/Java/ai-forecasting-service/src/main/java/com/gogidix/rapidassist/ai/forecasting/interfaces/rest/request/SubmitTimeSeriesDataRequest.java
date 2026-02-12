package com.gogidix.rapidassist.ai.forecasting.interfaces.rest.request;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.FrequencyType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.TimeSeriesData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for submitting time series data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitTimeSeriesDataRequest {

    @NotBlank
    private String dataSourceName;

    private String description;

    @NotNull
    private DataGranularity granularity;

    @NotEmpty
    private List<TimeSeriesData.DataPoint> dataPoints;

    private FrequencyType frequencyType;

    private Boolean hasSeasonality;

    private Boolean hasTrend;

    private Integer seasonalityPeriod;

    private Map<String, Object> metadata;
}
