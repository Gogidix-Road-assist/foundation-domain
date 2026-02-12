package com.gogidix.rapidassist.event.audit.service.domain.port.in;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;

import java.time.Instant;
import java.util.List;

public interface QueryAuditEventsQuery {

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
