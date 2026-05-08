package com.gogidix.rapidassist.service.health.monitor.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record ServiceHealthReport(
        String tenantId,
        String country,
        String serviceName,
        String instanceId,
        String status,
        Instant checkedAt,
        Map<String, String> details
) {
}
