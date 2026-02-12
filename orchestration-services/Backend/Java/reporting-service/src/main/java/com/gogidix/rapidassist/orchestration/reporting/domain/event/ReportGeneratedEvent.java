package com.gogidix.rapidassist.orchestration.reporting.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event published when a report is generated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneratedEvent {
    private String eventType;
    private String aggregateId;
    private String tenantId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;

    private String reportId;
    private String reportType;
    private String outputFormat;
    private String templateId;
    private Long fileSize;
    private Integer recordCount;
    private String generatedBy;

    public static ReportGeneratedEvent create(String reportId, String reportType, String outputFormat,
                                             String templateId, Long fileSize, Integer recordCount,
                                             String generatedBy, String tenantId) {
        return ReportGeneratedEvent.builder()
                .eventType("ReportGenerated")
                .aggregateId(reportId)
                .tenantId(tenantId)
                .timestamp(LocalDateTime.now())
                .reportId(reportId)
                .reportType(reportType)
                .outputFormat(outputFormat)
                .templateId(templateId)
                .fileSize(fileSize)
                .recordCount(recordCount)
                .generatedBy(generatedBy)
                .build();
    }
}
