package com.gogidix.rapidassist.rate.limiting.service.domain.port.out;

import java.time.Instant;
import java.util.Optional;

public interface RateLimitCounterStore {

    Optional<Long> incrementAndGetCount(String tenantId, String key, Instant windowStart, Instant expiresAt);
}
