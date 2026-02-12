package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event representation for alert lifecycle events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEvent {

    private String eventType;
    private String alertId;
    private String requestId;
    private String tenantId;

    private String type;
    private String severity;
    private String status;

    private String assignedTo;
    private String acknowledgedBy;
    private String resolvedBy;

    private Integer escalationLevel;

    private Instant timestamp;
}
