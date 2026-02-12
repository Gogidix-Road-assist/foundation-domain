package com.gogidix.rapidassist.ai.report.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.report.domain.model.ReportFormat;
import com.gogidix.rapidassist.ai.report.domain.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a report generation record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationDto {

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
    private Integer recordCount;
    private String errorMessage;
    private String requestedBy;
    private String createdBy;
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    private Integer retryCount;
    private Integer maxRetries;
}
