package com.gogidix.rapidassist.ai.report.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a report generation request.
 * Tracks the lifecycle of individual report generation operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneration {

    private UUID id;
    private String tenantId;
    private UUID scheduleId;
    private UUID templateId;
    private String name;
    private String description;
    private ReportStatus status;
    private ReportFormat format;
    private Map<String, Object> parameters;
    private String fileLocation;
    private Long fileSizeBytes;
    private LocalDateTime generatedAt;
    private LocalDateTime completedAt;
    private Integer recordCount;
    private String errorMessage;
    private String requestedBy;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer retryCount;
    private Integer maxRetries;
}
