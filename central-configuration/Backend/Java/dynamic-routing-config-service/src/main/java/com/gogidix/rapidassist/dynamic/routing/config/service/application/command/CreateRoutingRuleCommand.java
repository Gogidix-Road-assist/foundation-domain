package com.gogidix.rapidassist.dynamic.routing.config.service.application.command;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

/**
 * Command object for creating a new Routing Rule.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new routing rule entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateRoutingRuleCommand(
    String tenantId,
    String ruleName,
    RoutingRule.RoutePattern pattern,
    RoutingRule.RouteTarget target,
    RoutingRule.RoutingStrategy strategy,
    int priority,
    boolean active,
    String environment,
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
            && ruleName != null && !ruleName.isBlank()
            && pattern != null
            && target != null
            && strategy != null
            && priority >= 0 && priority <= 1000
            && environment != null && !environment.isBlank()
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateRoutingRuleCommand with defaults
     */
    public CreateRoutingRuleCommand withDefaults() {
        return new CreateRoutingRuleCommand(
            this.tenantId,
            this.ruleName,
            this.pattern,
            this.target,
            this.strategy != null ? this.strategy : RoutingRule.RoutingStrategy.ROUND_ROBIN,
            this.priority,
            this.active,
            this.environment,
            this.createdBy,
            this.reason != null ? this.reason : "Initial routing rule creation"
        );
    }
}
