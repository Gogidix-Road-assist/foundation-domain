package com.gogidix.rapidassist.dynamic.routing.config.service.domain.aggregate;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.event.RoutingRuleCreatedEvent;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.event.RoutingRuleDeletedEvent;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.event.RoutingRuleUpdatedEvent;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Routing Rule Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around routing rule management:
 * <ul>
 *   <li>Routing rules must have valid patterns and targets</li>
 *   <li>Rule changes must be tracked with audit trail</li>
 *   <li>Active rules cannot be deleted without deactivation</li>
 *   <li>Priority conflicts must be resolved</li>
 * </ul>
 */
public class RoutingRuleAggregate {

    private final RoutingRule routingRule;
    private final List<RoutingRuleUpdatedEvent> pendingEvents;
    private boolean isNew;

    private RoutingRuleAggregate(RoutingRule routingRule, boolean isNew) {
        this.routingRule = routingRule;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new RoutingRuleAggregate for a new routing rule.
     *
     * @param tenantId      the tenant ID
     * @param ruleName      the rule name
     * @param pattern       the route pattern
     * @param target        the route target
     * @param strategy      the routing strategy
     * @param priority      the priority
     * @param environment   the environment
     * @param createdBy     the user creating the rule
     * @return a new RoutingRuleAggregate
     */
    public static RoutingRuleAggregate create(
            String tenantId,
            String ruleName,
            RoutingRule.RoutePattern pattern,
            RoutingRule.RouteTarget target,
            RoutingRule.RoutingStrategy strategy,
            int priority,
            String environment,
            String createdBy) {

        // Validate business rules
        validateRuleName(ruleName);
        validatePattern(pattern);
        validateTarget(target);
        validatePriority(priority);

        RoutingRule rule = RoutingRule.builder()
            .tenantId(tenantId)
            .ruleName(ruleName)
            .pattern(pattern)
            .target(target)
            .strategy(strategy)
            .priority(priority)
            .environment(environment)
            .createdBy(createdBy)
            .updatedBy(createdBy)
            .build();

        return new RoutingRuleAggregate(rule, true);
    }

    /**
     * Reconstructs an existing RoutingRuleAggregate from persistence.
     *
     * @param routingRule the routing rule from persistence
     * @return a RoutingRuleAggregate
     */
    public static RoutingRuleAggregate fromExisting(RoutingRule routingRule) {
        return new RoutingRuleAggregate(routingRule, false);
    }

    /**
     * Updates the routing rule target.
     *
     * @param newTarget   the new target
     * @param updatedBy   the user making the change
     * @param reason      the reason for the change
     * @return the RoutingRuleUpdatedEvent if successful
     */
    public RoutingRuleUpdatedEvent updateTarget(RoutingRule.RouteTarget newTarget, String updatedBy, String reason) {
        validateTarget(newTarget);

        RoutingRule oldRule = routingRule;
        RoutingRule newRule = RoutingRule.builder()
            .id(routingRule.id())
            .tenantId(routingRule.tenantId())
            .ruleName(routingRule.ruleName())
            .pattern(routingRule.pattern())
            .target(newTarget)
            .strategy(routingRule.strategy())
            .conditions(routingRule.conditions())
            .config(routingRule.config())
            .priority(routingRule.priority())
            .active(routingRule.active())
            .environment(routingRule.environment())
            .createdBy(routingRule.createdBy())
            .createdAt(routingRule.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .version(routingRule.version() + 1)
            .build();

        Set<String> changedFields = new HashSet<>();
        changedFields.add("target");

        RoutingRuleUpdatedEvent event = new RoutingRuleUpdatedEvent(
            oldRule, newRule, updatedBy, reason, changedFields
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Updates the routing rule priority.
     *
     * @param newPriority the new priority
     * @param updatedBy   the user making the change
     * @param reason      the reason for the change
     * @return the RoutingRuleUpdatedEvent if successful
     */
    public RoutingRuleUpdatedEvent updatePriority(int newPriority, String updatedBy, String reason) {
        validatePriority(newPriority);

        RoutingRule oldRule = routingRule;
        RoutingRule newRule = RoutingRule.builder()
            .id(routingRule.id())
            .tenantId(routingRule.tenantId())
            .ruleName(routingRule.ruleName())
            .pattern(routingRule.pattern())
            .target(routingRule.target())
            .strategy(routingRule.strategy())
            .conditions(routingRule.conditions())
            .config(routingRule.config())
            .priority(newPriority)
            .active(routingRule.active())
            .environment(routingRule.environment())
            .createdBy(routingRule.createdBy())
            .createdAt(routingRule.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .version(routingRule.version() + 1)
            .build();

        Set<String> changedFields = new HashSet<>();
        changedFields.add("priority");

        RoutingRuleUpdatedEvent event = new RoutingRuleUpdatedEvent(
            oldRule, newRule, updatedBy, reason, changedFields
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Activates or deactivates the routing rule.
     *
     * @param active     the new active status
     * @param changedBy  the user making the change
     * @return the updated routing rule
     */
    public RoutingRule changeActiveStatus(boolean active, String changedBy) {
        // Business rule: Cannot deactivate a critical rule without approval
        if (!active && routingRule.priority() == 0) {
            throw new IllegalStateException(
                "Cannot deactivate critical routing rule (priority 0). " +
                "Change priority first or get approval."
            );
        }

        return RoutingRule.builder()
            .id(routingRule.id())
            .tenantId(routingRule.tenantId())
            .ruleName(routingRule.ruleName())
            .pattern(routingRule.pattern())
            .target(routingRule.target())
            .strategy(routingRule.strategy())
            .conditions(routingRule.conditions())
            .config(routingRule.config())
            .priority(routingRule.priority())
            .active(active)
            .environment(routingRule.environment())
            .createdBy(routingRule.createdBy())
            .createdAt(routingRule.createdAt())
            .updatedBy(changedBy)
            .updatedAt(java.time.Instant.now())
            .version(routingRule.version() + 1)
            .build();
    }

    /**
     * Marks this routing rule for deletion.
     *
     * @param deletedBy the user deleting the rule
     * @param reason    the reason for deletion
     * @return the RoutingRuleDeletedEvent
     */
    public RoutingRuleDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Active rules must be deactivated before deletion
        if (routingRule.active()) {
            throw new IllegalStateException(
                "Active routing rules must be deactivated before deletion."
            );
        }

        return new RoutingRuleDeletedEvent(
            routingRule.id(),
            routingRule.tenantId(),
            routingRule.ruleName(),
            routingRule.environment(),
            routingRule.priority(),
            routingRule.version(),
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current routing rule.
     *
     * @return set of validation errors, empty if valid
     */
    public Set<String> validate() {
        Set<String> errors = new HashSet<>();

        // Validate rule name
        try {
            validateRuleName(routingRule.ruleName());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Validate pattern
        try {
            validatePattern(routingRule.pattern());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Validate target
        try {
            validateTarget(routingRule.target());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Validate priority
        try {
            validatePriority(routingRule.priority());
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        // Business rule: Production rules must have descriptions or valid patterns
        if ("production".equalsIgnoreCase(routingRule.environment()) ||
            "prod".equalsIgnoreCase(routingRule.environment())) {
            if (routingRule.conditions().isEmpty()) {
                errors.add("Production routing rules should have conditions defined");
            }
        }

        return errors;
    }

    /**
     * Gets the underlying routing rule entity.
     *
     * @return the routing rule
     */
    public RoutingRule routingRule() {
        return routingRule;
    }

    /**
     * Gets the routing rule ID.
     *
     * @return the ID
     */
    public String id() {
        return routingRule.id();
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
    public List<RoutingRuleUpdatedEvent> getPendingEvents() {
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

    private static void validateRuleName(String ruleName) {
        if (ruleName == null || ruleName.isBlank()) {
            throw new IllegalArgumentException("Routing rule name cannot be blank");
        }

        if (ruleName.length() > 100) {
            throw new IllegalArgumentException("Routing rule name cannot exceed 100 characters");
        }

        // Rule name must follow pattern: alphanumeric with hyphens and underscores
        if (!ruleName.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException(
                "Routing rule name must contain only alphanumeric characters, hyphens, and underscores"
            );
        }
    }

    private static void validatePattern(RoutingRule.RoutePattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Routing pattern cannot be null");
        }

        if (pattern.value() == null || pattern.value().isBlank()) {
            throw new IllegalArgumentException("Routing pattern value cannot be blank");
        }

        if (pattern.methods() == null || pattern.methods().isEmpty()) {
            throw new IllegalArgumentException("Routing pattern must specify HTTP methods");
        }
    }

    private static void validateTarget(RoutingRule.RouteTarget target) {
        if (target == null) {
            throw new IllegalArgumentException("Routing target cannot be null");
        }

        if (target.endpoint() == null || target.endpoint().isBlank()) {
            throw new IllegalArgumentException("Routing target endpoint cannot be blank");
        }

        // Basic URL validation
        String endpoint = target.endpoint();
        if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://") &&
            !endpoint.startsWith("/")) {
            throw new IllegalArgumentException(
                "Routing target endpoint must be a valid URL or path starting with /"
            );
        }
    }

    private static void validatePriority(int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException("Routing rule priority cannot be negative");
        }

        if (priority > 1000) {
            throw new IllegalArgumentException("Routing rule priority cannot exceed 1000");
        }
    }
}
