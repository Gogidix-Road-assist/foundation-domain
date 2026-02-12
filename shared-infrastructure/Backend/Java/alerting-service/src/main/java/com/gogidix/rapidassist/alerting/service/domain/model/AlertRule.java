package com.gogidix.rapidassist.alerting.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record AlertRule(
        String tenantId,
        String ruleId,
        String name,
        String severity,
        boolean enabled,
        Map<String, String> conditions,
        Instant createdAt
) {
}
