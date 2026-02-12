package com.gogidix.rapidassist.audit.correlation.service.domain.port.in;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;

import java.util.Optional;

public interface GetCorrelationQuery {

    Optional<CorrelationRecord> get(String tenantId, String correlationId);
}
