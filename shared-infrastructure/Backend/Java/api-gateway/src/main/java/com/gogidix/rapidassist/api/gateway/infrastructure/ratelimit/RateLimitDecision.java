package com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit;

import java.time.Duration;

public record RateLimitDecision(
        boolean allowed,
        Duration retryAfter
) {

    public static RateLimitDecision allow() {
        return new RateLimitDecision(true, null);
    }

    public static RateLimitDecision deny(Duration retryAfter) {
        return new RateLimitDecision(false, retryAfter);
    }
}
