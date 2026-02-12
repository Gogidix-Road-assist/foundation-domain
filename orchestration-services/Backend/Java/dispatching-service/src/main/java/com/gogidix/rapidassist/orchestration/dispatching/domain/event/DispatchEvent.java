package com.gogidix.rapidassist.orchestration.dispatching.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Base domain event for dispatch-related events
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchEvent {

    private String eventId;
    private String eventType;
    private String dispatchId;
    private String tenantId;
    private String correlationId;
    private Instant timestamp;
    private Integer version;

    public DispatchEvent(String eventType, String dispatchId, String tenantId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.dispatchId = dispatchId;
        this.tenantId = tenantId;
        this.timestamp = Instant.now();
        this.version = 1;
    }
}
