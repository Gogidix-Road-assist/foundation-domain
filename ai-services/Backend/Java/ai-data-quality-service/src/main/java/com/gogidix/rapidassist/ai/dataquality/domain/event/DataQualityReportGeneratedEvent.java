package com.gogidix.rapidassist.ai.dataquality.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a data quality report is generated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityReportGeneratedEvent {

    private UUID eventId;
    private String tenantId;
    private UUID reportId;
    private String reportName;
    private String reportType;
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private double overallQualityScore;
    private int totalChecks;
    private int totalIssues;
    private LocalDateTime occurredAt;
    private String generatedBy;
}
