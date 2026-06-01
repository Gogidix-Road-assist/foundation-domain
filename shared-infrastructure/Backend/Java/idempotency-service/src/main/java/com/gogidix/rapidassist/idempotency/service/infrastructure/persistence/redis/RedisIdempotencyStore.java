package com.gogidix.rapidassist.idempotency.service.infrastructure.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.idempotency.service.domain.model.EnhancedIdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.port.out.IdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnClass(RedisTemplate.class)
public class RedisIdempotencyStore implements IdempotencyStore {

    private static final Logger logger = LoggerFactory.getLogger(RedisIdempotencyStore.class);
    private static final String KEY_PREFIX = "idempotency:";
    private static final String LOCK_PREFIX = "idempotency:lock:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisScript<Boolean> reserveScript;
    private final RedisScript<Boolean> updateScript;

    public RedisIdempotencyStore(RedisTemplate<String, String> redisTemplate,
                                ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        // Lua script for atomic reserve operation
        this.reserveScript = RedisScript.of("""
            local key = KEYS[1]
            local lockKey = KEYS[2]
            local ttl = tonumber(ARGV[1])
            local lockTtl = tonumber(ARGV[2])

            if redis.call('EXISTS', key) == 1 then
                return false
            end

            redis.call('SETEX', key, ttl, ARGV[3])
            redis.call('SETEX', lockKey, lockTtl, ARGV[4])

            return true
            """, Boolean.class);

        // Lua script for atomic update operation
        this.updateScript = RedisScript.of("""
            local key = KEYS[1]
            local lockKey = KEYS[2]
            local lockValue = ARGV[1]
            local newValue = ARGV[2]
            local ttl = tonumber(ARGV[3])

            local currentLock = redis.call('GET', lockKey)
            if currentLock ~= lockValue then
                return false
            end

            redis.call('SET', key, newValue)
            redis.call('EXPIRE', key, ttl)
            redis.call('DEL', lockKey)

            return true
            """, Boolean.class);

        logger.info("RedisIdempotencyStore initialized");
    }

    @Override
    public Optional<IdempotencyRecord> find(String tenantId, String key) {
        try {
            String redisKey = buildKey(tenantId, key);
            String json = redisTemplate.opsForValue().get(redisKey);

            if (json == null) {
                logger.debug("Idempotency record not found: tenant={}, key={}", tenantId, key);
                return Optional.empty();
            }

            // Try to deserialize as enhanced record first
            try {
                EnhancedIdempotencyRecord enhanced = objectMapper.readValue(json, EnhancedIdempotencyRecord.class);

                // Convert to basic record for backward compatibility
                IdempotencyRecord basic = new IdempotencyRecord(
                    enhanced.tenantId(),
                    enhanced.key(),
                    convertStatus(enhanced.status()),
                    enhanced.createdAt()
                );

                logger.debug("Retrieved idempotency record: tenant={}, key={}, status={}",
                    tenantId, key, enhanced.status());
                return Optional.of(basic);

            } catch (JsonProcessingException e) {
                // Fall back to basic record deserialization
                IdempotencyRecord record = objectMapper.readValue(json, IdempotencyRecord.class);
                logger.debug("Retrieved basic idempotency record: tenant={}, key={}", tenantId, key);
                return Optional.of(record);
            }

        } catch (Exception e) {
            logger.error("Error finding idempotency record: tenant={}, key={}", tenantId, key, e);
            throw new RuntimeException("Failed to find idempotency record", e);
        }
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        try {
            String redisKey = buildKey(record.tenantId(), record.key());
            String json = objectMapper.writeValueAsString(record);

            // Set with expiration based on status
            Duration ttl = record.status() == com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus.COMPLETED
                ? Duration.ofHours(24)
                : Duration.ofHours(1);

            redisTemplate.opsForValue().set(redisKey, json, ttl);

            logger.debug("Saved idempotency record: tenant={}, key={}, status={}",
                record.tenantId(), record.key(), record.status());
            return record;

        } catch (Exception e) {
            logger.error("Error saving idempotency record: tenant={}, key={}",
                record.tenantId(), record.key(), e);
            throw new RuntimeException("Failed to save idempotency record", e);
        }
    }

    // Enhanced operations
    public boolean reserveIdempotencyKey(String tenantId, String key, String requestId, Duration ttl) {
        try {
            String redisKey = buildKey(tenantId, key);
            String lockKey = buildLockKey(tenantId, key);
            String lockValue = UUID.randomUUID().toString();

            Boolean result = redisTemplate.execute(
                reserveScript,
                List.of(redisKey, lockKey),
                String.valueOf(ttl.getSeconds()),
                String.valueOf(300), // 5 minute lock TTL
                requestId,
                lockValue
            );

            if (Boolean.TRUE.equals(result)) {
                logger.debug("Reserved idempotency key: tenant={}, key={}, requestId={}",
                    tenantId, key, requestId);
            } else {
                logger.debug("Failed to reserve idempotency key (already exists): tenant={}, key={}",
                    tenantId, key);
            }

            return Boolean.TRUE.equals(result);

        } catch (Exception e) {
            logger.error("Error reserving idempotency key: tenant={}, key={}", tenantId, key, e);
            throw new RuntimeException("Failed to reserve idempotency key", e);
        }
    }

    public Optional<EnhancedIdempotencyRecord> getEnhancedRecord(String tenantId, String key) {
        try {
            String redisKey = buildKey(tenantId, key);
            String json = redisTemplate.opsForValue().get(redisKey);

            if (json == null) {
                return Optional.empty();
            }

            EnhancedIdempotencyRecord record = objectMapper.readValue(json, EnhancedIdempotencyRecord.class);

            // Check if expired
            if (record.isExpired()) {
                deleteRecord(tenantId, key);
                return Optional.empty();
            }

            return Optional.of(record);

        } catch (Exception e) {
            logger.error("Error retrieving enhanced idempotency record: tenant={}, key={}",
                tenantId, key, e);
            return Optional.empty();
        }
    }

    public boolean saveEnhancedRecord(EnhancedIdempotencyRecord record) {
        try {
            String redisKey = buildKey(record.tenantId(), record.key());
            String json = objectMapper.writeValueAsString(record);

            Duration ttl = Duration.between(Instant.now(), record.expiresAt());
            redisTemplate.opsForValue().set(redisKey, json, ttl);

            logger.debug("Saved enhanced idempotency record: tenant={}, key={}, status={}",
                record.tenantId(), record.key(), record.status());
            return true;

        } catch (Exception e) {
            logger.error("Error saving enhanced idempotency record: tenant={}, key={}",
                record.tenantId(), record.key(), e);
            return false;
        }
    }

    public void deleteRecord(String tenantId, String key) {
        String redisKey = buildKey(tenantId, key);
        String lockKey = buildLockKey(tenantId, key);

        redisTemplate.delete(List.of(redisKey, lockKey));
        logger.debug("Deleted idempotency record: tenant={}, key={}", tenantId, key);
    }

    public boolean updateRecordWithLock(EnhancedIdempotencyRecord record, String lockValue) {
        try {
            String redisKey = buildKey(record.tenantId(), record.key());
            String lockKey = buildLockKey(record.tenantId(), record.key());
            String json = objectMapper.writeValueAsString(record);

            Duration ttl = Duration.between(Instant.now(), record.expiresAt());

            Boolean result = redisTemplate.execute(
                updateScript,
                List.of(redisKey, lockKey),
                lockValue,
                json,
                String.valueOf(ttl.getSeconds())
            );

            if (Boolean.TRUE.equals(result)) {
                logger.debug("Updated idempotency record with lock: tenant={}, key={}",
                    record.tenantId(), record.key());
            }

            return Boolean.TRUE.equals(result);

        } catch (Exception e) {
            logger.error("Error updating idempotency record with lock: tenant={}, key={}",
                record.tenantId(), record.key(), e);
            return false;
        }
    }

    private String buildKey(String tenantId, String key) {
        return KEY_PREFIX + tenantId + ":" + key;
    }

    private String buildLockKey(String tenantId, String key) {
        return LOCK_PREFIX + tenantId + ":" + key;
    }

    private com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus convertStatus(
            EnhancedIdempotencyRecord.IdempotencyStatus status) {
        return switch (status) {
            case RESERVED, IN_PROGRESS -> com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus.RESERVED;
            case COMPLETED -> com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus.COMPLETED;
            case FAILED, TIMEOUT, CANCELLED -> com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus.RESERVED; // Map to reserved for backward compatibility
        };
    }
}