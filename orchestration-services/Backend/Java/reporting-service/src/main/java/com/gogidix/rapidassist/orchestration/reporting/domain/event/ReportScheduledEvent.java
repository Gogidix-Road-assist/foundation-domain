package com.gogidix.rapidassist.orchestration.reporting.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event published when a report schedule is triggered.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportScheduledEvent {
    private String eventType;
    private String aggregateId;
    private String tenantId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;

    private String scheduleId;
    private String scheduleName;
    private String templateId;

    public static ReportScheduledEvent create(String scheduleId, String scheduleName,
                                            String templateId, String tenantId) {
        return ReportScheduledEvent.builder()
                .eventType("ReportScheduled")
                .aggregateId(scheduleId)
                .tenantId(tenantId)
                .timestamp(LocalDateTime.now())
                .scheduleId(scheduleId)
                .scheduleName(scheduleName)
                .templateId(templateId)
                .build();
    }
}
