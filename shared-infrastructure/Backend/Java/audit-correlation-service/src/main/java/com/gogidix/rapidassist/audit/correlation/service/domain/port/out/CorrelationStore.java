package com.gogidix.rapidassist.audit.correlation.service.domain.port.out;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;

import java.time.Instant;
import java.util.Optional;

public interface CorrelationStore {

    void upsert(CorrelationRecord record, Instant expiresAt);

    Optional<CorrelationRecord> get(String tenantId, String correlationId);
}
