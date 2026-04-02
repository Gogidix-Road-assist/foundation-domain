package com.gogidix.rapidassist.config.service.domain.port.in;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ConfigurationQuery {

    CompletableFuture<Optional<Configuration>> getConfiguration(
        String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<List<Configuration>> getConfigurationsByNamespace(
        String tenantId, String environment, String namespace);

    CompletableFuture<List<Configuration>> getConfigurationsByTenant(
        String tenantId);

    CompletableFuture<List<Configuration>> getConfigurationsWithTags(
        String tenantId, List<String> tags);

    CompletableFuture<List<Configuration>> searchConfigurations(
        String tenantId, String keyword);

    CompletableFuture<List<Configuration>> getConfigurationsUpdatedSince(
        String tenantId, Instant since);

    CompletableFuture<List<Configuration>> getConfigurationHistory(
        String tenantId, String configKey, String environment);

    CompletableFuture<List<ConfigurationChange>> getConfigurationChanges(
        String tenantId, String configKey, String environment, String namespace);

    CompletableFuture<List<ConfigurationChange>> getChangesByTimeRange(
        String tenantId, Instant from, Instant to);

    CompletableFuture<List<ConfigurationChange>> getPendingApprovals(String tenantId);

    CompletableFuture<Optional<ConfigurationChange>> getChangeById(String changeId);

    CompletableFuture<Boolean> validateConfigurationValue(
        Object value, ConfigurationSchema schema);

    CompletableFuture<List<Configuration>> getActiveConfigurationsForEnvironment(
        String tenantId, String environment);

    CompletableFuture<List<Configuration>> getConfigurationsByStatus(
        String tenantId, Configuration.ConfigurationStatus status);
}