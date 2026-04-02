package com.gogidix.rapidassist.ai.analytics.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Command to generate an analytics report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportCommand {

    private String tenantId;
    private String name;
    private String description;
    private String reportType;
    private String triggeredBy;
    private Map<String, Object> parameters;
    private Map<String, Object> filters;
    private List<String> metricIds;
    private List<String> dashboardIds;
    private Boolean isScheduled;
    private String scheduleExpression;
    private LocalDateTime scheduledFor;
    private List<String> tags;
}
