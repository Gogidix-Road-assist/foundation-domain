package com.gogidix.rapidassist.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Metric operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDto {

    private UUID id;
    private String tenantId;
    private String metricName;
    private String metricCategory;
    private Double metricValue;
    private String metricUnit;
    private String metricType;
    private Map<String, Object> dimensions;
    private LocalDateTime timestamp;
    private String source;
    private String granularity;
    private Map<String, Object> tags;
    private Double threshold;
    private String status;
    private String description;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
