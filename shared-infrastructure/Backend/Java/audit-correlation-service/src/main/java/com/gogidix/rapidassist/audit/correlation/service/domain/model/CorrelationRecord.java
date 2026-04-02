package com.gogidix.rapidassist.audit.correlation.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record CorrelationRecord(
        String tenantId,
        String country,
        String correlationId,
        Instant createdAt,
        Map<String, String> tags
) {
}
