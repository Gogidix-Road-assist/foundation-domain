package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Redis implementation of IdempotencyStore.
 * Stores idempotency records in Redis with configurable TTL.
 *
 * <p>This implementation provides:
 * <ul>
 *   <li>Fast in-memory access</li>
 *   <li>Automatic expiration via TTL</li>
 *   <li>Distributed support</li>
 * </ul>
 *
 * <p>TTL is configured via idempotency.ttl property (default: 24 hours).
 */
public class RedisIdempotencyStore implements IdempotencyStore {

    private static final Logger log = LoggerFactory.getLogger(RedisIdempotencyStore.class);

    private static final String KEY_PREFIX = "idempotency:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public RedisIdempotencyStore(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = ttl;
    }

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        try {
            String redisKey = buildKey(key);
            Object value = redisTemplate.opsForValue().get(redisKey);

            if (value == null) {
                return Optional.empty();
            }

            if (value instanceof IdempotencyRecord) {
                return Optional.of((IdempotencyRecord) value);
            }

            // Handle deserialization from JSON
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) value;
            return Optional.of(deserializeRecord(map));

        } catch (Exception e) {
            log.error("Failed to retrieve idempotency record from Redis: key={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public IdempotencyRecord saveNew(IdempotencyRecord record) {
        try {
            String redisKey = buildKey(record.key());
            redisTemplate.opsForValue().set(redisKey, record, ttl);
            log.debug("Saved new idempotency record to Redis: key={}, status={}",
                    record.key(), record.status());
            return record;
        } catch (Exception e) {
            log.error("Failed to save idempotency record to Redis: key={}", record.key(), e);
            throw new RuntimeException("Failed to save idempotency record", e);
        }
    }

    @Override
    public IdempotencyRecord update(IdempotencyRecord record) {
        try {
            String redisKey = buildKey(record.key());
            // Update the record with new TTL
            redisTemplate.opsForValue().set(redisKey, record, ttl);
            log.debug("Updated idempotency record in Redis: key={}, status={}",
                    record.key(), record.status());
            return record;
        } catch (Exception e) {
            log.error("Failed to update idempotency record in Redis: key={}", record.key(), e);
            throw new RuntimeException("Failed to update idempotency record", e);
        }
    }

    /**
     * Delete an idempotency record.
     *
     * @param key the idempotency key
     * @return true if the record was deleted, false otherwise
     */
    public boolean delete(String key) {
        try {
            String redisKey = buildKey(key);
            Boolean deleted = redisTemplate.delete(redisKey);
            log.debug("Deleted idempotency record from Redis: key={}, deleted={}", key, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.error("Failed to delete idempotency record from Redis: key={}", key, e);
            return false;
        }
    }

    /**
     * Check if a record exists.
     *
     * @param key the idempotency key
     * @return true if the record exists, false otherwise
     */
    public boolean exists(String key) {
        try {
            String redisKey = buildKey(key);
            Boolean exists = redisTemplate.hasKey(redisKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Failed to check idempotency record existence in Redis: key={}", key, e);
            return false;
        }
    }

    private String buildKey(String key) {
        return KEY_PREFIX + key;
    }

    private IdempotencyRecord deserializeRecord(java.util.Map<String, Object> map) {
        return new IdempotencyRecord(
                (String) map.get("key"),
                Status.valueOf((String) map.get("status")),
                Instant.parse((String) map.get("createdAt")),
                Instant.parse((String) map.get("updatedAt")),
                (String) map.get("requestHash"),
                (Integer) map.get("responseStatus"),
                (String) map.get("responseHash")
        );
    }
}
