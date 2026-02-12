package com.gogidix.rapidassist.event.audit.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;
import com.gogidix.rapidassist.event.audit.service.domain.port.out.AuditEventStore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class NoOpAuditEventStore implements AuditEventStore {

    @Override
    public String append(AuditEvent event, Instant expiresAt) {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<AuditEvent> query(String tenantId, String country, String entityType, String entityId, Instant from, Instant to, int limit) {
        return List.of();
    }
}
