package com.gogidix.rapidassist.dynamic.routing.config.service.domain.event;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.util.Set;

/**
 * Domain event published when a Routing Rule is updated.
 * This event can be consumed by other services to react to routing rule changes.
 */
public class RoutingRuleUpdatedEvent extends DomainEvent<RoutingRule> {

    private final String ruleName;
    private final String environment;
    private final String updatedBy;
    private final String reason;
    private final Set<String> changedFields;

    public RoutingRuleUpdatedEvent(
            RoutingRule oldRule,
            RoutingRule newRule,
            String updatedBy,
            String reason,
            Set<String> changedFields) {
        super(
            newRule.id(),
            "RoutingRuleUpdated",
            newRule.tenantId(),
            newRule.version()
        );
        this.ruleName = newRule.ruleName();
        this.environment = newRule.environment();
        this.updatedBy = updatedBy;
        this.reason = reason;
        this.changedFields = Set.copyOf(changedFields);
    }

    public String ruleName() {
        return ruleName;
    }

    public String environment() {
        return environment;
    }

    public String updatedBy() {
        return updatedBy;
    }

    public String reason() {
        return reason;
    }

    public Set<String> changedFields() {
        return changedFields;
    }

    @Override
    public String toString() {
        return "RoutingRuleUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", environment='" + environment + '\'' +
                ", changedFields=" + changedFields +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
