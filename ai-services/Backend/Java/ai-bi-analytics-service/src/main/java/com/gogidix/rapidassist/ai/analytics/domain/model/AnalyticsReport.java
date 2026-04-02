package com.gogidix.rapidassist.ai.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an analytics report
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReport {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private ReportType reportType;
    private ReportStatus status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledFor;
    private LocalDateTime completedAt;
    private Map<String, Object> parameters;
    private Map<String, Object> filters;
    private List<UUID> metricIds;
    private List<UUID> dashboardIds;
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
