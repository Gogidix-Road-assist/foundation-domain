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
 * DTO for AnalyticsReport
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReportDto {

    private String id;
    private String tenantId;
    private String name;
    private String description;
    private String reportType;
    private String status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledFor;
    private LocalDateTime completedAt;
    private Map<String, Object> parameters;
    private Map<String, Object> filters;
    private List<String> metricIds;
    private List<String> dashboardIds;
    private String reportUrl;
    private Long executionTimeMs;
    private Integer recordCount;
    private String errorMessage;
    private Map<String, Object> metadata;
    private Boolean isScheduled;
    private String scheduleExpression;
    private LocalDateTime lastGeneratedAt;
    private Integer generationCount;
    private Map<String, Object> resultData;
    private List<String> tags;
    private Boolean isActive;
}
