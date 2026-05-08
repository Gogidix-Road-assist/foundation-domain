package com.gogidix.rapidassist.tenancy.configuration.service.domain.policy;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain policy for validating tenant configurations.
 *
 * <p>This policy encapsulates business rules for tenant configuration validation:
 * <ul>
 *   <li>Tenant ID must be unique and properly formatted</li>
 *   <li>Domain must be unique and properly formatted</li>
 *   <li>Required fields must be present</li>
 *   <li>Production environments require additional validation</li>
 *   <li>Feature limits must be respected</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any tenant configuration.
 */
public class TenantConfigValidationPolicy {

    private static final Set<String> RESERVED_TENANT_IDS = Set.of(
        "system", "admin", "root", "api", "www", "mail", "ftp", "localhost"
    );

    private static final Set<String> RESERVED_DOMAINS = Set.of(
        "system.local", "admin.local", "api.gogidix.com", "www.gogidix.com"
    );

    private final boolean enforceUniqueTenantId;
    private final boolean enforceUniqueDomain;
    private final boolean validateFeatureLimits;

    private TenantConfigValidationPolicy(Builder builder) {
        this.enforceUniqueTenantId = builder.enforceUniqueTenantId;
        this.enforceUniqueDomain = builder.enforceUniqueDomain;
        this.validateFeatureLimits = builder.validateFeatureLimits;
    }

    /**
     * Validates a tenant configuration according to this policy.
     *
     * @param tenantConfig the tenant configuration to validate
     * @return validation result
     */
    public ValidationResult validate(TenantConfig tenantConfig) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Required fields must be present
        if (tenantConfig.tenantId() == null || tenantConfig.tenantId().isBlank()) {
            errors.add("Tenant ID is required");
        }

        if (tenantConfig.name() == null || tenantConfig.name().isBlank()) {
            errors.add("Tenant name is required");
        }

        if (tenantConfig.domain() == null || tenantConfig.domain().isBlank()) {
            errors.add("Tenant domain is required");
        }

        if (tenantConfig.environment() == null || tenantConfig.environment().isBlank()) {
            errors.add("Environment is required");
        }

        // Rule 2: Tenant ID must not be reserved
        if (tenantConfig.tenantId() != null && isReservedTenantId(tenantConfig.tenantId())) {
            errors.add(
                "Tenant ID '" + tenantConfig.tenantId() + "' is reserved and cannot be used"
            );
        }

        // Rule 3: Domain must not be reserved
        if (tenantConfig.domain() != null && isReservedDomain(tenantConfig.domain())) {
            errors.add(
                "Domain '" + tenantConfig.domain() + "' is reserved and cannot be used"
            );
        }

        // Rule 4: Validate settings
        if (tenantConfig.settings() != null) {
            validateSettings(tenantConfig.settings(), warnings);
        }

        // Rule 5: Validate limits
        if (tenantConfig.limits() != null) {
            validateLimits(tenantConfig.limits(), warnings);
        }

        // Rule 6: Validate features
        if (tenantConfig.features() != null && validateFeatureLimits) {
            validateFeatures(tenantConfig.features(), warnings);
        }

        // Rule 7: Production environments require additional validation
        if ("production".equalsIgnoreCase(tenantConfig.environment()) ||
            "prod".equalsIgnoreCase(tenantConfig.environment())) {
            validateProductionRequirements(tenantConfig, warnings);
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Checks if a tenant ID is reserved.
     *
     * @param tenantId the tenant ID
     * @return true if reserved
     */
    public boolean isReservedTenantId(String tenantId) {
        return RESERVED_TENANT_IDS.contains(tenantId.toLowerCase());
    }

    /**
     * Checks if a domain is reserved.
     *
     * @param domain the domain
     * @return true if reserved
     */
    public boolean isReservedDomain(String domain) {
        return RESERVED_DOMAINS.contains(domain.toLowerCase());
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private void validateSettings(TenantConfig.TenantSettings settings, Set<String> warnings) {
        if (settings.timezone() == null || settings.timezone().isBlank()) {
            warnings.add("Timezone should be specified for proper date/time handling");
        }

        if (settings.locale() == null || settings.locale().isBlank()) {
            warnings.add("Locale should be specified for proper localization");
        }

        if (settings.currency() == null || settings.currency().isBlank()) {
            warnings.add("Currency should be specified for financial operations");
        }
    }

    private void validateLimits(TenantConfig.TenantLimits limits, Set<String> warnings) {
        if (limits.maxUsers() <= 0) {
            warnings.add("Max users should be greater than 0 for tenant functionality");
        }

        if (limits.maxStorageGB() <= 0) {
            warnings.add("Max storage should be greater than 0 for tenant functionality");
        }

        if (limits.maxRequestsPerMinute() <= 0) {
            warnings.add("Max requests per minute should be greater than 0 for rate limiting");
        }

        if (limits.subscriptionExpiry() == null) {
            warnings.add("Subscription expiry date should be set for proper billing");
        } else if (limits.subscriptionExpiry().isBefore(java.time.LocalDate.now())) {
            warnings.add("Subscription has expired - consider renewal or deactivation");
        }
    }

    private void validateFeatures(TenantConfig.TenantFeatures features, Set<String> warnings) {
        if (features.enabledFeatures() == null || features.enabledFeatures().isEmpty()) {
            warnings.add("No features are enabled - tenant may have limited functionality");
        }

        // Check for conflicts between enabled and disabled features
        if (features.enabledFeatures() != null && features.disabledFeatures() != null) {
            Set<String> conflicts = new HashSet<>(features.enabledFeatures());
            conflicts.retainAll(features.disabledFeatures());
            if (!conflicts.isEmpty()) {
                warnings.add(
                    "Features cannot be both enabled and disabled: " + conflicts
                );
            }
        }
    }

    private void validateProductionRequirements(TenantConfig tenantConfig, Set<String> warnings) {
        // Production tenants should have proper limits configured
        if (tenantConfig.limits() != null) {
            if (tenantConfig.limits().maxUsers() < 10) {
                warnings.add("Production tenant should support at least 10 users");
            }

            if (tenantConfig.limits().maxStorageGB() < 10) {
                warnings.add("Production tenant should have at least 10GB storage");
            }
        }

        // Production tenants should have valid subscription
        if (tenantConfig.limits() != null && tenantConfig.limits().subscriptionExpiry() != null) {
            if (tenantConfig.limits().subscriptionExpiry().isBefore(java.time.LocalDate.now().plusMonths(1))) {
                warnings.add("Production tenant subscription expires soon - review renewal");
            }
        }
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TenantConfigValidationPolicy existing) {
        return new Builder()
            .enforceUniqueTenantId(existing.enforceUniqueTenantId)
            .enforceUniqueDomain(existing.enforceUniqueDomain)
            .validateFeatureLimits(existing.validateFeatureLimits);
    }

    public static class Builder {
        private boolean enforceUniqueTenantId = true;
        private boolean enforceUniqueDomain = true;
        private boolean validateFeatureLimits = true;

        public Builder enforceUniqueTenantId(boolean value) {
            this.enforceUniqueTenantId = value;
            return this;
        }

        public Builder enforceUniqueDomain(boolean value) {
            this.enforceUniqueDomain = value;
            return this;
        }

        public Builder validateFeatureLimits(boolean value) {
            this.validateFeatureLimits = value;
            return this;
        }

        public TenantConfigValidationPolicy build() {
            return new TenantConfigValidationPolicy(this);
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
}
