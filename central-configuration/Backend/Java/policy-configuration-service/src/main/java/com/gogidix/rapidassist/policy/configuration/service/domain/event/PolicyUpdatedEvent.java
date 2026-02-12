package com.gogidix.rapidassist.policy.configuration.service.domain.event;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.Map;

/**
 * Domain event published when a Policy is updated.
 * This event can be consumed by other services to react to policy changes.
 */
public class PolicyUpdatedEvent extends DomainEvent<Policy> {

    private final String policyKey;
    private final String name;
    private final Policy.PolicyType type;
    private final String environment;
    private final Map<String, Object> oldRules;
    private final Map<String, Object> newRules;
    private final Policy.PolicyStatus oldStatus;
    private final Policy.PolicyStatus newStatus;
    private final String updatedBy;
    private final String reason;

    public PolicyUpdatedEvent(Policy oldPolicy, Policy newPolicy, String updatedBy, String reason) {
        super(
            newPolicy.id(),
            "PolicyUpdated",
            newPolicy.tenantId(),
            newPolicy.version()
        );
        this.policyKey = newPolicy.policyKey();
        this.name = newPolicy.name();
        this.type = newPolicy.type();
        this.environment = newPolicy.environment();
        this.oldRules = oldPolicy.rules();
        this.newRules = newPolicy.rules();
        this.oldStatus = oldPolicy.status();
        this.newStatus = newPolicy.status();
        this.updatedBy = updatedBy;
        this.reason = reason;
    }

    public String policyKey() {
        return policyKey;
    }

    public String name() {
        return name;
    }

    public Policy.PolicyType type() {
        return type;
    }

    public String environment() {
        return environment;
    }

    public Map<String, Object> oldRules() {
        return oldRules;
    }

    public Map<String, Object> newRules() {
        return newRules;
    }

    public Policy.PolicyStatus oldStatus() {
        return oldStatus;
    }

    public Policy.PolicyStatus newStatus() {
        return newStatus;
    }

    public String updatedBy() {
        return updatedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "PolicyUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", policyKey='" + policyKey + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", environment='" + environment + '\'' +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
