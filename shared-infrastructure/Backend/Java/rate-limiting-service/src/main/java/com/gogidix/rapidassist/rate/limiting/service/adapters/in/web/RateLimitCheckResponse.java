package com.gogidix.rapidassist.rate.limiting.service.adapters.in.web;

import java.time.Instant;

public record RateLimitCheckResponse(
        boolean allowed,
        long limit,
        Long currentCount,
        Long remaining,
        Instant resetAt
) {
}
