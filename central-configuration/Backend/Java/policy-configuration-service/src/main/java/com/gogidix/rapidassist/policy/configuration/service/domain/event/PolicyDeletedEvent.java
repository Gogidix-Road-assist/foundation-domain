package com.gogidix.rapidassist.policy.configuration.service.domain.event;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.Map;

/**
 * Domain event published when a Policy is deleted.
 * This event can be consumed by other services to react to policy deletion.
 */
public class PolicyDeletedEvent extends DomainEvent<Policy> {

    private final String policyKey;
    private final String name;
    private final Policy.PolicyType type;
    private final String environment;
    private final Map<String, Object> rules;
    private final String deletedBy;
    private final String reason;

    public PolicyDeletedEvent(Policy policy, String deletedBy, String reason) {
        super(
            policy.id(),
            "PolicyDeleted",
            policy.tenantId(),
            policy.version()
        );
        this.policyKey = policy.policyKey();
        this.name = policy.name();
        this.type = policy.type();
        this.environment = policy.environment();
        this.rules = policy.rules();
        this.deletedBy = deletedBy;
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

    public Map<String, Object> rules() {
        return rules;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "PolicyDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", policyKey='" + policyKey + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
