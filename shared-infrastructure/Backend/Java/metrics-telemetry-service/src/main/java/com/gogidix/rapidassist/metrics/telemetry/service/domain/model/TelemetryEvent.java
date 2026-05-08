package com.gogidix.rapidassist.metrics.telemetry.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record TelemetryEvent(
        String tenantId,
        String country,
        String correlationId,
        Instant timestamp,
        String type,
        String name,
        Double value,
        Map<String, String> attributes
) {
}
