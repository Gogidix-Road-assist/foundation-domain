package com.gogidix.rapidassist.ai.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a metric definition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDefinition {

    private UUID id;
    private String tenantId;
    private String name;
    private String code;
    private String description;
    private MetricType metricType;
    private String dataSource;
    private String query;
    private Map<String, Object> configuration;
    private String unit;
    private String aggregationFunction;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;
    private List<String> tags;
    private Boolean isActive;
    private String category;
    private Double thresholdWarning;
    private Double thresholdCritical;
    private String formatPattern;
}
