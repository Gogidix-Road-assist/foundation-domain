package com.gogidix.rapidassist.ai.forecasting.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a forecast by ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetForecastQuery {

    private String tenantId;
    private UUID forecastId;
    private Boolean includeDataPoints;
    private Boolean includeAccuracy;
}
