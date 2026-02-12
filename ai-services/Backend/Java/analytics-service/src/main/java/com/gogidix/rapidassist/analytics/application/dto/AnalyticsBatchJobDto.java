package com.gogidix.rapidassist.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for AnalyticsBatchJob operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsBatchJobDto {

    private UUID id;
    private String tenantId;
    private String jobName;
    private String jobType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    private Double progressPercentage;
    private Map<String, Object> jobParameters;
    private String dataSource;
    private String analyticsType;
    private Map<String, Object> result;
    private String errorMessage;
    private String triggeredBy;
    private String executionMode;
    private Integer retryCount;
    private Integer maxRetries;
    private String schedule;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
