package com.gogidix.rapidassist.idempotency.service.adapters.in.web;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus;

import java.time.Instant;

public record IdempotencyResponse(
        String key,
        IdempotencyStatus status,
        Instant createdAt
) {
}
