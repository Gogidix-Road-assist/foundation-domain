package com.gogidix.rapidassist.config.service.domain.port.in;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ConfigurationCommand {

    CompletableFuture<Configuration> createConfiguration(CreateConfigurationCommand command);

    CompletableFuture<Optional<Configuration>> updateConfiguration(UpdateConfigurationCommand command);

    CompletableFuture<Boolean> deleteConfiguration(String tenantId, String configKey,
                                                   String environment, String namespace,
                                                   String deletedBy);

    CompletableFuture<Optional<Configuration>> rollbackConfiguration(String tenantId, String configKey,
                                                                     String environment, String namespace,
                                                                     Integer targetVersion, String rolledBackBy);

    CompletableFuture<ConfigurationChange> approveConfigurationChange(String changeId, String approvedBy);

    CompletableFuture<ConfigurationChange> rejectConfigurationChange(String changeId, String rejectedBy, String reason);

    CompletableFuture<List<Configuration>> bulkUpdateConfigurations(BulkUpdateCommand command);

    CompletableFuture<Boolean> validateConfigurations(String tenantId, String environment);

    record CreateConfigurationCommand(
        String tenantId,
        String configKey,
        String environment,
        String namespace,
        Object value,
        Configuration.ConfigurationDataType dataType,
        boolean encrypted,
        boolean required,
        Object defaultValue,
        String description,
        java.util.Set<String> tags,
        java.util.Map<String, Object> metadata,
        ConfigurationSchema schema,
        String createdBy,
        String reason
    ) {}

    record UpdateConfigurationCommand(
        String tenantId,
        String configKey,
        String environment,
        String namespace,
        Object newValue,
        String updatedBy,
        String reason,
        boolean forceUpdate
    ) {}

    record BulkUpdateCommand(
        String tenantId,
        List<ConfigurationUpdate> updates,
        String updatedBy,
        String reason
    ) {}

    record ConfigurationUpdate(
        String configKey,
        String environment,
        String namespace,
        Object value
    ) {}
}