package com.gogidix.rapidassist.config.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationCacheStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component("noOpConfigurationCacheStore")
@ConditionalOnProperty(name = "gogidix.config.cache.provider", havingValue = "noop")
public class NoOpConfigurationCacheStore implements ConfigurationCacheStore {

    @Override
    public CompletableFuture<Optional<Configuration>> get(String tenantId, String configKey,
                                                         String environment, String namespace) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Void> put(String tenantId, String configKey, String environment,
                                       String namespace, Configuration configuration) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> evict(String tenantId, String configKey,
                                        String environment, String namespace) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> evictByNamespace(String tenantId, String environment, String namespace) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> evictByTenant(String tenantId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<Configuration>> getByNamespace(String tenantId, String environment, String namespace) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Set<String>> getTags(String tenantId) {
        return CompletableFuture.completedFuture(Set.of());
    }

    @Override
    public CompletableFuture<Void> putTags(String tenantId, Set<String> tags) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> exists(String tenantId, String configKey, String environment, String namespace) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Long> incrementVersion(String tenantId, String configKey, String environment, String namespace) {
        return CompletableFuture.completedFuture(0L);
    }

    @Override
    public CompletableFuture<Void> invalidatePattern(String pattern) {
        return CompletableFuture.completedFuture(null);
    }
}