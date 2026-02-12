package com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.out.CorrelationStore;

import java.time.Instant;
import java.util.Optional;

public class NoOpCorrelationStore implements CorrelationStore {

    @Override
    public void upsert(CorrelationRecord record, Instant expiresAt) {
    }

    @Override
    public Optional<CorrelationRecord> get(String tenantId, String correlationId) {
        return Optional.empty();
    }
}
