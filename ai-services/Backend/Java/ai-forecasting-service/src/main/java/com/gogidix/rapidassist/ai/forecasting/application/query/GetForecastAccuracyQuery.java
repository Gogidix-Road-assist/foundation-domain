package com.gogidix.rapidassist.ai.forecasting.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get forecast accuracy metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetForecastAccuracyQuery {

    private String tenantId;
    private UUID forecastId;
}
