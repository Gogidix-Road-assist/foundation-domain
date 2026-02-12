package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.redis;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Redis Cache: RedisPermissionCache
 *
 * Caches permissions for fast access control checks.
 * Cache key pattern: permission:{tenantId}:{subjectId}:{resource}:{action}
 *
 * This is an ADAPTER in the hexagonal architecture.
 */
@Component
public class RedisPermissionCache {

    private static final Logger log = LoggerFactory.getLogger(RedisPermissionCache.class);
    private static final String KEY_PREFIX = "permission:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisPermissionCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Cache a permission decision.
     */
    public void put(String tenantId, String subjectId, String resource,
                   String action, boolean allowed, String reason) {
        String key = buildKey(tenantId, subjectId, resource, action);
        PermissionCacheEntry entry = new PermissionCacheEntry(allowed, reason);
        redisTemplate.opsForValue().set(key, entry, DEFAULT_TTL);
        log.debug("Cached permission decision: key={}, allowed={}", key, allowed);
    }

    /**
     * Get a cached permission decision.
     */
    public Optional<PermissionCacheEntry> get(String tenantId, String subjectId,
                                              String resource, String action) {
        String key = buildKey(tenantId, subjectId, resource, action);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof PermissionCacheEntry entry) {
            log.debug("Cache hit: key={}, allowed={}", key, entry.isAllowed());
            return Optional.of(entry);
        }
        log.debug("Cache miss: key={}", key);
        return Optional.empty();
    }

    /**
     * Invalidate all cached permissions for a subject.
     */
    public void invalidateSubject(String tenantId, String subjectId) {
        String pattern = KEY_PREFIX + tenantId + ":" + subjectId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Invalidated {} permission cache entries for subject: {}",
                    keys.size(), subjectId);
        }
    }

    /**
     * Invalidate all cached permissions for a tenant.
     */
    public void invalidateTenant(String tenantId) {
        String pattern = KEY_PREFIX + tenantId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Invalidated {} permission cache entries for tenant: {}",
                    keys.size(), tenantId);
        }
    }

    /**
     * Cache permissions for a subject.
     */
    public void cachePermissions(String tenantId, String subjectId, List<Permission> permissions) {
        // Group permissions by resource and action for efficient caching
        for (Permission permission : permissions) {
            String key = buildKey(tenantId, subjectId,
                    permission.getResource(), permission.getAction());
            PermissionCacheEntry entry = new PermissionCacheEntry(
                    permission.isAllowed(),
                    permission.getId() + ":" + permission.getEffect()
            );
            redisTemplate.opsForValue().set(key, entry, DEFAULT_TTL);
        }
        log.debug("Cached {} permissions for subject: {}", permissions.size(), subjectId);
    }

    private String buildKey(String tenantId, String subjectId, String resource, String action) {
        return KEY_PREFIX + tenantId + ":" + subjectId + ":" + resource + ":" + action;
    }

    /**
     * Cache entry for permission decisions.
     */
    public static class PermissionCacheEntry {
        private final boolean allowed;
        private final String reason;
        private final long timestamp;

        public PermissionCacheEntry(boolean allowed, String reason) {
            this.allowed = allowed;
            this.reason = reason;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isAllowed() { return allowed; }
        public String getReason() { return reason; }
        public long getTimestamp() { return timestamp; }

        public boolean isExpired(long maxAgeMillis) {
            return System.currentTimeMillis() - timestamp > maxAgeMillis;
        }
    }
}
