package com.gogidix.rapidassist.alerting.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record AlertEvent(
        String tenantId,
        String country,
        String correlationId,
        Instant occurredAt,
        String ruleId,
        String severity,
        String message,
        Map<String, String> attributes
) {
}
