package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.rate.limiting.service.domain.port.out.RateLimitCounterStore;

import java.time.Instant;
import java.util.Optional;

public class NoOpRateLimitCounterStore implements RateLimitCounterStore {

    @Override
    public Optional<Long> incrementAndGetCount(String tenantId, String key, Instant windowStart, Instant expiresAt) {
        return Optional.empty();
    }
}
