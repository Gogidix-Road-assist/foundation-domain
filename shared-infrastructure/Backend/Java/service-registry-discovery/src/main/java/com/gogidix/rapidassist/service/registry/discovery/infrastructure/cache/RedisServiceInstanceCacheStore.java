package com.gogidix.rapidassist.service.registry.discovery.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceInstanceCacheStore;
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
public class RedisServiceInstanceCacheStore implements ServiceInstanceCacheStore {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceInstanceCacheStore.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public RedisServiceInstanceCacheStore() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public CompletableFuture<Void> put(String tenantId, String serviceName,
                                       String instanceId, ServiceInstance instance) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildKey(tenantId, serviceName, instanceId);
                redisTemplate.opsForValue().set(key, instance, CACHE_TTL);
                logger.debug("Cached service instance: {}", key);
            } catch (Exception e) {
                logger.warn("Failed to cache service instance", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<ServiceInstance>> get(
        String tenantId, String serviceName, String instanceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String key = buildKey(tenantId, serviceName, instanceId);
                Object cached = redisTemplate.opsForValue().get(key);
                if (cached instanceof ServiceInstance) {
                    return Optional.of((ServiceInstance) cached);
                }
                return Optional.empty();
            } catch (Exception e) {
                logger.warn("Failed to get cached service instance", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getByService(
        String tenantId, String serviceName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String pattern = buildServicePattern(tenantId, serviceName);
                var keys = redisTemplate.keys(pattern);
                if (keys == null || keys.isEmpty()) {
                    return List.of();
                }
                return redisTemplate.opsForValue().multiGet(keys).stream()
                    .filter(obj -> obj instanceof ServiceInstance)
                    .map(obj -> (ServiceInstance) obj)
                    .toList();
            } catch (Exception e) {
                logger.warn("Failed to get cached service instances by service", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Void> evict(String tenantId, String serviceName,
                                        String instanceId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildKey(tenantId, serviceName, instanceId);
                redisTemplate.delete(key);
            } catch (Exception e) {
                logger.warn("Failed to evict cached service instance", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictByService(String tenantId, String serviceName) {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = buildServicePattern(tenantId, serviceName);
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                logger.warn("Failed to evict cached service instances by service", e);
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
                logger.warn("Failed to evict cached service instances by tenant", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictAll() {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = "service:instance:*";
                var keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                logger.warn("Failed to evict all cached service instances", e);
            }
        });
    }

    private String buildKey(String tenantId, String serviceName, String instanceId) {
        return String.format("service:instance:%s:%s:%s", tenantId, serviceName, instanceId);
    }

    private String buildServicePattern(String tenantId, String serviceName) {
        return String.format("service:instance:%s:%s:*", tenantId, serviceName);
    }

    private String buildTenantPattern(String tenantId) {
        return String.format("service:instance:%s:*", tenantId);
    }
}
