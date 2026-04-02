package com.gogidix.rapidassist.policy.configuration.service.domain.aggregate;

import com.gogidix.rapidassist.policy.configuration.service.domain.event.PolicyCreatedEvent;
import com.gogidix.rapidassist.policy.configuration.service.domain.event.PolicyDeletedEvent;
import com.gogidix.rapidassist.policy.configuration.service.domain.event.PolicyUpdatedEvent;
import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Policy Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around policy management:
 * <ul>
 *   <li>Policies must be validated before activation</li>
 *   <li>Policy changes must be tracked with audit trail</li>
 *   <li>Active policies cannot be deleted without deactivation</li>
 *   <li>Policy rules must be valid for the policy type</li>
 * </ul>
 */
public class PolicyAggregate {

    private final Policy policy;
    private final List<PolicyUpdatedEvent> pendingEvents;
    private boolean isNew;

    private PolicyAggregate(Policy policy, boolean isNew) {
        this.policy = policy;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new PolicyAggregate for a new policy.
     *
     * @param tenantId      the tenant ID
     * @param policyKey     the policy key
     * @param name          the policy name
     * @param type          the policy type
     * @param rules         the policy rules
     * @param scope         the policy scope
     * @param environment   the environment
     * @param createdBy     the user creating the policy
     * @return a new PolicyAggregate
     */
    public static PolicyAggregate create(
            String tenantId,
            String policyKey,
            String name,
            Policy.PolicyType type,
            Map<String, Object> rules,
            Policy.PolicyScope scope,
            String environment,
            String createdBy) {

        // Validate business rules
        validatePolicyKey(policyKey);
        validatePolicyName(name);
        validateRulesForType(type, rules);

        Policy newPolicy = Policy.builder()
            .tenantId(tenantId)
            .policyKey(policyKey)
            .name(name)
            .type(type)
            .rules(rules)
            .scope(scope)
            .environment(environment)
            .status(Policy.PolicyStatus.DRAFT)
            .createdBy(createdBy)
            .build();

        return new PolicyAggregate(newPolicy, true);
    }

    /**
     * Reconstructs an existing PolicyAggregate from persistence.
     *
     * @param policy the policy from persistence
     * @return a PolicyAggregate
     */
    public static PolicyAggregate fromExisting(Policy policy) {
        return new PolicyAggregate(policy, false);
    }

    /**
     * Updates the policy rules.
     *
     * @param newRules   the new rules
     * @param updatedBy  the user making the change
     * @param reason     the reason for the change
     * @return the PolicyUpdatedEvent if successful
     */
    public PolicyUpdatedEvent updateRules(Map<String, Object> newRules, String updatedBy, String reason) {
        validateRulesForType(policy.type(), newRules);

        Policy oldPolicy = policy;
        Policy newPolicy = Policy.builder()
            .id(policy.id())
            .tenantId(policy.tenantId())
            .policyKey(policy.policyKey())
            .name(policy.name())
            .description(policy.description())
            .type(policy.type())
            .scope(policy.scope())
            .rules(newRules)
            .constraints(policy.constraints())
            .enforced(policy.enforced())
            .priority(policy.priority())
            .environment(policy.environment())
            .status(policy.status())
            .createdBy(policy.createdBy())
            .createdAt(policy.createdAt())
            .updatedBy(updatedBy)
            .version(policy.version() + 1)
            .tags(policy.tags())
            .build();

        PolicyUpdatedEvent event = new PolicyUpdatedEvent(
            oldPolicy, newPolicy, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Changes the policy status.
     *
     * @param newStatus the new status
     * @param changedBy the user making the change
     * @return the updated policy
     */
    public Policy changeStatus(Policy.PolicyStatus newStatus, String changedBy) {
        // Business rule: Archived policies cannot be reactivated
        if (policy.status() == Policy.PolicyStatus.ARCHIVED
                && newStatus == Policy.PolicyStatus.ACTIVE) {
            throw new IllegalStateException(
                "Cannot reactivate an archived policy. Create a new one instead."
            );
        }

        // Business rule: Policies with invalid rules cannot be activated
        if (newStatus == Policy.PolicyStatus.ACTIVE) {
            Set<String> errors = validate();
            if (!errors.isEmpty()) {
                throw new IllegalStateException(
                    "Cannot activate a policy with validation errors: " + errors
                );
            }
        }

        return Policy.builder()
            .id(policy.id())
            .tenantId(policy.tenantId())
            .policyKey(policy.policyKey())
            .name(policy.name())
            .description(policy.description())
            .type(policy.type())
            .scope(policy.scope())
            .rules(policy.rules())
            .constraints(policy.constraints())
            .enforced(policy.enforced())
            .priority(policy.priority())
            .environment(policy.environment())
            .status(newStatus)
            .createdBy(policy.createdBy())
            .createdAt(policy.createdAt())
            .updatedBy(changedBy)
            .version(policy.version() + 1)
            .tags(policy.tags())
            .build();
    }

    /**
     * Marks this policy for deletion.
     *
     * @param deletedBy the user deleting the policy
     * @param reason    the reason for deletion
     * @return the PolicyDeletedEvent
     */
    public PolicyDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Active policies cannot be deleted
        if (policy.status() == Policy.PolicyStatus.ACTIVE) {
            throw new IllegalStateException(
                "Active policies must be deactivated before deletion."
            );
        }

        // Business rule: Enforced policies require special approval
        if (policy.enforced() && policy.status() == Policy.PolicyStatus.INACTIVE) {
            throw new IllegalStateException(
                "Enforced policies require special approval for deletion."
            );
        }

        return new PolicyDeletedEvent(
            policy,
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current policy state.
     *
     * @return set of validation errors, empty if valid
     */
    public Set<String> validate() {
        Set<String> errors = new java.util.HashSet<>();

        // Check if policy has rules
        if (policy.rules() == null || policy.rules().isEmpty()) {
            errors.add("Policy must have at least one rule");
        }

        // Validate rules are appropriate for policy type
        if (policy.rules() != null) {
            try {
                validateRulesForType(policy.type(), policy.rules());
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }

        // Check if constraints are valid
        if (policy.constraints() == null) {
            errors.add("Policy constraints cannot be null");
        }

        // Check if scope is valid
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
    public Policy policy() {
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
    public List<PolicyUpdatedEvent> getPendingEvents() {
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

    private static void validatePolicyName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Policy name cannot be blank");
        }

        if (name.length() > 255) {
            throw new IllegalArgumentException("Policy name cannot exceed 255 characters");
        }
    }

    private static void validateRulesForType(Policy.PolicyType type, Map<String, Object> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Policy rules cannot be null or empty");
        }

        // Type-specific validation
        switch (type) {
            case RATE_LIMIT -> {
                if (!rules.containsKey("maxRequests") || !rules.containsKey("timeWindowMs")) {
                    throw new IllegalArgumentException(
                        "RATE_LIMIT policies must have 'maxRequests' and 'timeWindowMs' rules"
                    );
                }
            }
            case ACCESS_CONTROL -> {
                if (!rules.containsKey("roles") && !rules.containsKey("permissions")) {
                    throw new IllegalArgumentException(
                        "ACCESS_CONTROL policies must have 'roles' or 'permissions' rules"
                    );
                }
            }
            case SECURITY -> {
                if (!rules.containsKey("encryptionLevel") && !rules.containsKey("authenticationRequired")) {
                    throw new IllegalArgumentException(
                        "SECURITY policies must have security-related rules"
                    );
                }
            }
            case COMPLIANCE -> {
                if (!rules.containsKey("complianceStandard") && !rules.containsKey("requirements")) {
                    throw new IllegalArgumentException(
                        "COMPLIANCE policies must define compliance requirements"
                    );
                }
            }
        }
    }
}
