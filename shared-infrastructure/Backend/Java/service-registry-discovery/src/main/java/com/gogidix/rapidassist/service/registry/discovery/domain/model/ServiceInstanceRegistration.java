package com.gogidix.rapidassist.service.registry.discovery.domain.model;

import java.time.Instant;
import java.util.Map;

public record ServiceInstanceRegistration(
        String tenantId,
        String serviceName,
        String instanceId,
        String baseUrl,
        Instant registeredAt,
        Map<String, String> metadata
) {
}
