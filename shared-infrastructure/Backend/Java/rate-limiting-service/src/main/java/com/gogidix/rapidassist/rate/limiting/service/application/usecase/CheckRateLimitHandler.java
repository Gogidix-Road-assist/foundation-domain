package com.gogidix.rapidassist.rate.limiting.service.application.usecase;

import com.gogidix.rapidassist.rate.limiting.service.domain.model.RateLimitDecision;
import com.gogidix.rapidassist.rate.limiting.service.domain.port.in.CheckRateLimitCommand;
import com.gogidix.rapidassist.rate.limiting.service.domain.port.out.RateLimitCounterStore;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
public class CheckRateLimitHandler implements CheckRateLimitCommand {

    private final RateLimitCounterStore counterStore;

    public CheckRateLimitHandler(RateLimitCounterStore counterStore) {
        this.counterStore = counterStore;
    }

    @Override
    public RateLimitDecision check(String tenantId, String key, long limit, Duration window) {
        if (tenantId == null || tenantId.isBlank()) {
            return new RateLimitDecision(true, limit, null, null, null);
        }
        if (key == null || key.isBlank()) {
            return new RateLimitDecision(true, limit, null, null, null);
        }
        if (limit <= 0) {
            return new RateLimitDecision(true, limit, null, null, null);
        }
        if (window == null || window.isNegative() || window.isZero()) {
            return new RateLimitDecision(true, limit, null, null, null);
        }

        Instant now = Instant.now();
        long windowSeconds = window.getSeconds();
        long nowEpoch = now.getEpochSecond();
        long windowStartEpoch = (nowEpoch / windowSeconds) * windowSeconds;
        Instant windowStart = Instant.ofEpochSecond(windowStartEpoch);
        Instant resetAt = Instant.ofEpochSecond(windowStartEpoch + windowSeconds);

        Optional<Long> countOpt = counterStore.incrementAndGetCount(tenantId, key, windowStart, resetAt);
        if (countOpt.isEmpty()) {
            return new RateLimitDecision(true, limit, null, null, resetAt);
        }

        long count = countOpt.get();
        boolean allowed = count <= limit;
        long remaining = Math.max(0, limit - count);
        return new RateLimitDecision(allowed, limit, count, remaining, resetAt);
    }
}
