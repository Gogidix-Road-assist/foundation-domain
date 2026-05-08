package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.comprehensive;

import com.gogidix.rapidassist.rate.limiting.service.domain.port.out.RateLimitCounterStore;
import com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.RateLimitingProperties;
import com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.redis.RedisRateLimitCounterStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Optional;

/**
 * Comprehensive rate limit counter store that delegates to Redis implementation.
 * This acts as an adapter between the domain port and the Redis implementation.
 */
public class ComprehensiveRateLimitCounterStore implements RateLimitCounterStore {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveRateLimitCounterStore.class);

    private final RedisRateLimitCounterStore redisStore;
    private final RateLimitingProperties properties;

    public ComprehensiveRateLimitCounterStore(RedisTemplate<String, String> redisTemplate,
                                             RateLimitingProperties properties) {
        this.properties = properties;
        this.redisStore = new RedisRateLimitCounterStore(redisTemplate, properties);
        logger.info("ComprehensiveRateLimitCounterStore initialized with algorithm: {}",
                   properties.getAlgorithm());
    }

    @Override
    public Optional<Long> incrementAndGetCount(String tenantId, String key, Instant windowStart, Instant expiresAt) {
        return redisStore.incrementAndGetCount(tenantId, key, windowStart, expiresAt);
    }

    /**
     * Additional convenience methods
     */
    public void reset(String tenantId, String key) {
        redisStore.reset(tenantId, key);
    }

    public Optional<Long> getCurrentCount(String tenantId, String key) {
        return redisStore.getCurrentCount(tenantId, key);
    }

    public boolean isRateLimited(String tenantId, String key) {
        return redisStore.isRateLimited(tenantId, key);
    }

    public Optional<Long> getTtl(String tenantId, String key) {
        return redisStore.getTtl(tenantId, key);
    }
}