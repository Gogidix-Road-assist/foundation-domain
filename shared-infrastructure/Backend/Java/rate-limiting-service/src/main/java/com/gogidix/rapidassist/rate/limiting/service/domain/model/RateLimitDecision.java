package com.gogidix.rapidassist.rate.limiting.service.domain.model;

import java.time.Instant;

public record RateLimitDecision(
        boolean allowed,
        long limit,
        Long currentCount,
        Long remaining,
        Instant resetAt
) {
}
