package com.gogidix.rapidassist.config.service.infrastructure.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationCacheStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component("redisConfigurationCacheStore")
@ConditionalOnProperty(name = "gogidix.config.cache.provider", havingValue = "redis", matchIfMissing = true)
public class RedisConfigurationCacheStore implements ConfigurationCacheStore {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfigurationCacheStore.class);
    private static final String CONFIG_KEY_PREFIX = "config:";
    private static final String NAMESPACE_KEY_PREFIX = "config:ns:";
    private static final String TAGS_KEY_PREFIX = "config:tags:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${gogidix.config.cache.ttl:PT30M}")
    private Duration cacheTtl;

    private String buildConfigKey(String tenantId, String configKey, String environment, String namespace) {
        return String.format("%s%s:%s:%s:%s", CONFIG_KEY_PREFIX, tenantId, configKey, environment, namespace);
    }

    private String buildNamespaceKey(String tenantId, String environment, String namespace) {
        return String.format("%s%s:%s:%s", NAMESPACE_KEY_PREFIX, tenantId, environment, namespace);
    }

    private String buildTagsKey(String tenantId) {
        return String.format("%s%s", TAGS_KEY_PREFIX, tenantId);
    }

    @Override
    public CompletableFuture<Optional<Configuration>> get(String tenantId, String configKey,
                                                         String environment, String namespace) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String key = buildConfigKey(tenantId, configKey, environment, namespace);
                Object cached = redisTemplate.opsForValue().get(key);

                if (cached instanceof String json) {
                    Configuration config = objectMapper.readValue(json, Configuration.class);
                    logger.debug("Cache hit for configuration: {}", key);
                    return Optional.of(config);
                }

                logger.debug("Cache miss for configuration: {}", key);
                return Optional.empty();
            } catch (Exception e) {
                logger.error("Error getting configuration from cache", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Void> put(String tenantId, String configKey, String environment,
                                       String namespace, Configuration configuration) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildConfigKey(tenantId, configKey, environment, namespace);
                String json = objectMapper.writeValueAsString(configuration);

                redisTemplate.opsForValue().set(key, json, cacheTtl);

                String namespaceKey = buildNamespaceKey(tenantId, environment, namespace);
                redisTemplate.opsForSet().add(namespaceKey, key);
                redisTemplate.expire(namespaceKey, cacheTtl);

                if (configuration.tags() != null && !configuration.tags().isEmpty()) {
                    String tagsKey = buildTagsKey(tenantId);
                    redisTemplate.opsForSet().add(tagsKey, (Object[]) configuration.tags().toArray(new String[0]));
                    redisTemplate.expire(tagsKey, cacheTtl);
                }

                logger.debug("Cached configuration: {}", key);
            } catch (Exception e) {
                logger.error("Error caching configuration", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evict(String tenantId, String configKey,
                                        String environment, String namespace) {
        return CompletableFuture.runAsync(() -> {
            try {
                String key = buildConfigKey(tenantId, configKey, environment, namespace);
                redisTemplate.delete(key);
                logger.debug("Evicted configuration from cache: {}", key);
            } catch (Exception e) {
                logger.error("Error evicting configuration from cache", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictByNamespace(String tenantId, String environment, String namespace) {
        return CompletableFuture.runAsync(() -> {
            try {
                String namespaceKey = buildNamespaceKey(tenantId, environment, namespace);
                Set<Object> keys = redisTemplate.opsForSet().members(namespaceKey);

                if (keys != null) {
                    Set<String> stringKeys = keys.stream()
                        .filter(obj -> obj instanceof String)
                        .map(obj -> (String) obj)
                        .collect(java.util.stream.Collectors.toSet());
                    redisTemplate.delete(stringKeys);
                    redisTemplate.delete(namespaceKey);
                    logger.debug("Evicted namespace configurations from cache: {}", namespaceKey);
                }
            } catch (Exception e) {
                logger.error("Error evicting namespace from cache", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictByTenant(String tenantId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String pattern = CONFIG_KEY_PREFIX + tenantId + ":*";
                ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();

                Set<String> keys = redisTemplate.scan(options).stream()
                    .collect(Collectors.toSet());

                if (!keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    logger.debug("Evicted tenant configurations from cache: {} keys", keys.size());
                }

                String tagsKey = buildTagsKey(tenantId);
                redisTemplate.delete(tagsKey);
            } catch (Exception e) {
                logger.error("Error evicting tenant from cache", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> getByNamespace(String tenantId, String environment, String namespace) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String namespaceKey = buildNamespaceKey(tenantId, environment, namespace);
                Set<Object> keys = redisTemplate.opsForSet().members(namespaceKey);

                if (keys == null) {
                    return List.of();
                }

                Collection<String> stringKeys = keys.stream()
                    .filter(obj -> obj instanceof String)
                    .map(obj -> (String) obj)
                    .collect(java.util.stream.Collectors.toList());

                List<Object> values = redisTemplate.opsForValue().multiGet(stringKeys);
                return values.stream()
                    .filter(obj -> obj instanceof String)
                    .map(obj -> {
                        try {
                            return objectMapper.readValue((String) obj, Configuration.class);
                        } catch (Exception e) {
                            logger.warn("Error deserializing cached configuration", e);
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                logger.error("Error getting namespace configurations from cache", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Set<String>> getTags(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String tagsKey = buildTagsKey(tenantId);
                Set<Object> tags = redisTemplate.opsForSet().members(tagsKey);

                if (tags == null) {
                    return Set.of();
                }

                return tags.stream()
                    .filter(obj -> obj instanceof String)
                    .map(obj -> (String) obj)
                    .collect(Collectors.toSet());
            } catch (Exception e) {
                logger.error("Error getting tags from cache", e);
                return Set.of();
            }
        });
    }

    @Override
    public CompletableFuture<Void> putTags(String tenantId, Set<String> tags) {
        return CompletableFuture.runAsync(() -> {
            try {
                String tagsKey = buildTagsKey(tenantId);
                if (tags != null && !tags.isEmpty()) {
                    redisTemplate.opsForSet().add(tagsKey, (Object[]) tags.toArray(new String[0]));
                    redisTemplate.expire(tagsKey, cacheTtl);
                }
            } catch (Exception e) {
                logger.error("Error caching tags", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> exists(String tenantId, String configKey, String environment, String namespace) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String key = buildConfigKey(tenantId, configKey, environment, namespace);
                return Boolean.TRUE.equals(redisTemplate.hasKey(key));
            } catch (Exception e) {
                logger.error("Error checking existence in cache", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Long> incrementVersion(String tenantId, String configKey, String environment, String namespace) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String versionKey = buildConfigKey(tenantId, configKey, environment, namespace) + ":version";
                return redisTemplate.opsForValue().increment(versionKey);
            } catch (Exception e) {
                logger.error("Error incrementing version in cache", e);
                return 0L;
            }
        });
    }

    @Override
    public CompletableFuture<Void> invalidatePattern(String pattern) {
        return CompletableFuture.runAsync(() -> {
            try {
                ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
                Set<String> keys = redisTemplate.scan(options).stream().collect(Collectors.toSet());

                if (!keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    logger.debug("Invalidated cache pattern: {} ({} keys)", pattern, keys.size());
                }
            } catch (Exception e) {
                logger.error("Error invalidating cache pattern", e);
            }
        });
    }
}