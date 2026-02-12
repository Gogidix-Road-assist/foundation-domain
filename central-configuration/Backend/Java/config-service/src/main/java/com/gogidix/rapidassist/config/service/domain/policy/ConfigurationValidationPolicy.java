package com.gogidix.rapidassist.config.service.domain.policy;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Domain policy for validating configurations.
 *
 * <p>This policy encapsulates business rules for configuration validation:
 * <ul>
 *   <li>Required configurations must have non-null values</li>
 *   <li>Encrypted configurations must have non-empty string values</li>
 *   <li>Configurations with schemas must validate against their schema</li>
 *   <li>Production environment configurations must have specific validations</li>
 *   <li>Sensitive keys must be encrypted</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any configuration.
 */
public class ConfigurationValidationPolicy {

    // Patterns for detecting sensitive configuration keys
    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
        ".*(?:password|secret|token|api[_-]?key|private[_-]?key|credential).*",
        Pattern.CASE_INSENSITIVE
    );

    // Protected keys that cannot be modified without special approval
    private static final Set<String> PROTECTED_KEYS = Set.of(
        "system.tenant.id",
        "system.service.name",
        "system.environment"
    );

    private final boolean enforceEncryptionForSensitive;
    private final boolean requireApprovalForProtectedKeys;

    private ConfigurationValidationPolicy(Builder builder) {
        this.enforceEncryptionForSensitive = builder.enforceEncryptionForSensitive;
        this.requireApprovalForProtectedKeys = builder.requireApprovalForProtectedKeys;
    }

    /**
     * Validates a configuration according to this policy.
     *
     * @param configuration the configuration to validate
     * @return validation result
     */
    public ValidationResult validate(Configuration configuration) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Required configurations must have values
        if (configuration.required() && configuration.value() == null) {
            errors.add("Required configuration must have a value");
        }

        // Rule 2: Encrypted configurations must have string values
        if (configuration.encrypted() && configuration.value() != null) {
            if (!(configuration.value() instanceof String)) {
                errors.add("Encrypted configurations must have string values");
            } else if (((String) configuration.value()).isBlank()) {
                errors.add("Encrypted configurations cannot have empty values");
            }
        }

        // Rule 3: Sensitive keys must be encrypted
        if (enforceEncryptionForSensitive && isSensitiveKey(configuration.configKey())) {
            if (!configuration.encrypted()) {
                errors.add(
                    "Configuration key '" + configuration.configKey() +
                    "' appears to be sensitive and must be encrypted"
                );
            }
        }

        // Rule 4: Validate against schema if present
        if (configuration.schema() != null) {
            try {
                validateAgainstSchema(configuration.value(), configuration.schema());
            } catch (ValidationException e) {
                errors.add(e.getMessage());
            }
        }

        // Rule 5: Production environments require additional validation
        if ("production".equalsIgnoreCase(configuration.environment()) ||
            "prod".equalsIgnoreCase(configuration.environment())) {
            validateProductionRequirements(configuration, warnings);
        }

        // Rule 6: Protected keys require approval
        if (requireApprovalForProtectedKeys && isProtectedKey(configuration.configKey())) {
            warnings.add(
                "Configuration key '" + configuration.configKey() +
                "' is protected and requires approval for modification"
            );
        }

        // Rule 7: Validate data type matches value
        if (configuration.value() != null) {
            try {
                validateDataType(configuration.value(), configuration.dataType());
            } catch (ValidationException e) {
                errors.add(e.getMessage());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates a configuration change request.
     *
     * @param oldValue      the existing configuration (null for new configs)
     * @param newValue      the new value
     * @param configKey     the configuration key
     * @param environment   the environment
     * @param changedBy     the user making the change
     * @return validation result
     */
    public ChangeValidationResult validateChange(
            Configuration oldValue,
            Object newValue,
            String configKey,
            String environment,
            String changedBy) {

        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Cannot change data type of existing configuration
        if (oldValue != null && newValue != null) {
            if (!isCompatibleType(newValue, oldValue.dataType())) {
                errors.add(
                    "Cannot change data type from " + oldValue.dataType() +
                    " to " + detectType(newValue) + ". Create a new configuration instead."
                );
            }
        }

        // Rule 2: Protected keys require approval
        if (requireApprovalForProtectedKeys && isProtectedKey(configKey) && oldValue != null) {
            warnings.add(
                "Modification of protected key '" + configKey + "' requires approval"
            );
        }

        // Rule 3: Sensitive key changes in production require special handling
        if (isSensitiveKey(configKey) &&
            ("production".equalsIgnoreCase(environment) || "prod".equalsIgnoreCase(environment))) {
            warnings.add(
                "Sensitive configuration change in production requires audit logging"
            );
        }

        return new ChangeValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Checks if a configuration key is considered sensitive.
     *
     * @param configKey the configuration key
     * @return true if sensitive
     */
    public boolean isSensitiveKey(String configKey) {
        return SENSITIVE_KEY_PATTERN.matcher(configKey).matches();
    }

    /**
     * Checks if a configuration key is protected.
     *
     * @param configKey the configuration key
     * @return true if protected
     */
    public boolean isProtectedKey(String configKey) {
        return PROTECTED_KEYS.contains(configKey.toLowerCase());
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private void validateProductionRequirements(Configuration configuration, Set<String> warnings) {
        // Production must have non-null values for required configs
        if (configuration.required() && configuration.value() == null) {
            warnings.add("Production environment should not have null required configuration values");
        }

        // Production configs should have descriptions
        if (configuration.description() == null || configuration.description().isBlank()) {
            warnings.add("Production configurations should have descriptions");
        }

        // Production configs should be validated recently
        if (configuration.lastValidatedAt() == null ||
            configuration.lastValidatedAt().isBefore(Instant.now().minusSeconds(86400 * 30))) {
            warnings.add("Production configuration should be validated within the last 30 days");
        }
    }

    private void validateAgainstSchema(Object value, ConfigurationSchema schema) {
        // Check required constraint
        if (schema.required() && value == null) {
            throw new ValidationException("Value is required according to schema");
        }

        // If value is null and not required, skip further validation
        if (value == null) {
            return;
        }

        // Check allowed values
        if (schema.allowedValues() != null && !schema.allowedValues().isEmpty()) {
            String valueStr = value.toString();
            boolean matches = schema.allowedValues().stream()
                .anyMatch(allowed -> allowed.equals(valueStr));
            if (!matches) {
                throw new ValidationException(
                    "Value '" + value + "' is not in allowed values: " + schema.allowedValues()
                );
            }
        }

        // Type-specific validation would go here based on schema type
    }

    private void validateDataType(Object value, Configuration.ConfigurationDataType dataType) {
        switch (dataType) {
            case STRING -> {
                if (!(value instanceof String)) {
                    throw new ValidationException("Value must be String for data type STRING");
                }
            }
            case NUMBER -> {
                if (!(value instanceof Number)) {
                    throw new ValidationException("Value must be Number for data type NUMBER");
                }
            }
            case BOOLEAN -> {
                if (!(value instanceof Boolean)) {
                    throw new ValidationException("Value must be Boolean for data type BOOLEAN");
                }
            }
            case ARRAY -> {
                if (!(value instanceof java.util.Collection) && !value.getClass().isArray()) {
                    throw new ValidationException("Value must be Collection or Array for data type ARRAY");
                }
            }
            case BINARY -> {
                if (!(value instanceof byte[]) && !(value instanceof String)) {
                    throw new ValidationException("Value must be byte[] or base64 String for data type BINARY");
                }
            }
            case JSON, YAML, OBJECT -> {
                // These can be Map or String (parsed JSON/YAML)
                if (!(value instanceof java.util.Map) && !(value instanceof String)) {
                    throw new ValidationException(
                        "Value must be Map or String for data type " + dataType
                    );
                }
            }
        }
    }

    private boolean isCompatibleType(Object value, Configuration.ConfigurationDataType dataType) {
        try {
            validateDataType(value, dataType);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    private Configuration.ConfigurationDataType detectType(Object value) {
        if (value instanceof Number) return Configuration.ConfigurationDataType.NUMBER;
        if (value instanceof Boolean) return Configuration.ConfigurationDataType.BOOLEAN;
        if (value instanceof Collection || value.getClass().isArray()) return Configuration.ConfigurationDataType.ARRAY;
        if (value instanceof Map) return Configuration.ConfigurationDataType.OBJECT;
        if (value instanceof byte[]) return Configuration.ConfigurationDataType.BINARY;
        return Configuration.ConfigurationDataType.STRING; // Default
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ConfigurationValidationPolicy existing) {
        return new Builder()
            .enforceEncryptionForSensitive(existing.enforceEncryptionForSensitive)
            .requireApprovalForProtectedKeys(existing.requireApprovalForProtectedKeys);
    }

    public static class Builder {
        private boolean enforceEncryptionForSensitive = true;
        private boolean requireApprovalForProtectedKeys = true;

        public Builder enforceEncryptionForSensitive(boolean value) {
            this.enforceEncryptionForSensitive = value;
            return this;
        }

        public Builder requireApprovalForProtectedKeys(boolean value) {
            this.requireApprovalForProtectedKeys = value;
            return this;
        }

        public ConfigurationValidationPolicy build() {
            return new ConfigurationValidationPolicy(this);
        }
    }

    // ========================================================================
    // Result classes
    // ========================================================================

    public record ValidationResult(
        boolean isValid,
        Set<String> errors,
        Set<String> warnings
    ) {
        public ValidationResult {
            errors = Set.copyOf(errors);
            warnings = Set.copyOf(warnings);
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, Set.of(), Set.of());
        }

        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, Set.of(error), Set.of());
        }
    }

    public record ChangeValidationResult(
        boolean isValid,
        Set<String> errors,
        Set<String> warnings
    ) {
        public ChangeValidationResult {
            errors = Set.copyOf(errors);
            warnings = Set.copyOf(warnings);
        }

        public static ChangeValidationResult valid() {
            return new ChangeValidationResult(true, Set.of(), Set.of());
        }
    }

    private static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
