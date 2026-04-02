package com.gogidix.rapidassist.ai.analytics.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get a metric by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMetricQuery {

    private String metricId;
    private String tenantId;
}
