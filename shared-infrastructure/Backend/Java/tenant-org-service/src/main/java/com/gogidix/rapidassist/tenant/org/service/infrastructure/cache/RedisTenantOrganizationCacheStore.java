package com.gogidix.rapidassist.tenant.org.service.infrastructure.cache;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationCacheStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class RedisTenantOrganizationCacheStore implements TenantOrganizationCacheStore {

    private static final Logger logger = LoggerFactory.getLogger(RedisTenantOrganizationCacheStore.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public CompletableFuture<Void> put(String tenantId, String orgId, TenantOrganization organization) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildKey(tenantId, orgId);
                redisTemplate.opsForValue().set(key, organization, CACHE_TTL);
                logger.debug("Cached organization: {}", key);
            } catch (Exception e) {
                logger.warn("Failed to cache organization", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> get(String tenantId, String orgId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String key = buildKey(tenantId, orgId);
                Object cached = redisTemplate.opsForValue().get(key);
                if (cached instanceof TenantOrganization) {
                    return Optional.of((TenantOrganization) cached);
                }
                return Optional.empty();
            } catch (Exception e) {
                logger.warn("Failed to get cached organization", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> getByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String pattern = buildTenantPattern(tenantId);
                var keys = redisTemplate.keys(pattern);
                if (keys == null || keys.isEmpty()) {
                    return List.of();
                }
                return redisTemplate.opsForValue().multiGet(keys).stream()
                        .filter(obj -> obj instanceof TenantOrganization)
                        .map(obj -> (TenantOrganization) obj)
                        .toList();
            } catch (Exception e) {
                logger.warn("Failed to get cached organizations by tenant", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Void> evict(String tenantId, String orgId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildKey(tenantId, orgId);
                redisTemplate.delete(key);
            } catch (Exception e) {
                logger.warn("Failed to evict cached organization", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictByTenant(String tenantId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = buildTenantPattern(tenantId);
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                logger.warn("Failed to evict cached organizations by tenant", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictAll() {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = "tenant:org:*";
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                logger.warn("Failed to evict all cached organizations", e);
            }
        });
    }

    private String buildKey(String tenantId, String orgId) {
        return String.format("tenant:org:%s:%s", tenantId, orgId);
    }

    private String buildTenantPattern(String tenantId) {
        return String.format("tenant:org:%s:*", tenantId);
    }
}
