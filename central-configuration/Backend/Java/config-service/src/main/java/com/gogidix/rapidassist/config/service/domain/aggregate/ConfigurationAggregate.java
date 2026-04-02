package com.gogidix.rapidassist.config.service.domain.aggregate;

import com.gogidix.rapidassist.config.service.domain.event.ConfigurationCreatedEvent;
import com.gogidix.rapidassist.config.service.domain.event.ConfigurationDeletedEvent;
import com.gogidix.rapidassist.config.service.domain.event.ConfigurationUpdatedEvent;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Configuration Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around configuration management:
 * <ul>
 *   <li>Configurations must be validated before creation</li>
 *   <li>Configuration changes must be tracked with audit trail</li>
 *   <li>Configurations in ACTIVE status cannot be deleted without approval</li>
 *   <li>Required configurations must have values</li>
 * </ul>
 */
public class ConfigurationAggregate {

    private final Configuration configuration;
    private final List<ConfigurationUpdatedEvent> pendingEvents;
    private boolean isNew;

    private ConfigurationAggregate(Configuration configuration, boolean isNew) {
        this.configuration = configuration;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new ConfigurationAggregate for a new configuration.
     *
     * @param tenantId      the tenant ID
     * @param configKey     the configuration key
     * @param environment   the environment
     * @param namespace     the namespace
     * @param value         the configuration value
     * @param dataType      the data type
     * @param schema        optional schema for validation
     * @param createdBy     the user creating the configuration
     * @return a new ConfigurationAggregate
     */
    public static ConfigurationAggregate create(
            String tenantId,
            String configKey,
            String environment,
            String namespace,
            Object value,
            Configuration.ConfigurationDataType dataType,
            ConfigurationSchema schema,
            String createdBy) {

        // Validate business rules
        validateConfigurationKey(configKey);
        validateValue(value, dataType);
        if (schema != null) {
            validateAgainstSchema(value, schema);
        }

        Configuration config = Configuration.create(
            tenantId, configKey, environment, namespace, value, dataType, createdBy
        );

        return new ConfigurationAggregate(config, true);
    }

    /**
     * Reconstructs an existing ConfigurationAggregate from persistence.
     *
     * @param configuration the configuration from persistence
     * @return a ConfigurationAggregate
     */
    public static ConfigurationAggregate fromExisting(Configuration configuration) {
        return new ConfigurationAggregate(configuration, false);
    }

    /**
     * Updates the configuration value.
     *
     * @param newValue   the new value
     * @param updatedBy  the user making the change
     * @param reason     the reason for the change
     * @return the ConfigurationUpdatedEvent if successful
     */
    public ConfigurationUpdatedEvent updateValue(Object newValue, String updatedBy, String reason) {
        validateValue(newValue, configuration.dataType());

        if (configuration.schema() != null) {
            validateAgainstSchema(newValue, configuration.schema());
        }

        Configuration oldValue = configuration;
        Configuration newConfig = configuration.withValue(newValue);

        ConfigurationUpdatedEvent event = new ConfigurationUpdatedEvent(
            oldValue, newConfig, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Changes the configuration status.
     *
     * @param newStatus the new status
     * @param changedBy the user making the change
     * @return the updated configuration
     */
    public Configuration changeStatus(Configuration.ConfigurationStatus newStatus, String changedBy) {
        // Business rule: Deprecated configurations cannot be reactivated
        if (configuration.status() == Configuration.ConfigurationStatus.DEPRECATED
                && newStatus == Configuration.ConfigurationStatus.ACTIVE) {
            throw new IllegalStateException(
                "Cannot reactivate a deprecated configuration. Create a new one instead."
            );
        }

        // Business rule: Validation failed configurations must be fixed before activation
        if (configuration.status() == Configuration.ConfigurationStatus.VALIDATION_FAILED
                && newStatus == Configuration.ConfigurationStatus.ACTIVE
                && configuration.validationErrors() != null
                && !configuration.validationErrors().isEmpty()) {
            throw new IllegalStateException(
                "Cannot activate a configuration with validation errors. Fix the errors first."
            );
        }

        return configuration.withStatus(newStatus, changedBy);
    }

    /**
     * Marks this configuration for deletion.
     *
     * @param deletedBy the user deleting the configuration
     * @param reason    the reason for deletion
     * @return the ConfigurationDeletedEvent
     */
    public ConfigurationDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Required configurations cannot be deleted
        if (configuration.required()) {
            throw new IllegalStateException(
                "Required configurations cannot be deleted. Change value or status instead."
            );
        }

        // Business rule: Active configurations require approval for deletion
        if (configuration.status() == Configuration.ConfigurationStatus.ACTIVE) {
            throw new IllegalStateException(
                "Active configurations must be deactivated before deletion."
            );
        }

        return new ConfigurationDeletedEvent(
            configuration.id(),
            configuration.tenantId(),
            configuration.configKey(),
            configuration.environment(),
            configuration.namespace(),
            configuration.version(),
            configuration.value(),
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current configuration value against its schema.
     *
     * @return set of validation errors, empty if valid
     */
    public Set<String> validate() {
        Set<String> errors = new java.util.HashSet<>();

        // Check if required value is present
        if (configuration.required() && configuration.value() == null) {
            errors.add("Required configuration must have a value");
        }

        // Validate against schema if present
        if (configuration.schema() != null && configuration.value() != null) {
            try {
                validateAgainstSchema(configuration.value(), configuration.schema());
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }

        // Check data type matches value
        if (configuration.value() != null) {
            try {
                validateValue(configuration.value(), configuration.dataType());
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }

        return errors;
    }

    /**
     * Gets the underlying configuration entity.
     *
     * @return the configuration
     */
    public Configuration configuration() {
        return configuration;
    }

    /**
     * Gets the configuration ID.
     *
     * @return the ID
     */
    public String id() {
        return configuration.id();
    }

    /**
     * Checks if this is a newly created aggregate.
     *
     * @return true if new, false if loaded from persistence
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Gets all pending events that haven't been published yet.
     *
     * @return list of pending events
     */
    public List<ConfigurationUpdatedEvent> getPendingEvents() {
        return new ArrayList<>(pendingEvents);
    }

    /**
     * Clears pending events after they've been published.
     */
    public void clearPendingEvents() {
        pendingEvents.clear();
    }

    // ========================================================================
    // Private validation methods
    // ========================================================================

    private static void validateConfigurationKey(String configKey) {
        if (configKey == null || configKey.isBlank()) {
            throw new IllegalArgumentException("Configuration key cannot be blank");
        }

        // Config key must follow pattern: dot-separated namespaced keys
        if (!configKey.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                "Configuration key must contain only alphanumeric characters, dots, hyphens, and underscores"
            );
        }

        if (configKey.length() > 255) {
            throw new IllegalArgumentException("Configuration key cannot exceed 255 characters");
        }
    }

    private static void validateValue(Object value, Configuration.ConfigurationDataType dataType) {
        if (value == null) {
            return; // Null values are allowed for non-required configs
        }

        switch (dataType) {
            case STRING -> {
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException("Value must be a String for STRING data type");
                }
            }
            case NUMBER -> {
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException("Value must be a Number for NUMBER data type");
                }
            }
            case BOOLEAN -> {
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException("Value must be a Boolean for BOOLEAN data type");
                }
            }
            case JSON, YAML, OBJECT -> {
                if (!(value instanceof java.util.Map) && !(value instanceof String)) {
                    throw new IllegalArgumentException(
                        "Value must be a Map or String for " + dataType + " data type"
                    );
                }
            }
            case ARRAY -> {
                if (!(value instanceof java.util.Collection) && !value.getClass().isArray()) {
                    throw new IllegalArgumentException("Value must be a Collection or Array for ARRAY data type");
                }
            }
            case BINARY -> {
                if (!(value instanceof byte[]) && !(value instanceof String)) {
                    throw new IllegalArgumentException("Value must be a byte[] or base64 String for BINARY data type");
                }
            }
        }
    }

    private static void validateAgainstSchema(Object value, ConfigurationSchema schema) {
        if (schema == null || value == null) {
            return;
        }

        // Check required constraint
        if (schema.required() && value == null) {
            throw new IllegalArgumentException("Value is required according to schema");
        }

        // Check allowed values
        if (schema.allowedValues() != null && !schema.allowedValues().isEmpty()) {
            if (!schema.allowedValues().contains(value.toString())) {
                throw new IllegalArgumentException(
                    "Value '" + value + "' is not in allowed values: " + schema.allowedValues()
                );
            }
        }

        // Additional type-specific validation based on schema type
        // This would be expanded based on specific schema implementations
    }
}
