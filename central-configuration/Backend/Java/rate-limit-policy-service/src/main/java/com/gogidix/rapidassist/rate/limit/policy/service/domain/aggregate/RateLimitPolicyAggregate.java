package com.gogidix.rapidassist.rate.limit.policy.service.domain.aggregate;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.event.RateLimitPolicyCreatedEvent;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.event.RateLimitPolicyDeletedEvent;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.event.RateLimitPolicyUpdatedEvent;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * Rate Limit Policy Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around rate limit policy management:
 * <ul>
 *   <li>Policies must have valid configuration parameters</li>
 *   <li>Policy changes must be tracked with audit trail</li>
 *   <li>Enabled policies cannot be deleted without deactivation</li>
 *   <li>Global policies require special approval for modification</li>
 * </ul>
 */
public class RateLimitPolicyAggregate {

    private final RateLimitPolicy policy;
    private final List<RateLimitPolicyUpdatedEvent> pendingEvents;
    private boolean isNew;

    private RateLimitPolicyAggregate(RateLimitPolicy policy, boolean isNew) {
        this.policy = policy;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new RateLimitPolicyAggregate for a new policy.
     *
     * @param tenantId      the tenant ID
     * @param policyKey     the policy key
     * @param name          the policy name
     * @param description   the policy description
     * @param limitType     the limit type
     * @param config        the rate limit configuration
     * @param scope         the policy scope
     * @param enabled       whether the policy is enabled
     * @param environment   the environment
     * @param createdBy     the user creating the policy
     * @return a new RateLimitPolicyAggregate
     */
    public static RateLimitPolicyAggregate create(
            String tenantId,
            String policyKey,
            String name,
            String description,
            RateLimitPolicy.LimitType limitType,
            RateLimitPolicy.RateLimitConfig config,
            RateLimitPolicy.Scope scope,
            boolean enabled,
            String environment,
            String createdBy) {

        // Validate business rules
        validatePolicyKey(policyKey);
        validateName(name);
        validateConfig(config);

        RateLimitPolicy rateLimitPolicy = RateLimitPolicy.builder()
            .tenantId(tenantId)
            .policyKey(policyKey)
            .name(name)
            .description(description)
            .limitType(limitType)
            .config(config)
            .scope(scope)
            .enabled(enabled)
            .environment(environment)
            .createdBy(createdBy)
            .build();

        return new RateLimitPolicyAggregate(rateLimitPolicy, true);
    }

    /**
     * Reconstructs an existing RateLimitPolicyAggregate from persistence.
     *
     * @param policy the policy from persistence
     * @return a RateLimitPolicyAggregate
     */
    public static RateLimitPolicyAggregate fromExisting(RateLimitPolicy policy) {
        return new RateLimitPolicyAggregate(policy, false);
    }

    /**
     * Updates the policy configuration.
     *
     * @param newName      the new name
     * @param newDescription the new description
     * @param newConfig    the new configuration
     * @param newScope     the new scope
     * @param updatedBy    the user making the change
     * @param reason       the reason for the change
     * @return the RateLimitPolicyUpdatedEvent if successful
     */
    public RateLimitPolicyUpdatedEvent updatePolicy(
            String newName,
            String newDescription,
            RateLimitPolicy.RateLimitConfig newConfig,
            RateLimitPolicy.Scope newScope,
            String updatedBy,
            String reason) {

        if (newName != null) {
            validateName(newName);
        }

        if (newConfig != null) {
            validateConfig(newConfig);
        }

        RateLimitPolicy oldPolicy = policy;

        RateLimitPolicy.Builder builder = RateLimitPolicy.builder()
            .id(oldPolicy.id())
            .tenantId(oldPolicy.tenantId())
            .policyKey(oldPolicy.policyKey())
            .name(newName != null ? newName : oldPolicy.name())
            .description(newDescription != null ? newDescription : oldPolicy.description())
            .limitType(oldPolicy.limitType())
            .config(newConfig != null ? newConfig : oldPolicy.config())
            .scope(newScope != null ? newScope : oldPolicy.scope())
            .enabled(oldPolicy.enabled())
            .environment(oldPolicy.environment())
            .createdBy(oldPolicy.createdBy())
            .createdAt(oldPolicy.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .version(oldPolicy.version() + 1);

        RateLimitPolicy newPolicy = builder.build();

        RateLimitPolicyUpdatedEvent event = new RateLimitPolicyUpdatedEvent(
            oldPolicy, newPolicy, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Changes the policy enabled status.
     *
     * @param enabled the new enabled status
     * @param changedBy the user making the change
     * @return the updated policy
     */
    public RateLimitPolicy changeEnabledStatus(boolean enabled, String changedBy) {
        // Business rule: Global policies cannot be disabled without approval
        if (policy.limitType() == RateLimitPolicy.LimitType.GLOBAL && !enabled) {
            throw new IllegalStateException(
                "Global policies cannot be disabled without explicit approval. "
                + "Please use the deactivation workflow instead."
            );
        }

        return RateLimitPolicy.builder()
            .id(policy.id())
            .tenantId(policy.tenantId())
            .policyKey(policy.policyKey())
            .name(policy.name())
            .description(policy.description())
            .limitType(policy.limitType())
            .config(policy.config())
            .scope(policy.scope())
            .enabled(enabled)
            .environment(policy.environment())
            .createdBy(policy.createdBy())
            .createdAt(policy.createdAt())
            .updatedBy(changedBy)
            .updatedAt(java.time.Instant.now())
            .version(policy.version())
            .build();
    }

    /**
     * Marks this policy for deletion.
     *
     * @param deletedBy the user deleting the policy
     * @param reason    the reason for deletion
     * @return the RateLimitPolicyDeletedEvent
     */
    public RateLimitPolicyDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Global policies cannot be deleted
        if (policy.limitType() == RateLimitPolicy.LimitType.GLOBAL) {
            throw new IllegalStateException(
                "Global policies cannot be deleted. Disable them instead."
            );
        }

        // Business rule: Enabled policies must be disabled before deletion
        if (policy.enabled()) {
            throw new IllegalStateException(
                "Enabled policies must be disabled before deletion."
            );
        }

        return new RateLimitPolicyDeletedEvent(
            policy.id(),
            policy.tenantId(),
            policy.policyKey(),
            policy.name(),
            policy.environment(),
            policy.version(),
            policy,
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current policy configuration.
     *
     * @return list of validation errors, empty if valid
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // Validate policy key
        try {
            validatePolicyKey(policy.policyKey());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Validate name
        try {
            validateName(policy.name());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Validate configuration
        try {
            validateConfig(policy.config());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Validate scope
        if (policy.scope() == null) {
            errors.add("Policy scope cannot be null");
        }

        return errors;
    }

    /**
     * Gets the underlying policy entity.
     *
     * @return the policy
     */
    public RateLimitPolicy policy() {
        return policy;
    }

    /**
     * Gets the policy ID.
     *
     * @return the ID
     */
    public String id() {
        return policy.id();
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
    public List<RateLimitPolicyUpdatedEvent> getPendingEvents() {
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

    private static void validatePolicyKey(String policyKey) {
        if (policyKey == null || policyKey.isBlank()) {
            throw new IllegalArgumentException("Policy key cannot be blank");
        }

        // Policy key must follow pattern: dot-separated namespaced keys
        if (!policyKey.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                "Policy key must contain only alphanumeric characters, dots, hyphens, and underscores"
            );
        }

        if (policyKey.length() > 255) {
            throw new IllegalArgumentException("Policy key cannot exceed 255 characters");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Policy name cannot be blank");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Policy name cannot exceed 255 characters");
        }
    }

    private static void validateConfig(RateLimitPolicy.RateLimitConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Rate limit configuration cannot be null");
        }

        if (config.requestsPerMinute() <= 0) {
            throw new IllegalArgumentException("Requests per minute must be positive");
        }

        if (config.requestsPerHour() <= 0) {
            throw new IllegalArgumentException("Requests per hour must be positive");
        }

        if (config.requestsPerDay() <= 0) {
            throw new IllegalArgumentException("Requests per day must be positive");
        }

        if (config.burstCapacity() <= 0) {
            throw new IllegalArgumentException("Burst capacity must be positive");
        }

        if (config.windowSizeMs() <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }

        if (config.algorithm() == null || config.algorithm().isBlank()) {
            throw new IllegalArgumentException("Algorithm cannot be blank");
        }
    }
}
