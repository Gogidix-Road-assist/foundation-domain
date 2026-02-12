package com.gogidix.rapidassist.logging.aggregation.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record LogEvent(
        String tenantId,
        String country,
        String correlationId,
        Instant timestamp,
        String level,
        String logger,
        String message,
        Map<String, String> attributes
) {
}
