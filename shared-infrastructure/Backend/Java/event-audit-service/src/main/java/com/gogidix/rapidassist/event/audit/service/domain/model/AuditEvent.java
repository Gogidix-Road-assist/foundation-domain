package com.gogidix.rapidassist.event.audit.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record AuditEvent(
        String tenantId,
        String country,
        String correlationId,
        Instant occurredAt,
        String eventType,
        String entityType,
        String entityId,
        Map<String, Object> payload
) {
}
