package com.gogidix.rapidassist.config.service.application.command;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;

import java.util.Set;

/**
 * Command object for creating a new Configuration.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new configuration entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateConfigurationCommand(
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
    Set<String> tags,
    ConfigurationSchema schema,
    String createdBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && configKey != null && !configKey.isBlank()
            && environment != null && !environment.isBlank()
            && namespace != null && !namespace.isBlank()
            && dataType != null
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateConfigurationCommand with defaults
     */
    public CreateConfigurationCommand withDefaults() {
        return new CreateConfigurationCommand(
            this.tenantId,
            this.configKey,
            this.environment,
            this.namespace,
            this.value,
            this.dataType,
            this.encrypted,
            this.required,
            this.defaultValue,
            this.description != null ? this.description : "",
            this.tags != null ? this.tags : Set.of(),
            this.schema,
            this.createdBy,
            this.reason != null ? this.reason : "Initial configuration creation"
        );
    }
}
