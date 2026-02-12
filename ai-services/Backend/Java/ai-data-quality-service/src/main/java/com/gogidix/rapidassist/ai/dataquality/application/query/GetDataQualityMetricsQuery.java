package com.gogidix.rapidassist.ai.dataquality.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Query to get data quality metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDataQualityMetricsQuery {

    private String tenantId;
    private String metricName;
    private String entityType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer limit;
}
