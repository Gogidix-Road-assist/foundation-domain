package com.gogidix.rapidassist.orchestration.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for Report responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String id;
    private String reportId;
    private String name;
    private String description;
    private ReportTypeDTO reportType;
    private OutputFormatDTO outputFormat;
    private ReportStatusDTO status;
    private String templateId;
    private Map<String, Object> parameters;
    private String filePath;
    private Long fileSizeBytes;
    private Integer recordCount;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private Boolean isScheduled;
    private String scheduleId;
    private String generatedBy;
    private Map<String, Object> metadata;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ReportTypeDTO {
        DISPATCH, FLEET_UTILIZATION, PERFORMANCE, COMPLIANCE,
        MAINTENANCE, FINANCIAL, CUSTOMER_SATISFACTION, CUSTOM
    }

    public enum OutputFormatDTO {
        PDF, EXCEL, CSV, HTML, JSON
    }

    public enum ReportStatusDTO {
        PENDING, GENERATING, COMPLETED, FAILED, EXPIRED
    }
}
