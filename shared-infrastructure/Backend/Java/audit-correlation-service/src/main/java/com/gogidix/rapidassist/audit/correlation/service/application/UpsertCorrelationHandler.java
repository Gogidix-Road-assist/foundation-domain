package com.gogidix.rapidassist.audit.correlation.service.application;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.UpsertCorrelationCommand;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.out.CorrelationStore;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UpsertCorrelationHandler implements UpsertCorrelationCommand {

    private final CorrelationStore store;
    private final CorrelationRetentionProperties retentionProperties;

    public UpsertCorrelationHandler(CorrelationStore store, CorrelationRetentionProperties retentionProperties) {
        this.store = store;
        this.retentionProperties = retentionProperties;
    }

    @Override
    public void upsert(CorrelationRecord record) {
        Instant createdAt = record.createdAt() == null ? Instant.now() : record.createdAt();
        Instant expiresAt = createdAt.plus(retentionProperties.getRetention());
        CorrelationRecord normalized = new CorrelationRecord(
                record.tenantId(),
                record.country(),
                record.correlationId(),
                createdAt,
                record.tags()
        );
        store.upsert(normalized, expiresAt);
    }
}
