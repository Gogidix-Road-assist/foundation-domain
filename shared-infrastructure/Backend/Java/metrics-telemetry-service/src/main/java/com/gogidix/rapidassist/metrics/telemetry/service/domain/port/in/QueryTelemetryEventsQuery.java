package com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;

import java.time.Instant;
import java.util.List;

public interface QueryTelemetryEventsQuery {

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
