package com.gogidix.rapidassist.event.audit.service.application.usecase;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;
import com.gogidix.rapidassist.event.audit.service.domain.port.in.AppendAuditEventCommand;
import com.gogidix.rapidassist.event.audit.service.domain.port.out.AuditEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AppendAuditEventUseCase implements AppendAuditEventCommand {

    private final AuditEventStore store;
    private final AuditRetentionProperties retentionProperties;

    public AppendAuditEventUseCase(AuditEventStore store, AuditRetentionProperties retentionProperties) {
        this.store = store;
        this.retentionProperties = retentionProperties;
    }

    @Override
    public String append(AuditEvent event) {
        Instant occurredAt = event.occurredAt() == null ? Instant.now() : event.occurredAt();
        Instant expiresAt = occurredAt.plus(retentionProperties.getRetention());
        AuditEvent normalized = new AuditEvent(
                event.tenantId(),
                event.country(),
                event.correlationId(),
                occurredAt,
                event.eventType(),
                event.entityType(),
                event.entityId(),
                event.payload()
        );
        return store.append(normalized, expiresAt);
    }
}
