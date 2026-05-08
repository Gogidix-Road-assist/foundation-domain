package com.gogidix.rapidassist.event.audit.service.domain.port.out;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;

import java.time.Instant;
import java.util.List;

public interface AuditEventStore {

    String append(AuditEvent event, Instant expiresAt);

    List<AuditEvent> query(
            String tenantId,
            String country,
            String entityType,
            String entityId,
            Instant from,
            Instant to,
            int limit
    );
}
