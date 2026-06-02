package com.gogidix.rapidassist.user.profile.service.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileCacheStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnBean(name = "redisTemplate")
public class RedisUserProfileCacheStore implements UserProfileCacheStore {

    private static final Logger logger = LoggerFactory.getLogger(RedisUserProfileCacheStore.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public RedisUserProfileCacheStore() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public CompletableFuture<Void> put(String tenantId, String userId, UserProfile profile) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildKey(tenantId, userId);
                redisTemplate.opsForValue().set(key, profile, CACHE_TTL);
                logger.debug("Cached user profile: {}", key);
            } catch (Exception e) {
                logger.warn("Failed to cache user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> get(String tenantId, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String key = buildKey(tenantId, userId);
                Object cached = redisTemplate.opsForValue().get(key);
                if (cached instanceof UserProfile) {
                    return Optional.of((UserProfile) cached);
                }
                return Optional.empty();
            } catch (Exception e) {
                logger.warn("Failed to get cached user profile", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> getByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String pattern = buildTenantPattern(tenantId);
                var keys = redisTemplate.keys(pattern);
                if (keys == null || keys.isEmpty()) {
                    return List.of();
                }
                return redisTemplate.opsForValue().multiGet(keys).stream()
                    .filter(obj -> obj instanceof UserProfile)
                    .map(obj -> (UserProfile) obj)
                    .toList();
            } catch (Exception e) {
                logger.warn("Failed to get cached user profiles by tenant", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Void> evict(String tenantId, String userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildKey(tenantId, userId);
                redisTemplate.delete(key);
                logger.debug("Evicted cached user profile: {}", key);
            } catch (Exception e) {
                logger.warn("Failed to evict cached user profile", e);
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
                    logger.debug("Evicted {} cached user profiles for tenant: {}", keys.size(), tenantId);
                }
            } catch (Exception e) {
                logger.warn("Failed to evict cached user profiles by tenant", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictByRole(String tenantId, String role) {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = buildTenantPattern(tenantId);
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    List<Object> profiles = redisTemplate.opsForValue().multiGet(keys);
                    if (profiles != null) {
                        List<String> keysToDelete = profiles.stream()
                            .filter(obj -> obj instanceof UserProfile)
                            .map(obj -> (UserProfile) obj)
                            .filter(profile -> profile.roles().contains(role))
                            .map(profile -> buildKey(tenantId, profile.userId()))
                            .toList();

                        if (!keysToDelete.isEmpty()) {
                            redisTemplate.delete(keysToDelete);
                            logger.debug("Evicted {} cached user profiles for role: {}", keysToDelete.size(), role);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to evict cached user profiles by role", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictAll() {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = "user:profile:*";
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    logger.debug("Evicted all cached user profiles: {}", keys.size());
                }
            } catch (Exception e) {
                logger.warn("Failed to evict all cached user profiles", e);
            }
        });
    }

    private String buildKey(String tenantId, String userId) {
        return String.format("user:profile:%s:%s", tenantId, userId);
    }

    private String buildTenantPattern(String tenantId) {
        return String.format("user:profile:%s:*", tenantId);
    }
}
