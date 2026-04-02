package com.gogidix.rapidassist.ai.analytics.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when an analytics report is generated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGeneratedEvent {

    private UUID eventId;
    private UUID reportId;
    private String tenantId;
    private String reportName;
    private String reportType;
    private LocalDateTime occurredAt;
    private String triggeredBy;
    private Long executionTimeMs;
    private Integer recordCount;
    private String status;
}
