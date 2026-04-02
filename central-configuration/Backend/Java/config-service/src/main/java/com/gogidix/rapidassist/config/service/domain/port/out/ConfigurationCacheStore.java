package com.gogidix.rapidassist.config.service.domain.port.out;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ConfigurationCacheStore {

    CompletableFuture<Optional<Configuration>> get(String tenantId, String configKey,
                                                  String environment, String namespace);

    CompletableFuture<Void> put(String tenantId, String configKey, String environment,
                                String namespace, Configuration configuration);

    CompletableFuture<Void> evict(String tenantId, String configKey,
                                  String environment, String namespace);

    CompletableFuture<Void> evictByNamespace(String tenantId, String environment, String namespace);

    CompletableFuture<Void> evictByTenant(String tenantId);

    CompletableFuture<List<Configuration>> getByNamespace(String tenantId, String environment, String namespace);

    CompletableFuture<Set<String>> getTags(String tenantId);

    CompletableFuture<Void> putTags(String tenantId, Set<String> tags);

    CompletableFuture<Boolean> exists(String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<Long> incrementVersion(String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<Void> invalidatePattern(String pattern);
}