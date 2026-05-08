package com.gogidix.rapidassist.feature.flags.service.domain.aggregate;

import com.gogidix.rapidassist.feature.flags.service.domain.event.FeatureFlagCreatedEvent;
import com.gogidix.rapidassist.feature.flags.service.domain.event.FeatureFlagDeletedEvent;
import com.gogidix.rapidassist.feature.flags.service.domain.event.FeatureFlagUpdatedEvent;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FeatureFlag Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around feature flag management:
 * <ul>
 *   <li>Feature flags must have unique keys within a tenant-environment combination</li>
 *   <li>Feature flag changes must be tracked with audit trail</li>
 *   <li>Active flags cannot be deleted without approval</li>
 *   <li>Archived flags cannot be reactivated</li>
 * </ul>
 */
public class FeatureFlagAggregate {

    private final FeatureFlag featureFlag;
    private final List<FeatureFlagUpdatedEvent> pendingEvents;
    private boolean isNew;

    private FeatureFlagAggregate(FeatureFlag featureFlag, boolean isNew) {
        this.featureFlag = featureFlag;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new FeatureFlagAggregate for a new feature flag.
     *
     * @param tenantId        the tenant ID
     * @param key             the unique feature flag key
     * @param name            the feature flag name
     * @param description     the feature flag description
     * @param type            the flag type
     * @param rolloutStrategy the rollout strategy
     * @param environment     the environment
     * @param createdBy       the user creating the flag
     * @return a new FeatureFlagAggregate
     */
    public static FeatureFlagAggregate create(
            String tenantId,
            String key,
            String name,
            String description,
            FeatureFlag.FlagType type,
            FeatureFlag.RolloutStrategy rolloutStrategy,
            String environment,
            String createdBy) {

        // Validate business rules
        validateFeatureFlagKey(key);
        validateFeatureFlagName(name);

        FeatureFlag flag = FeatureFlag.builder()
            .tenantId(tenantId)
            .key(key)
            .name(name)
            .description(description)
            .type(type)
            .rolloutStrategy(rolloutStrategy)
            .environment(environment)
            .status(FeatureFlag.FlagStatus.DRAFT)
            .enabled(false)
            .createdBy(createdBy)
            .build();

        return new FeatureFlagAggregate(flag, true);
    }

    /**
     * Reconstructs an existing FeatureFlagAggregate from persistence.
     *
     * @param featureFlag the feature flag from persistence
     * @return a FeatureFlagAggregate
     */
    public static FeatureFlagAggregate fromExisting(FeatureFlag featureFlag) {
        return new FeatureFlagAggregate(featureFlag, false);
    }

    /**
     * Enables the feature flag.
     *
     * @param enabledBy the user enabling the flag
     * @return the FeatureFlagUpdatedEvent if successful
     */
    public FeatureFlagUpdatedEvent enable(String enabledBy) {
        if (featureFlag.enabled()) {
            throw new IllegalStateException("Feature flag is already enabled");
        }

        if (featureFlag.status() == FeatureFlag.FlagStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot enable an archived feature flag");
        }

        FeatureFlag oldFlag = featureFlag;
        FeatureFlag newFlag = featureFlag.enable();

        FeatureFlagUpdatedEvent event = new FeatureFlagUpdatedEvent(
            oldFlag, newFlag, enabledBy, "Feature flag enabled"
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Disables the feature flag.
     *
     * @param disabledBy the user disabling the flag
     * @param reason     the reason for disabling
     * @return the FeatureFlagUpdatedEvent if successful
     */
    public FeatureFlagUpdatedEvent disable(String disabledBy, String reason) {
        if (!featureFlag.enabled()) {
            throw new IllegalStateException("Feature flag is already disabled");
        }

        FeatureFlag oldFlag = featureFlag;
        FeatureFlag newFlag = featureFlag.disable();

        FeatureFlagUpdatedEvent event = new FeatureFlagUpdatedEvent(
            oldFlag, newFlag, disabledBy, reason != null ? reason : "Feature flag disabled"
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Activates the feature flag (changes status to ACTIVE).
     *
     * @param activatedBy the user activating the flag
     * @return the updated feature flag
     */
    public FeatureFlag activate(String activatedBy) {
        if (featureFlag.status() == FeatureFlag.FlagStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot activate an archived feature flag");
        }

        if (featureFlag.status() == FeatureFlag.FlagStatus.ACTIVE) {
            throw new IllegalStateException("Feature flag is already active");
        }

        return featureFlag.toBuilder()
            .status(FeatureFlag.FlagStatus.ACTIVE)
            .updatedBy(activatedBy)
            .build();
    }

    /**
     * Archives the feature flag.
     *
     * @param archivedBy the user archiving the flag
     * @return the FeatureFlagUpdatedEvent if successful
     */
    public FeatureFlagUpdatedEvent archive(String archivedBy) {
        if (featureFlag.status() == FeatureFlag.FlagStatus.ARCHIVED) {
            throw new IllegalStateException("Feature flag is already archived");
        }

        FeatureFlag oldFlag = featureFlag;
        FeatureFlag newFlag = featureFlag.archive();

        FeatureFlagUpdatedEvent event = new FeatureFlagUpdatedEvent(
            oldFlag, newFlag, archivedBy, "Feature flag archived"
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Updates the feature flag rollout strategy.
     *
     * @param newStrategy the new rollout strategy
     * @param updatedBy   the user making the change
     * @param reason      the reason for the change
     * @return the FeatureFlagUpdatedEvent if successful
     */
    public FeatureFlagUpdatedEvent updateRolloutStrategy(
            FeatureFlag.RolloutStrategy newStrategy,
            String updatedBy,
            String reason) {

        if (featureFlag.rolloutStrategy() == newStrategy) {
            throw new IllegalStateException("Rollout strategy is already set to " + newStrategy);
        }

        FeatureFlag oldFlag = featureFlag;
        FeatureFlag newFlag = featureFlag.toBuilder()
            .rolloutStrategy(newStrategy)
            .updatedBy(updatedBy)
            .build();

        FeatureFlagUpdatedEvent event = new FeatureFlagUpdatedEvent(
            oldFlag, newFlag, updatedBy, reason != null ? reason : "Rollout strategy updated"
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Marks this feature flag for deletion.
     *
     * @param deletedBy the user deleting the flag
     * @param reason    the reason for deletion
     * @return the FeatureFlagDeletedEvent
     */
    public FeatureFlagDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Active flags cannot be deleted
        if (featureFlag.status() == FeatureFlag.FlagStatus.ACTIVE) {
            throw new IllegalStateException(
                "Active feature flags must be archived before deletion."
            );
        }

        return new FeatureFlagDeletedEvent(
            featureFlag.id(),
            featureFlag.tenantId(),
            featureFlag.key(),
            featureFlag.name(),
            featureFlag.environment(),
            featureFlag.version(),
            featureFlag.enabled(),
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current feature flag state.
     *
     * @return set of validation errors, empty if valid
     */
    public Set<String> validate() {
        Set<String> errors = new java.util.HashSet<>();

        // Check if key is valid
        try {
            validateFeatureFlagKey(featureFlag.key());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Check if name is valid
        try {
            validateFeatureFlagName(featureFlag.name());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Check if enabled flag matches status
        if (featureFlag.enabled() && featureFlag.status() != FeatureFlag.FlagStatus.ACTIVE) {
            errors.add("Enabled flag must have ACTIVE status");
        }

        // Check percentage rollout validity
        if (featureFlag.rolloutStrategy() == FeatureFlag.RolloutStrategy.PERCENTAGE
            && featureFlag.percentageRollout() == null) {
            errors.add("Percentage rollout strategy requires percentage configuration");
        }

        return errors;
    }

    /**
     * Gets the underlying feature flag entity.
     *
     * @return the feature flag
     */
    public FeatureFlag featureFlag() {
        return featureFlag;
    }

    /**
     * Gets the feature flag ID.
     *
     * @return the ID
     */
    public String id() {
        return featureFlag.id();
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
    public List<FeatureFlagUpdatedEvent> getPendingEvents() {
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

    private static void validateFeatureFlagKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Feature flag key cannot be blank");
        }

        // Key must follow pattern: dot-separated or hyphen-separated namespaced keys
        if (!key.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                "Feature flag key must contain only alphanumeric characters, dots, hyphens, and underscores"
            );
        }

        if (key.length() > 255) {
            throw new IllegalArgumentException("Feature flag key cannot exceed 255 characters");
        }
    }

    private static void validateFeatureFlagName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Feature flag name cannot be blank");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Feature flag name cannot exceed 255 characters");
        }
    }
}
