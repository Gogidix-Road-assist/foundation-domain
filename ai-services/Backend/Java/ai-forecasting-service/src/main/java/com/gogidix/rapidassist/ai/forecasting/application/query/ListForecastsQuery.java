package com.gogidix.rapidassist.ai.forecasting.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list forecasts with filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListForecastsQuery {

    private String tenantId;
    private String status;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
