package com.gogidix.rapidassist.metrics.telemetry.service.domain.port.out;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;

import java.time.Instant;
import java.util.List;

public interface TelemetryEventStore {

    void append(TelemetryEvent event, Instant expiresAt);

    List<TelemetryEvent> query(
            String tenantId,
            String correlationId,
            Instant from,
            Instant to,
            String type,
            String name,
            int limit
    );
}
