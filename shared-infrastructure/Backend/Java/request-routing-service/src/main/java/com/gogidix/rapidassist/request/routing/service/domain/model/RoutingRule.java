package com.gogidix.rapidassist.request.routing.service.domain.model;

import java.time.Instant;

public record RoutingRule(
        String tenantId,
        String routeKey,
        String destinationBaseUrl,
        Instant updatedAt
) {
}
