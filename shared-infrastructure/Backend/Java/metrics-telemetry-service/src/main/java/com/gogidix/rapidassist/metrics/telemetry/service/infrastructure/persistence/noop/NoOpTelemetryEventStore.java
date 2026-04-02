package com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.out.TelemetryEventStore;

import java.time.Instant;
import java.util.List;

public class NoOpTelemetryEventStore implements TelemetryEventStore {

    @Override
    public void append(TelemetryEvent event, Instant expiresAt) {
    }

    @Override
    public List<TelemetryEvent> query(String tenantId, String correlationId, Instant from, Instant to, String type, String name, int limit) {
        return List.of();
    }
}
