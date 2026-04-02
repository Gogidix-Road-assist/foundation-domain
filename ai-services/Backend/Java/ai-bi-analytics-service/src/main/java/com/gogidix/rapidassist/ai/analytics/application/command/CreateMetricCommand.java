package com.gogidix.rapidassist.ai.analytics.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Command to create a metric definition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMetricCommand {

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
    private List<String> tags;
    private Boolean isActive;
    private String category;
    private Double thresholdWarning;
    private Double thresholdCritical;
    private String formatPattern;
}
