package com.gogidix.rapidassist.ai.dataquality.application.dto;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityMetric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DataQualityMetric
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityMetricDto {

    private UUID id;
    private String tenantId;
    private String metricName;
    private DataQualityMetric.MetricType metricType;
    private String entityType;
    private Double metricValue;
    private String unit;
    private LocalDateTime metricTimestamp;
    private Map<String, Object> metricDimensions;
    private Map<String, Object> metadata;
    private LocalDateTime calculatedAt;
    private String calculatedBy;
}
