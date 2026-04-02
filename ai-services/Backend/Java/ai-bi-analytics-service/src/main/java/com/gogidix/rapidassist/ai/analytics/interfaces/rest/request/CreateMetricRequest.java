package com.gogidix.rapidassist.ai.analytics.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * REST request to create a metric definition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMetricRequest {

    @NotBlank(message = "Metric name is required")
    private String name;

    @NotBlank(message = "Metric code is required")
    private String code;

    private String description;

    @NotBlank(message = "Metric type is required")
    private String metricType;

    private String dataSource;

    private String query;

    private Map<String, Object> configuration;

    private String unit;

    private String aggregationFunction;

    private List<String> tags;

    private Boolean isActive;

    private String category;

    private Double thresholdWarning;

    private Double thresholdCritical;

    private String formatPattern;
}
