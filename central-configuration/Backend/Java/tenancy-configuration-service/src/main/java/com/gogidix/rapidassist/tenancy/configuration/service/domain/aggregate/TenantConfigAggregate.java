package com.gogidix.rapidassist.tenancy.configuration.service.domain.aggregate;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.event.TenantConfigCreatedEvent;
import com.gogidix.rapidassist.tenancy.configuration.service.domain.event.TenantConfigDeletedEvent;
import com.gogidix.rapidassist.tenancy.configuration.service.domain.event.TenantConfigUpdatedEvent;
import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * TenantConfig Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around tenant configuration management:
 * <ul>
 *   <li>Tenant configurations must be validated before creation</li>
 *   <li>Configuration changes must be tracked with audit trail</li>
 *   <li>Active configurations cannot be deleted without deactivation</li>
 *   <li>Required fields must be present</li>
 * </ul>
 */
public class TenantConfigAggregate {

    private final TenantConfig tenantConfig;
    private final List<TenantConfigUpdatedEvent> pendingEvents;
    private boolean isNew;

    private TenantConfigAggregate(TenantConfig tenantConfig, boolean isNew) {
        this.tenantConfig = tenantConfig;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new TenantConfigAggregate for a new tenant configuration.
     *
     * @param tenantId      the tenant ID
     * @param name          the tenant name
     * @param domain        the tenant domain
     * @param environment   the environment
     * @param settings      the tenant settings
     * @param limits        the tenant limits
     * @param features      the tenant features
     * @param active        whether the tenant is active
     * @param createdBy     the user creating the configuration
     * @return a new TenantConfigAggregate
     */
    public static TenantConfigAggregate create(
            String tenantId,
            String name,
            String domain,
            String environment,
            TenantConfig.TenantSettings settings,
            TenantConfig.TenantLimits limits,
            TenantConfig.TenantFeatures features,
            boolean active,
            String createdBy) {

        // Validate business rules
        validateTenantId(tenantId);
        validateName(name);
        validateDomain(domain);
        validateEnvironment(environment);

        TenantConfig config = TenantConfig.builder()
            .tenantId(tenantId)
            .name(name)
            .domain(domain)
            .environment(environment)
            .settings(settings)
            .limits(limits)
            .features(features)
            .active(active)
            .createdBy(createdBy)
            .build();

        return new TenantConfigAggregate(config, true);
    }

    /**
     * Reconstructs an existing TenantConfigAggregate from persistence.
     *
     * @param tenantConfig the tenant configuration from persistence
     * @return a TenantConfigAggregate
     */
    public static TenantConfigAggregate fromExisting(TenantConfig tenantConfig) {
        return new TenantConfigAggregate(tenantConfig, false);
    }

    /**
     * Updates the tenant configuration name.
     *
     * @param newName   the new name
     * @param updatedBy the user making the change
     * @param reason    the reason for the change
     * @return the TenantConfigUpdatedEvent if successful
     */
    public TenantConfigUpdatedEvent updateName(String newName, String updatedBy, String reason) {
        validateName(newName);

        TenantConfig oldConfig = this.tenantConfig;
        TenantConfig newConfig = TenantConfig.builder()
            .id(oldConfig.id())
            .tenantId(oldConfig.tenantId())
            .name(newName)
            .domain(oldConfig.domain())
            .environment(oldConfig.environment())
            .settings(oldConfig.settings())
            .limits(oldConfig.limits())
            .features(oldConfig.features())
            .active(oldConfig.active())
            .createdBy(oldConfig.createdBy())
            .createdAt(oldConfig.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .version(oldConfig.version() + 1)
            .build();

        TenantConfigUpdatedEvent event = new TenantConfigUpdatedEvent(
            oldConfig, newConfig, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Updates the tenant configuration settings.
     *
     * @param newSettings the new settings
     * @param updatedBy   the user making the change
     * @param reason      the reason for the change
     * @return the TenantConfigUpdatedEvent if successful
     */
    public TenantConfigUpdatedEvent updateSettings(TenantConfig.TenantSettings newSettings, String updatedBy, String reason) {
        TenantConfig oldConfig = this.tenantConfig;
        TenantConfig newConfig = TenantConfig.builder()
            .id(oldConfig.id())
            .tenantId(oldConfig.tenantId())
            .name(oldConfig.name())
            .domain(oldConfig.domain())
            .environment(oldConfig.environment())
            .settings(newSettings)
            .limits(oldConfig.limits())
            .features(oldConfig.features())
            .active(oldConfig.active())
            .createdBy(oldConfig.createdBy())
            .createdAt(oldConfig.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .version(oldConfig.version() + 1)
            .build();

        TenantConfigUpdatedEvent event = new TenantConfigUpdatedEvent(
            oldConfig, newConfig, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Updates the tenant configuration features.
     *
     * @param newFeatures the new features
     * @param updatedBy   the user making the change
     * @param reason      the reason for the change
     * @return the TenantConfigUpdatedEvent if successful
     */
    public TenantConfigUpdatedEvent updateFeatures(TenantConfig.TenantFeatures newFeatures, String updatedBy, String reason) {
        TenantConfig oldConfig = this.tenantConfig;
        TenantConfig newConfig = TenantConfig.builder()
            .id(oldConfig.id())
            .tenantId(oldConfig.tenantId())
            .name(oldConfig.name())
            .domain(oldConfig.domain())
            .environment(oldConfig.environment())
            .settings(oldConfig.settings())
            .limits(oldConfig.limits())
            .features(newFeatures)
            .active(oldConfig.active())
            .createdBy(oldConfig.createdBy())
            .createdAt(oldConfig.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .version(oldConfig.version() + 1)
            .build();

        TenantConfigUpdatedEvent event = new TenantConfigUpdatedEvent(
            oldConfig, newConfig, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Activates the tenant configuration.
     *
     * @param activatedBy the user activating the configuration
     * @return the updated configuration
     */
    public TenantConfig activate(String activatedBy) {
        if (tenantConfig.active()) {
            throw new IllegalStateException("Tenant configuration is already active");
        }

        return TenantConfig.builder()
            .id(tenantConfig.id())
            .tenantId(tenantConfig.tenantId())
            .name(tenantConfig.name())
            .domain(tenantConfig.domain())
            .environment(tenantConfig.environment())
            .settings(tenantConfig.settings())
            .limits(tenantConfig.limits())
            .features(tenantConfig.features())
            .active(true)
            .createdBy(tenantConfig.createdBy())
            .createdAt(tenantConfig.createdAt())
            .updatedBy(activatedBy)
            .updatedAt(java.time.Instant.now())
            .version(tenantConfig.version() + 1)
            .build();
    }

    /**
     * Deactivates the tenant configuration.
     *
     * @param deactivatedBy the user deactivating the configuration
     * @return the updated configuration
     */
    public TenantConfig deactivate(String deactivatedBy) {
        if (!tenantConfig.active()) {
            throw new IllegalStateException("Tenant configuration is already inactive");
        }

        return TenantConfig.builder()
            .id(tenantConfig.id())
            .tenantId(tenantConfig.tenantId())
            .name(tenantConfig.name())
            .domain(tenantConfig.domain())
            .environment(tenantConfig.environment())
            .settings(tenantConfig.settings())
            .limits(tenantConfig.limits())
            .features(tenantConfig.features())
            .active(false)
            .createdBy(tenantConfig.createdBy())
            .createdAt(tenantConfig.createdAt())
            .updatedBy(deactivatedBy)
            .updatedAt(java.time.Instant.now())
            .version(tenantConfig.version() + 1)
            .build();
    }

    /**
     * Marks this tenant configuration for deletion.
     *
     * @param deletedBy the user deleting the configuration
     * @param reason    the reason for deletion
     * @return the TenantConfigDeletedEvent
     */
    public TenantConfigDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Active configurations must be deactivated first
        if (tenantConfig.active()) {
            throw new IllegalStateException(
                "Active tenant configurations must be deactivated before deletion."
            );
        }

        return new TenantConfigDeletedEvent(
            tenantConfig.id(),
            tenantConfig.tenantId(),
            tenantConfig.name(),
            tenantConfig.domain(),
            tenantConfig.environment(),
            tenantConfig.version(),
            tenantConfig,
            deletedBy,
            reason
        );
    }

    /**
     * Checks if this tenant has a specific feature enabled.
     *
     * @param feature the feature name
     * @return true if the feature is enabled
     */
    public boolean hasFeature(String feature) {
        return tenantConfig.hasFeature(feature);
    }

    /**
     * Gets the underlying tenant configuration entity.
     *
     * @return the tenant configuration
     */
    public TenantConfig tenantConfig() {
        return tenantConfig;
    }

    /**
     * Gets the configuration ID.
     *
     * @return the ID
     */
    public String id() {
        return tenantConfig.id();
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
    public List<TenantConfigUpdatedEvent> getPendingEvents() {
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

    private static void validateTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be blank");
        }

        if (tenantId.length() > 255) {
            throw new IllegalArgumentException("Tenant ID cannot exceed 255 characters");
        }

        if (!tenantId.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                "Tenant ID must contain only alphanumeric characters, dots, hyphens, and underscores"
            );
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tenant name cannot be blank");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Tenant name cannot exceed 255 characters");
        }
    }

    private static void validateDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Tenant domain cannot be blank");
        }

        if (domain.length() > 255) {
            throw new IllegalArgumentException("Tenant domain cannot exceed 255 characters");
        }

        // Basic domain validation
        if (!domain.matches("^[a-zA-Z0-9.-]+$")) {
            throw new IllegalArgumentException(
                "Tenant domain must contain only valid domain characters"
            );
        }
    }

    private static void validateEnvironment(String environment) {
        if (environment == null || environment.isBlank()) {
            throw new IllegalArgumentException("Environment cannot be blank");
        }

        if (!List.of("development", "staging", "production", "dev", "stage", "prod").contains(environment.toLowerCase())) {
            throw new IllegalArgumentException(
                "Environment must be one of: development, staging, production, dev, stage, prod"
            );
        }
    }
}
