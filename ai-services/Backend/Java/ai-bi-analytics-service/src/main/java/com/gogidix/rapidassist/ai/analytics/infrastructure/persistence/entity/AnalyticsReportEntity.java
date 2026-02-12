package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MongoDB entity for AnalyticsReport
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analytics_reports")
public class AnalyticsReportEntity {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private String reportType;

    @Indexed
    private String status;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
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

    @Indexed
    private Boolean isActive;
}
