package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component("mongoConfigurationStore")
public class MongoConfigurationStore implements ConfigurationStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigurationStore.class);

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ConfigurationChangeRepository changeRepository;

    @Override
    public CompletableFuture<Configuration> save(Configuration configuration) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ConfigurationDocument doc = ConfigurationDocument.fromDomain(configuration);
                ConfigurationDocument saved = configurationRepository.save(doc);
                logger.debug("Saved configuration: {}", saved.id());
                return saved.toDomain();
            } catch (Exception e) {
                logger.error("Error saving configuration", e);
                throw new RuntimeException("Failed to save configuration", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Configuration>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository.findById(id)
                    .map(ConfigurationDocument::toDomain);
            } catch (Exception e) {
                logger.error("Error finding configuration by id: {}", id, e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Configuration>> findByKeyAndEnvironment(
        String tenantId, String configKey, String environment, String namespace) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                        tenantId, configKey, environment, namespace)
                    .map(ConfigurationDocument::toDomain);
            } catch (Exception e) {
                logger.error("Error finding configuration by key and environment", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository.findByTenantId(tenantId).stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding configurations for tenant: {}", tenantId, e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> findByNamespace(
        String tenantId, String environment, String namespace) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findByTenantIdAndEnvironmentAndNamespace(tenantId, environment, namespace)
                    .stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding configurations for namespace", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> findAllVersions(
        String tenantId, String configKey, String environment) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findAllVersions(tenantId, configKey, environment)
                    .stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding all versions of configuration", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Configuration>> findLatestVersion(
        String tenantId, String configKey, String environment) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findLatestVersion(tenantId, configKey, environment)
                    .map(ConfigurationDocument::toDomain);
            } catch (Exception e) {
                logger.error("Error finding latest version of configuration", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> findByTags(String tenantId, List<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findByTenantIdAndTags(tenantId, tags)
                    .stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding configurations by tags", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> searchByKeyword(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .searchByKeyword(tenantId, keyword)
                    .stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error searching configurations by keyword", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> findUpdatedSince(String tenantId, Instant since) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findByTenantIdAndUpdatedAfter(tenantId, since)
                    .stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding configurations updated since", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                configurationRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                logger.error("Error deleting configuration by id: {}", id, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByKeyAndEnvironment(
        String tenantId, String configKey, String environment, String namespace) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                configurationRepository.deleteByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                    tenantId, configKey, environment, namespace);
                return true;
            } catch (Exception e) {
                logger.error("Error deleting configuration", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<ConfigurationChange> saveChange(ConfigurationChange change) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ConfigurationChangeDocument doc = ConfigurationChangeDocument.fromDomain(change);
                ConfigurationChangeDocument saved = changeRepository.save(doc);
                logger.debug("Saved configuration change: {}", saved.changeId());
                return saved.toDomain();
            } catch (Exception e) {
                logger.error("Error saving configuration change", e);
                throw new RuntimeException("Failed to save configuration change", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<ConfigurationChange>> findChangeById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return changeRepository.findByChangeId(id)
                    .map(ConfigurationChangeDocument::toDomain);
            } catch (Exception e) {
                logger.error("Error finding configuration change by id: {}", id, e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> findChangesByConfiguration(
        String tenantId, String configKey, String environment, String namespace) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return changeRepository
                    .findByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                        tenantId, configKey, environment, namespace)
                    .stream()
                    .map(ConfigurationChangeDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding changes for configuration", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> findChangesByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return changeRepository.findByTenantId(tenantId).stream()
                    .map(ConfigurationChangeDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding changes for tenant: {}", tenantId, e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> findChangesByTimeRange(
        String tenantId, Instant from, Instant to) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return changeRepository
                    .findByTenantIdAndTimeRange(tenantId, from, to)
                    .stream()
                    .map(ConfigurationChangeDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding changes in time range", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> findPendingApprovals(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return changeRepository
                    .findByTenantIdAndApprovalStatus(
                        tenantId, ConfigurationChange.ApprovalStatus.PENDING)
                    .stream()
                    .map(ConfigurationChangeDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding pending approvals", e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> findByStatus(
        String tenantId, Configuration.ConfigurationStatus status) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return configurationRepository
                    .findByTenantIdAndStatus(tenantId, status)
                    .stream()
                    .map(ConfigurationDocument::toDomain)
                    .toList();
            } catch (Exception e) {
                logger.error("Error finding configurations by status", e);
                return List.of();
            }
        });
    }
}