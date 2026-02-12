package com.gogidix.rapidassist.ai.analytics.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * REST request to generate an analytics report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {

    @NotBlank(message = "Report name is required")
    private String name;

    private String description;

    @NotBlank(message = "Report type is required")
    private String reportType;

    private Map<String, Object> parameters;

    private Map<String, Object> filters;

    private List<String> metricIds;

    private List<String> dashboardIds;

    private Boolean isScheduled;

    private String scheduleExpression;

    private String scheduledFor;

    private List<String> tags;
}
