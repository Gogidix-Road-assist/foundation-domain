package com.gogidix.rapidassist.ai.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for MetricDefinition
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDefinitionDto {

    private String id;
    private String tenantId;
    private String name;
    private String code;
    private String description;
    private String metricType;
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
