package com.gogidix.rapidassist.config.service.application.service;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import com.gogidix.rapidassist.config.service.domain.port.in.ConfigurationCommand;
import com.gogidix.rapidassist.config.service.domain.port.in.ConfigurationQuery;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationCacheStore;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@Transactional
public class ComprehensiveConfigurationService implements ConfigurationCommand, ConfigurationQuery {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveConfigurationService.class);

    @Autowired
    @Qualifier("mongoConfigurationStore")
    private ConfigurationStore configurationStore;

    @Autowired
    @Qualifier("redisConfigurationCacheStore")
    private ConfigurationCacheStore cacheStore;

    @PostConstruct
    public void init() {
        logger.info("Comprehensive Configuration Service initialized with MongoDB store and Redis cache");
    }

    @Override
    public CompletableFuture<Configuration> createConfiguration(CreateConfigurationCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!validateRequired(command.configKey(), command.value(), command.dataType())) {
                    throw new IllegalArgumentException("Invalid configuration: missing required fields");
                }

                Optional<Configuration> existing = findByKeyAndEnvironment(
                    command.tenantId(), command.configKey(),
                    command.environment(), command.namespace()).join();

                if (existing.isPresent() && existing.get().status() != Configuration.ConfigurationStatus.DEPRECATED) {
                    throw new IllegalStateException("Configuration already exists");
                }

                if (command.schema() != null) {
                    Set<String> validationErrors = validateValue(command.value(), command.schema()).join();
                    if (!validationErrors.isEmpty()) {
                        throw new IllegalArgumentException("Configuration value validation failed: " + validationErrors);
                    }
                }

                Configuration config = Configuration.create(
                    command.tenantId(),
                    command.configKey(),
                    command.environment(),
                    command.namespace(),
                    command.value(),
                    command.dataType(),
                    command.createdBy()
                );

                Configuration enhancedConfig = enhanceConfiguration(config, command);

                Configuration saved = configurationStore.save(enhancedConfig).join();

                ConfigurationChange change = ConfigurationChange.create(
                    command.tenantId(),
                    command.configKey(),
                    command.environment(),
                    command.namespace(),
                    ConfigurationChange.ChangeType.CREATE,
                    null,
                    1,
                    null,
                    command.value(),
                    command.createdBy(),
                    command.reason()
                );

                configurationStore.saveChange(change).join();
                cacheStore.put(
                    command.tenantId(),
                    command.configKey(),
                    command.environment(),
                    command.namespace(),
                    saved
                ).join();

                cacheStore.evictByNamespace(command.tenantId(), command.environment(), command.namespace()).join();

                logger.info("Created configuration: {} for tenant: {}", command.configKey(), command.tenantId());
                return saved;

            } catch (Exception e) {
                logger.error("Error creating configuration", e);
                throw new RuntimeException("Failed to create configuration", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Configuration>> updateConfiguration(UpdateConfigurationCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Configuration> existingOpt = findByKeyAndEnvironment(
                    command.tenantId(), command.configKey(),
                    command.environment(), command.namespace()).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                Configuration existing = existingOpt.get();

                if (!command.forceUpdate() && existing.status() == Configuration.ConfigurationStatus.DEPRECATED) {
                    throw new IllegalStateException("Cannot update deprecated configuration");
                }

                Object newValue = processEncryptedValue(command.newValue(), existing.encrypted());

                if (existing.schema() != null) {
                    Set<String> validationErrors = validateValue(newValue, existing.schema()).join();
                    if (!validationErrors.isEmpty()) {
                        throw new IllegalArgumentException("Configuration value validation failed: " + validationErrors);
                    }
                }

                Configuration updated = existing.withValue(newValue)
                    .withVersion(existing.version() + 1);

                Configuration saved = configurationStore.save(updated).join();

                ConfigurationChange change = ConfigurationChange.create(
                    command.tenantId(),
                    command.configKey(),
                    command.environment(),
                    command.namespace(),
                    ConfigurationChange.ChangeType.UPDATE,
                    existing.version(),
                    updated.version(),
                    existing.value(),
                    newValue,
                    command.updatedBy(),
                    command.reason()
                );

                configurationStore.saveChange(change).join();

                cacheStore.put(
                    command.tenantId(),
                    command.configKey(),
                    command.environment(),
                    command.namespace(),
                    saved
                ).join();

                cacheStore.evictByNamespace(command.tenantId(), command.environment(), command.namespace()).join();

                logger.info("Updated configuration: {} for tenant: {}", command.configKey(), command.tenantId());
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating configuration", e);
                throw new RuntimeException("Failed to update configuration", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteConfiguration(String tenantId, String configKey,
                                                         String environment, String namespace,
                                                         String deletedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Configuration> existingOpt = findByKeyAndEnvironment(tenantId, configKey, environment, namespace).join();

                if (existingOpt.isEmpty()) {
                    return false;
                }

                Configuration existing = existingOpt.get();

                if (existing.required()) {
                    throw new IllegalStateException("Cannot delete required configuration");
                }

                Configuration deleted = existing.withStatus(Configuration.ConfigurationStatus.INACTIVE, deletedBy);
                configurationStore.save(deleted).join();

                ConfigurationChange change = ConfigurationChange.create(
                    tenantId, configKey, environment, namespace,
                    ConfigurationChange.ChangeType.DELETE,
                    existing.version(), null,
                    existing.value(), null,
                    deletedBy, "Configuration deleted"
                );

                configurationStore.saveChange(change).join();

                cacheStore.evict(tenantId, configKey, environment, namespace).join();
                cacheStore.evictByNamespace(tenantId, environment, namespace).join();

                logger.info("Deleted configuration: {} for tenant: {}", configKey, tenantId);
                return true;

            } catch (Exception e) {
                logger.error("Error deleting configuration", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Configuration>> rollbackConfiguration(String tenantId, String configKey,
                                                                           String environment, String namespace,
                                                                           Integer targetVersion, String rolledBackBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Configuration> versions = configurationStore
                    .findAllVersions(tenantId, configKey, environment)
                    .join();

                Optional<Configuration> targetVersionOpt = versions.stream()
                    .filter(c -> c.version().equals(targetVersion))
                    .findFirst();

                if (targetVersionOpt.isEmpty()) {
                    throw new IllegalArgumentException("Target version not found: " + targetVersion);
                }

                Configuration targetConfig = targetVersionOpt.get();
                Configuration currentOpt = findByKeyAndEnvironment(tenantId, configKey, environment, namespace).join()
                    .orElseThrow(() -> new IllegalStateException("Current configuration not found"));

                Integer newVersion = versions.stream()
                    .mapToInt(Configuration::version)
                    .max()
                    .orElse(targetVersion) + 1;

                Configuration rolledBack = new Configuration(
                    currentOpt.id(),
                    tenantId, configKey, environment, namespace,
                    newVersion,
                    targetConfig.value(),
                    targetConfig.dataType(),
                    targetConfig.encrypted(),
                    targetConfig.required(),
                    targetConfig.defaultValue(),
                    targetConfig.description(),
                    targetConfig.tags(),
                    targetConfig.metadata(),
                    targetConfig.schema(),
                    Configuration.ConfigurationStatus.ACTIVE,
                    targetConfig.createdBy(),
                    targetConfig.createdAt(),
                    rolledBackBy,
                    Instant.now(),
                    null,
                    Set.of()
                );

                Configuration saved = configurationStore.save(rolledBack).join();

                ConfigurationChange change = ConfigurationChange.create(
                    tenantId, configKey, environment, namespace,
                    ConfigurationChange.ChangeType.ROLLBACK,
                    currentOpt.version(),
                    newVersion,
                    currentOpt.value(),
                    targetConfig.value(),
                    rolledBackBy,
                    "Rolled back to version " + targetVersion
                );

                configurationStore.saveChange(change).join();

                cacheStore.put(tenantId, configKey, environment, namespace, saved).join();
                cacheStore.evictByNamespace(tenantId, environment, namespace).join();

                logger.info("Rolled back configuration: {} to version: {} for tenant: {}",
                           configKey, targetVersion, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error rolling back configuration", e);
                throw new RuntimeException("Failed to rollback configuration", e);
            }
        });
    }

    @Override
    public CompletableFuture<ConfigurationChange> approveConfigurationChange(String changeId, String approvedBy) {
        return updateChangeApproval(changeId, ConfigurationChange.ApprovalStatus.APPROVED, approvedBy, null);
    }

    @Override
    public CompletableFuture<ConfigurationChange> rejectConfigurationChange(String changeId, String rejectedBy, String reason) {
        return updateChangeApproval(changeId, ConfigurationChange.ApprovalStatus.REJECTED, rejectedBy, reason);
    }

    @Override
    public CompletableFuture<List<Configuration>> bulkUpdateConfigurations(BulkUpdateCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Configuration> results = command.updates().stream()
                    .map(update -> updateConfiguration(
                        new UpdateConfigurationCommand(
                            command.tenantId(),
                            update.configKey(),
                            update.environment(),
                            update.namespace(),
                            update.value(),
                            command.updatedBy(),
                            command.reason(),
                            false
                        )
                    ).join())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

                cacheStore.evictByTenant(command.tenantId()).join();

                logger.info("Bulk updated {} configurations for tenant: {}", results.size(), command.tenantId());
                return results;

            } catch (Exception e) {
                logger.error("Error bulk updating configurations", e);
                throw new RuntimeException("Failed to bulk update configurations", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> validateConfigurations(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Configuration> configs = getActiveConfigurationsForEnvironment(tenantId, environment).join();
                boolean allValid = configs.stream()
                    .allMatch(config -> {
                        if (config.schema() != null) {
                            Set<String> errors = validateValue(config.value(), config.schema()).join();
                            if (!errors.isEmpty()) {
                                logger.warn("Configuration validation failed: {} - {}", config.configKey(), errors);
                                return false;
                            }
                        }
                        return true;
                    });

                logger.info("Configuration validation completed for tenant: {}, environment: {}, allValid: {}",
                          tenantId, environment, allValid);
                return allValid;

            } catch (Exception e) {
                logger.error("Error validating configurations", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Configuration>> getConfiguration(
        String tenantId, String configKey, String environment, String namespace) {
        return cacheStore.get(tenantId, configKey, environment, namespace)
            .thenCompose(cached -> cached.isPresent()
                ? CompletableFuture.completedFuture(cached)
                : configurationStore.findByKeyAndEnvironment(tenantId, configKey, environment, namespace)
                    .thenApply(configOpt -> {
                        configOpt.ifPresent(config -> {
                            cacheStore.put(tenantId, configKey, environment, namespace, config);
                        });
                        return configOpt;
                    }));
    }

    @Override
    public CompletableFuture<List<Configuration>> getConfigurationsByNamespace(
        String tenantId, String environment, String namespace) {
        return cacheStore.getByNamespace(tenantId, environment, namespace)
            .thenCompose(cachedConfigs -> {
                if (cachedConfigs.size() > 0) {
                    return CompletableFuture.completedFuture(cachedConfigs);
                }
                return configurationStore.findByNamespace(tenantId, environment, namespace)
                    .thenApply(configs -> {
                        configs.forEach(config -> {
                            cacheStore.put(tenantId, config.configKey(), environment, namespace, config);
                        });
                        return configs;
                    });
            });
    }

    @Override
    public CompletableFuture<List<Configuration>> getConfigurationsByTenant(String tenantId) {
        return configurationStore.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<Configuration>> getConfigurationsWithTags(String tenantId, List<String> tags) {
        return configurationStore.findByTags(tenantId, tags);
    }

    @Override
    public CompletableFuture<List<Configuration>> searchConfigurations(String tenantId, String keyword) {
        return configurationStore.searchByKeyword(tenantId, keyword);
    }

    @Override
    public CompletableFuture<List<Configuration>> getConfigurationsUpdatedSince(String tenantId, Instant since) {
        return configurationStore.findUpdatedSince(tenantId, since);
    }

    @Override
    public CompletableFuture<List<Configuration>> getConfigurationHistory(
        String tenantId, String configKey, String environment) {
        return configurationStore.findAllVersions(tenantId, configKey, environment);
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> getConfigurationChanges(
        String tenantId, String configKey, String environment, String namespace) {
        return configurationStore.findChangesByConfiguration(tenantId, configKey, environment, namespace);
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> getChangesByTimeRange(
        String tenantId, Instant from, Instant to) {
        return configurationStore.findChangesByTimeRange(tenantId, from, to);
    }

    @Override
    public CompletableFuture<List<ConfigurationChange>> getPendingApprovals(String tenantId) {
        return configurationStore.findPendingApprovals(tenantId);
    }

    @Override
    public CompletableFuture<Optional<ConfigurationChange>> getChangeById(String changeId) {
        return configurationStore.findChangeById(changeId);
    }

    @Override
    public CompletableFuture<Boolean> validateConfigurationValue(Object value, ConfigurationSchema schema) {
        return CompletableFuture.supplyAsync(() -> {
            Set<String> errors = validateValue(value, schema).join();
            return errors.isEmpty();
        });
    }

    @Override
    public CompletableFuture<List<Configuration>> getActiveConfigurationsForEnvironment(String tenantId, String environment) {
        return configurationStore.findByStatus(tenantId, Configuration.ConfigurationStatus.ACTIVE)
            .thenApply(configs -> configs.stream()
                .filter(config -> config.environment().equals(environment))
                .toList());
    }

    @Override
    public CompletableFuture<List<Configuration>> getConfigurationsByStatus(
        String tenantId, Configuration.ConfigurationStatus status) {
        return configurationStore.findByStatus(tenantId, status);
    }

    private CompletableFuture<Optional<Configuration>> findByKeyAndEnvironment(
        String tenantId, String configKey, String environment, String namespace) {
        return configurationStore.findByKeyAndEnvironment(tenantId, configKey, environment, namespace);
    }

    private boolean validateRequired(String configKey, Object value, Configuration.ConfigurationDataType dataType) {
        return configKey != null && !configKey.trim().isEmpty()
            && value != null
            && dataType != null;
    }

    private Configuration enhanceConfiguration(Configuration config, CreateConfigurationCommand command) {
        return new Configuration(
            config.id(),
            config.tenantId(),
            config.configKey(),
            config.environment(),
            config.namespace(),
            config.version(),
            config.value(),
            config.dataType(),
            command.encrypted(),
            command.required(),
            command.defaultValue(),
            command.description(),
            command.tags(),
            command.metadata(),
            command.schema(),
            Configuration.ConfigurationStatus.ACTIVE,
            config.createdBy(),
            config.createdAt(),
            config.updatedBy(),
            config.updatedAt(),
            Instant.now(),
            Set.of()
        );
    }

    private Object processEncryptedValue(Object value, boolean encrypted) {
        if (!encrypted) {
            return value;
        }

        return value;
    }

    private CompletableFuture<Set<String>> validateValue(Object value, ConfigurationSchema schema) {
        return CompletableFuture.supplyAsync(() -> {
            Set<String> errors = java.util.HashSet.newHashSet(10);

            try {
                switch (schema) {
                    case ConfigurationSchema.StringSchema stringSchema -> {
                        if (!(value instanceof String)) {
                            errors.add("Value must be a string");
                        } else {
                            String str = (String) value;
                            if (stringSchema.minLength() != null && str.length() < stringSchema.minLength()) {
                                errors.add("String too short, minimum length: " + stringSchema.minLength());
                            }
                            if (stringSchema.maxLength() != null && str.length() > stringSchema.maxLength()) {
                                errors.add("String too long, maximum length: " + stringSchema.maxLength());
                            }
                            if (stringSchema.pattern() != null && !str.matches(stringSchema.pattern())) {
                                errors.add("String does not match required pattern");
                            }
                        }
                    }
                    case ConfigurationSchema.NumberSchema numberSchema -> {
                        if (!(value instanceof Number)) {
                            errors.add("Value must be a number");
                        } else {
                            double num = ((Number) value).doubleValue();
                            if (numberSchema.minimum() != null && num < numberSchema.minimum()) {
                                errors.add("Number too small, minimum: " + numberSchema.minimum());
                            }
                            if (numberSchema.maximum() != null && num > numberSchema.maximum()) {
                                errors.add("Number too large, maximum: " + numberSchema.maximum());
                            }
                        }
                    }
                    case ConfigurationSchema.BooleanSchema booleanSchema -> {
                        if (!(value instanceof Boolean)) {
                            errors.add("Value must be a boolean");
                        }
                    }
                    case ConfigurationSchema.ArraySchema arraySchema -> {
                        if (!(value instanceof List)) {
                            errors.add("Value must be an array");
                        } else {
                            List<?> list = (List<?>) value;
                            if (arraySchema.minItems() != null && list.size() < arraySchema.minItems()) {
                                errors.add("Array too small, minimum items: " + arraySchema.minItems());
                            }
                            if (arraySchema.maxItems() != null && list.size() > arraySchema.maxItems()) {
                                errors.add("Array too large, maximum items: " + arraySchema.maxItems());
                            }
                        }
                    }
                    default -> {
                    }
                }
            } catch (Exception e) {
                errors.add("Validation error: " + e.getMessage());
            }

            return errors;
        });
    }

    private CompletableFuture<ConfigurationChange> updateChangeApproval(
        String changeId, ConfigurationChange.ApprovalStatus status, String approvedBy, String reason) {
        return configurationStore.findChangeById(changeId)
            .thenCompose(changeOpt -> {
                if (changeOpt.isEmpty()) {
                    throw new IllegalArgumentException("Change not found: " + changeId);
                }

                ConfigurationChange change = changeOpt.get();
                ConfigurationChange updated = change.withApproval(status, approvedBy);

                return configurationStore.saveChange(updated)
                    .thenApply(saved -> {
                        logger.info("Configuration change {} {}", changeId, status.name().toLowerCase());
                        return saved;
                    });
            });
    }
}