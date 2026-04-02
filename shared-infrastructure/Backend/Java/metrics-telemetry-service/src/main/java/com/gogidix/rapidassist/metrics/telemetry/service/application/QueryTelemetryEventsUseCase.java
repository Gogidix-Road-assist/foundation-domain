package com.gogidix.rapidassist.metrics.telemetry.service.application;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in.QueryTelemetryEventsQuery;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.out.TelemetryEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class QueryTelemetryEventsUseCase implements QueryTelemetryEventsQuery {

    private final TelemetryEventStore store;

    public QueryTelemetryEventsUseCase(TelemetryEventStore store) {
        this.store = store;
    }

    @Override
    public List<TelemetryEvent> query(String tenantId, String correlationId, Instant from, Instant to, String type, String name, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);
        return store.query(tenantId, correlationId, from, to, type, name, safeLimit);
    }
}
