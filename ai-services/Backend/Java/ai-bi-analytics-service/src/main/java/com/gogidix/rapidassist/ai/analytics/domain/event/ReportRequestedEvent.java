package com.gogidix.rapidassist.ai.analytics.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a report generation is requested
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestedEvent {

    private UUID eventId;
    private UUID reportId;
    private String tenantId;
    private String reportName;
    private String reportType;
    private LocalDateTime occurredAt;
    private String requestedBy;
    private LocalDateTime scheduledFor;
}
