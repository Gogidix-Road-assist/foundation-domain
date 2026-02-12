package com.gogidix.rapidassist.idempotency.service.domain.model;

import java.time.Instant;

public record IdempotencyRecord(
        String tenantId,
        String key,
        IdempotencyStatus status,
        Instant createdAt
) {
}
