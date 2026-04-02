package com.gogidix.rapidassist.audit.correlation.service.application;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.GetCorrelationQuery;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.out.CorrelationStore;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetCorrelationUseCase implements GetCorrelationQuery {

    private final CorrelationStore store;

    public GetCorrelationUseCase(CorrelationStore store) {
        this.store = store;
    }

    @Override
    public Optional<CorrelationRecord> get(String tenantId, String correlationId) {
        return store.get(tenantId, correlationId);
    }
}
