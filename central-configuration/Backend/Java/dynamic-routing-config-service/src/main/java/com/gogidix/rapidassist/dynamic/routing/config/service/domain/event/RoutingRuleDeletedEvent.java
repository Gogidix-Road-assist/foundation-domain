package com.gogidix.rapidassist.dynamic.routing.config.service.domain.event;

/**
 * Domain event published when a Routing Rule is deleted.
 * This event can be consumed by other services to react to routing rule deletions.
 */
public class RoutingRuleDeletedEvent extends DomainEvent<Void> {

    private final String ruleName;
    private final String environment;
    private final int priority;
    private final String deletedBy;
    private final String reason;

    public RoutingRuleDeletedEvent(
            String ruleId,
            String tenantId,
            String ruleName,
            String environment,
            int priority,
            Integer version,
            String deletedBy,
            String reason) {
        super(
            ruleId,
            "RoutingRuleDeleted",
            tenantId,
            version
        );
        this.ruleName = ruleName;
        this.environment = environment;
        this.priority = priority;
        this.deletedBy = deletedBy;
        this.reason = reason;
    }

    public String ruleName() {
        return ruleName;
    }

    public String environment() {
        return environment;
    }

    public int priority() {
        return priority;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RoutingRuleDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", environment='" + environment + '\'' +
                ", priority=" + priority +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
