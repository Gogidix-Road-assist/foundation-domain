package com.gogidix.rapidassist.policy.configuration.service.domain.event;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

/**
 * Domain event published when a new Policy is created.
 * This event can be consumed by other services to react to new policies.
 */
public class PolicyCreatedEvent extends DomainEvent<Policy> {

    private final String policyKey;
    private final String name;
    private final Policy.PolicyType type;
    private final String environment;
    private final String createdBy;

    public PolicyCreatedEvent(Policy policy, String createdBy) {
        super(
            policy.id(),
            "PolicyCreated",
            policy.tenantId(),
            policy.version()
        );
        this.policyKey = policy.policyKey();
        this.name = policy.name();
        this.type = policy.type();
        this.environment = policy.environment();
        this.createdBy = createdBy;
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

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "PolicyCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", policyKey='" + policyKey + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
