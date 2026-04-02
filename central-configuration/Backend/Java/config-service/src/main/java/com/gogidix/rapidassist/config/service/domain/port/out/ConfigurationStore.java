package com.gogidix.rapidassist.config.service.domain.port.out;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ConfigurationStore {

    CompletableFuture<Configuration> save(Configuration configuration);

    CompletableFuture<Optional<Configuration>> findById(String id);

    CompletableFuture<Optional<Configuration>> findByKeyAndEnvironment(
        String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<List<Configuration>> findByTenant(String tenantId);

    CompletableFuture<List<Configuration>> findByNamespace(
        String tenantId, String environment, String namespace);

    CompletableFuture<List<Configuration>> findAllVersions(
        String tenantId, String configKey, String environment);

    CompletableFuture<Optional<Configuration>> findLatestVersion(
        String tenantId, String configKey, String environment);

    CompletableFuture<List<Configuration>> findByTags(String tenantId, List<String> tags);

    CompletableFuture<List<Configuration>> searchByKeyword(String tenantId, String keyword);

    CompletableFuture<List<Configuration>> findUpdatedSince(String tenantId, Instant since);

    CompletableFuture<Boolean> deleteById(String id);

    CompletableFuture<Boolean> deleteByKeyAndEnvironment(
        String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<ConfigurationChange> saveChange(ConfigurationChange change);

    CompletableFuture<Optional<ConfigurationChange>> findChangeById(String id);

    CompletableFuture<List<ConfigurationChange>> findChangesByConfiguration(
        String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<List<ConfigurationChange>> findChangesByTenant(String tenantId);

    CompletableFuture<List<ConfigurationChange>> findChangesByTimeRange(
        String tenantId, Instant from, Instant to);

    CompletableFuture<List<ConfigurationChange>> findPendingApprovals(String tenantId);

    CompletableFuture<List<Configuration>> findByStatus(
        String tenantId, Configuration.ConfigurationStatus status);
}