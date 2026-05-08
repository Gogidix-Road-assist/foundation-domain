package com.gogidix.rapidassist.dynamic.routing.config.service.domain.event;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

/**
 * Domain event published when a new Routing Rule is created.
 * This event can be consumed by other services to react to new routing rules.
 */
public class RoutingRuleCreatedEvent extends DomainEvent<RoutingRule> {

    private final String ruleName;
    private final String environment;
    private final String targetEndpoint;
    private final int priority;
    private final boolean active;
    private final String createdBy;

    public RoutingRuleCreatedEvent(RoutingRule routingRule, String createdBy) {
        super(
            routingRule.id(),
            "RoutingRuleCreated",
            routingRule.tenantId(),
            routingRule.version()
        );
        this.ruleName = routingRule.ruleName();
        this.environment = routingRule.environment();
        this.targetEndpoint = routingRule.target().endpoint();
        this.priority = routingRule.priority();
        this.active = routingRule.active();
        this.createdBy = createdBy;
    }

    public String ruleName() {
        return ruleName;
    }

    public String environment() {
        return environment;
    }

    public String targetEndpoint() {
        return targetEndpoint;
    }

    public int priority() {
        return priority;
    }

    public boolean active() {
        return active;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "RoutingRuleCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", environment='" + environment + '\'' +
                ", targetEndpoint='" + targetEndpoint + '\'' +
                ", priority=" + priority +
                ", active=" + active +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
