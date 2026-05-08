package com.gogidix.rapidassist.audit.correlation.service.application;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.GetCorrelationQuery;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.out.CorrelationStore;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetCorrelationHandler implements GetCorrelationQuery {

    private final CorrelationStore store;

    public GetCorrelationHandler(CorrelationStore store) {
        this.store = store;
    }

    @Override
    public Optional<CorrelationRecord> get(String tenantId, String correlationId) {
        return store.get(tenantId, correlationId);
    }
}
