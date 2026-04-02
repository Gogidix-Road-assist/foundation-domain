package com.gogidix.rapidassist.feature.flags.service.domain.policy;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Domain policy for validating feature flags.
 *
 * <p>This policy encapsulates business rules for feature flag validation:
 * <ul>
 *   <li>Feature flags must have valid keys and names</li>
 *   <li>Production flags must have descriptions</li>
 *   <li>Certain rollout strategies require specific configurations</li>
 *   <li>Expired flags cannot be activated</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any feature flag.
 */
public class FeatureFlagValidationPolicy {

    // Patterns for detecting sensitive feature flag keys
    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
        ".*(?:beta|experimental|alpha|test|demo).*",
        Pattern.CASE_INSENSITIVE
    );

    // Protected keys that cannot be modified without special approval
    private static final Set<String> PROTECTED_KEYS = Set.of(
        "system.maintenance",
        "system.shutdown",
        "authentication.required"
    );

    private final boolean requireDescriptionForProduction;
    private final boolean warnOnExperimentalFlags;

    private FeatureFlagValidationPolicy(Builder builder) {
        this.requireDescriptionForProduction = builder.requireDescriptionForProduction;
        this.warnOnExperimentalFlags = builder.warnOnExperimentalFlags;
    }

    /**
     * Validates a feature flag according to this policy.
     *
     * @param featureFlag the feature flag to validate
     * @return validation result
     */
    public ValidationResult validate(FeatureFlag featureFlag) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Key must be valid
        validateKey(featureFlag.key(), errors);

        // Rule 2: Name must be valid
        validateName(featureFlag.name(), errors);

        // Rule 3: Production flags must have descriptions
        if (requireDescriptionForProduction && isProductionEnvironment(featureFlag.environment())) {
            if (featureFlag.description() == null || featureFlag.description().isBlank()) {
                errors.add("Production feature flags must have descriptions");
            }
        }

        // Rule 4: Validate rollout strategy configuration
        validateRolloutStrategy(featureFlag, errors);

        // Rule 5: Expired flags cannot be active
        if (featureFlag.expiresAt() != null && featureFlag.expiresAt().isBefore(Instant.now())) {
            if (featureFlag.status() == FeatureFlag.FlagStatus.ACTIVE) {
                errors.add("Expired feature flags cannot be active");
            }
        }

        // Rule 6: Experimental keys should trigger warnings in production
        if (warnOnExperimentalFlags && isExperimentalKey(featureFlag.key())) {
            if (isProductionEnvironment(featureFlag.environment())) {
                warnings.add(
                    "Feature flag key '" + featureFlag.key() +
                    "' appears to be experimental but is in production"
                );
            }
        }

        // Rule 7: Protected keys require approval
        if (isProtectedKey(featureFlag.key())) {
            warnings.add(
                "Feature flag key '" + featureFlag.key() +
                "' is protected and requires approval for modification"
            );
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates a feature flag change request.
     *
     * @param oldFlag   the existing feature flag (null for new flags)
     * @param newFlag   the new flag state
     * @param changedBy the user making the change
     * @return validation result
     */
    public ChangeValidationResult validateChange(
            FeatureFlag oldFlag,
            FeatureFlag newFlag,
            String changedBy) {

        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Cannot change flag type
        if (oldFlag != null && oldFlag.type() != newFlag.type()) {
            errors.add(
                "Cannot change flag type from " + oldFlag.type() +
                " to " + newFlag.type() + ". Create a new flag instead."
            );
        }

        // Rule 2: Protected keys require approval
        if (isProtectedKey(newFlag.key()) && oldFlag != null) {
            warnings.add(
                "Modification of protected key '" + newFlag.key() + "' requires approval"
            );
        }

        // Rule 3: Enabling flags in production requires special handling
        if (isProductionEnvironment(newFlag.environment())) {
            if (!oldFlag.enabled() && newFlag.enabled()) {
                warnings.add(
                    "Enabling feature flag in production requires audit logging"
                );
            }
        }

        return new ChangeValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Checks if a feature flag key is considered experimental.
     *
     * @param key the feature flag key
     * @return true if experimental
     */
    public boolean isExperimentalKey(String key) {
        return SENSITIVE_KEY_PATTERN.matcher(key).matches();
    }

    /**
     * Checks if a feature flag key is protected.
     *
     * @param key the feature flag key
     * @return true if protected
     */
    public boolean isProtectedKey(String key) {
        return PROTECTED_KEYS.contains(key.toLowerCase());
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private void validateKey(String key, Set<String> errors) {
        if (key == null || key.isBlank()) {
            errors.add("Feature flag key cannot be blank");
            return;
        }

        if (!key.matches("^[a-zA-Z0-9._-]+$")) {
            errors.add(
                "Feature flag key must contain only alphanumeric characters, dots, hyphens, and underscores"
            );
        }

        if (key.length() > 255) {
            errors.add("Feature flag key cannot exceed 255 characters");
        }
    }

    private void validateName(String name, Set<String> errors) {
        if (name == null || name.isBlank()) {
            errors.add("Feature flag name cannot be blank");
            return;
        }

        if (name.length() > 255) {
            errors.add("Feature flag name cannot exceed 255 characters");
        }
    }

    private void validateRolloutStrategy(FeatureFlag flag, Set<String> errors) {
        switch (flag.rolloutStrategy()) {
            case PERCENTAGE -> {
                if (flag.percentageRollout() == null) {
                    errors.add("Percentage rollout strategy requires percentage configuration");
                } else {
                    int percentage = flag.percentageRollout().percentage();
                    if (percentage < 0 || percentage > 100) {
                        errors.add("Percentage rollout must be between 0 and 100");
                    }
                }
            }
            case SPECIFIC_TENANTS -> {
                if (flag.allowedTenants() == null || flag.allowedTenants().isEmpty()) {
                    errors.add("Specific tenants rollout strategy requires at least one tenant");
                }
            }
            case SPECIFIC_USERS -> {
                if (flag.allowedUsers() == null || flag.allowedUsers().isEmpty()) {
                    errors.add("Specific users rollout strategy requires at least one user");
                }
            }
            case COUNTRY_BASED -> {
                if (flag.allowedCountries() == null || flag.allowedCountries().isEmpty()) {
                    errors.add("Country-based rollout strategy requires at least one country");
                }
            }
        }
    }

    private boolean isProductionEnvironment(String environment) {
        return "production".equalsIgnoreCase(environment)
            || "prod".equalsIgnoreCase(environment);
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(FeatureFlagValidationPolicy existing) {
        return new Builder()
            .requireDescriptionForProduction(existing.requireDescriptionForProduction)
            .warnOnExperimentalFlags(existing.warnOnExperimentalFlags);
    }

    public static class Builder {
        private boolean requireDescriptionForProduction = true;
        private boolean warnOnExperimentalFlags = true;

        public Builder requireDescriptionForProduction(boolean value) {
            this.requireDescriptionForProduction = value;
            return this;
        }

        public Builder warnOnExperimentalFlags(boolean value) {
            this.warnOnExperimentalFlags = value;
            return this;
        }

        public FeatureFlagValidationPolicy build() {
            return new FeatureFlagValidationPolicy(this);
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
}
