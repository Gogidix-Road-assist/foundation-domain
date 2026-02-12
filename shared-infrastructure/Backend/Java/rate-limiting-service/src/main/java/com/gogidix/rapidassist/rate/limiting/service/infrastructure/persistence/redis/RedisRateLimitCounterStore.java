package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.redis;

import com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.RateLimitingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based implementation of rate limiting counter store.
 * Supports multiple algorithms: Token Bucket, Sliding Window, and Fixed Window.
 */
public class RedisRateLimitCounterStore {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimitCounterStore.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitingProperties properties;

    // Lua scripts for atomic operations
    private static final String SLIDING_WINDOW_SCRIPT = """
        local key = KEYS[1]
        local now = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        local pipeline_id = ARGV[3]

        -- Remove expired entries
        redis.call('zremrangebyscore', key, 0, now - window)

        -- Get current count
        local count = redis.call('zcard', key)

        -- Add new request
        redis.call('zadd', key, now, pipeline_id)

        -- Set expiration
        redis.call('expire', key, math.ceil(window))

        return count
        """;

    private static final String TOKEN_BUCKET_SCRIPT = """
        local key = KEYS[1]
        local tokens = tonumber(ARGV[1])
        local capacity = tonumber(ARGV[2])
        local refill_rate = tonumber(ARGV[3])
        local interval = tonumber(ARGV[4])
        local now = tonumber(ARGV[5])

        local bucket = redis.call('hmget', key, 'tokens', 'last_refill')
        local current_tokens = tonumber(bucket[1]) or capacity
        local last_refill = tonumber(bucket[2]) or now

        -- Calculate tokens to add based on elapsed time
        local elapsed = now - last_refill
        local tokens_to_add = math.floor(elapsed / interval * refill_rate)
        current_tokens = math.min(current_tokens + tokens_to_add, capacity)

        if current_tokens >= tokens then
            -- Consume tokens
            current_tokens = current_tokens - tokens
            redis.call('hmset', key, 'tokens', current_tokens, 'last_refill', now)
            redis.call('expire', key, math.ceil(capacity / refill_rate * interval))
            return {1, current_tokens}
        else
            -- Not enough tokens
            redis.call('hmset', key, 'tokens', current_tokens, 'last_refill', now)
            redis.call('expire', key, math.ceil(capacity / refill_rate * interval))
            return {0, current_tokens}
        end
        """;

    private final RedisScript<Long> slidingWindowScript;
    private final RedisScript<List<Long>> tokenBucketScript;

    public RedisRateLimitCounterStore(RedisTemplate<String, String> redisTemplate,
                                     RateLimitingProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.slidingWindowScript = new DefaultRedisScript<>(SLIDING_WINDOW_SCRIPT, Long.class);
        this.tokenBucketScript = new DefaultRedisScript<>(TOKEN_BUCKET_SCRIPT, (Class<List<Long>>) (Class<?>) List.class);

        logger.info("RedisRateLimitCounterStore initialized with algorithm: {}", properties.getAlgorithm());
    }

    public Optional<Long> incrementAndGetCount(String tenantId, String key, Instant windowStart, Instant expiresAt) {
        try {
            String fullKey = buildKey(tenantId, key);

            switch (properties.getAlgorithmTypeLower()) {
                case "sliding_window":
                    return handleSlidingWindow(fullKey, windowStart);

                case "token_bucket":
                    return handleTokenBucket(fullKey);

                case "fixed_window":
                default:
                    return handleFixedWindow(fullKey, expiresAt);
            }

        } catch (Exception e) {
            logger.error("Failed to increment rate limit counter for key: {}", key, e);
            return Optional.empty();
        }
    }

    private Optional<Long> handleSlidingWindow(String key, Instant windowStart) {
        long windowMs = properties.getWindowSizeMs();
        String pipelineId = windowStart.toEpochMilli() + "-" + Thread.currentThread().getId();

        Long count = redisTemplate.execute(
            slidingWindowScript,
            Collections.singletonList(key),
            String.valueOf(System.currentTimeMillis()),
            String.valueOf(windowMs),
            pipelineId
        );

        return Optional.ofNullable(count);
    }

    private Optional<Long> handleTokenBucket(String key) {
        List<Long> result = redisTemplate.execute(
            tokenBucketScript,
            Collections.singletonList(key),
            "1", // tokens to consume
            String.valueOf(properties.getCapacity()),
            String.valueOf(properties.getRefillRate()),
            String.valueOf(properties.getRefillIntervalMs()),
            String.valueOf(System.currentTimeMillis())
        );

        if (result != null && result.size() >= 2) {
            // Return 0 if allowed, 1 if denied (inverse of what script returns)
            return Optional.of(result.get(0) == 1 ? 0L : 1L);
        }

        return Optional.empty();
    }

    private Optional<Long> handleFixedWindow(String key, Instant expiresAt) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if (ttl == null || ttl < 0) {
            // Key doesn't exist or has no expiration
            redisTemplate.opsForValue().set(key, "1");
            if (expiresAt != null) {
                long ttlSeconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
                if (ttlSeconds > 0) {
                    redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
                }
            }
            return Optional.of(1L);
        } else {
            // Increment existing counter
            Long count = redisTemplate.opsForValue().increment(key);
            return Optional.ofNullable(count);
        }
    }

    /**
     * Reset rate limit for a key
     */
    public void reset(String tenantId, String key) {
        String fullKey = buildKey(tenantId, key);
        redisTemplate.delete(fullKey);
        logger.debug("Rate limit reset for key: {}", fullKey);
    }

    /**
     * Get current count without incrementing
     */
    public Optional<Long> getCurrentCount(String tenantId, String key) {
        try {
            String fullKey = buildKey(tenantId, key);

            switch (properties.getAlgorithmTypeLower()) {
                case "sliding_window":
                    Long count = redisTemplate.opsForZSet().count(
                        fullKey,
                        System.currentTimeMillis() - properties.getWindowSizeMs(),
                        Double.MAX_VALUE
                    );
                    return Optional.ofNullable(count);

                case "token_bucket":
                    List<Object> bucket = redisTemplate.opsForHash().multiGet(fullKey, List.of("tokens"));
                    if (bucket != null && bucket.get(0) != null) {
                        return Optional.of(properties.getCapacity() - Long.parseLong(bucket.get(0).toString()));
                    }
                    return Optional.of(properties.getCapacity());

                case "fixed_window":
                default:
                    String value = redisTemplate.opsForValue().get(fullKey);
                    return value != null ? Optional.of(Long.parseLong(value)) : Optional.of(0L);
            }

        } catch (Exception e) {
            logger.error("Failed to get current count for key: {}", key, e);
            return Optional.empty();
        }
    }

    /**
     * Check if key is currently rate limited
     */
    public boolean isRateLimited(String tenantId, String key) {
        Optional<Long> currentCount = getCurrentCount(tenantId, key);
        return currentCount.map(count -> count >= properties.getLimit()).orElse(false);
    }

    /**
     * Get time until next reset (for fixed window)
     */
    public Optional<Long> getTtl(String tenantId, String key) {
        String fullKey = buildKey(tenantId, key);
        Long ttl = redisTemplate.getExpire(fullKey, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? Optional.of(ttl) : Optional.empty();
    }

    private String buildKey(String tenantId, String key) {
        return String.format("%s:rate-limit:%s:%s",
                           properties.getKeyPrefix(),
                           tenantId,
                           key);
    }
}