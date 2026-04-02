package com.gogidix.rapidassist.rate.limit.policy.service.domain.event;

/**
 * Domain event published when a RateLimitPolicy is deleted.
 * This event can be consumed by other services to react to policy deletions.
 */
public class RateLimitPolicyDeletedEvent extends DomainEvent<Object> {

    private final String policyKey;
    private final String name;
    private final String environment;
    private final Object deletedPolicy;
    private final String deletedBy;
    private final String reason;

    public RateLimitPolicyDeletedEvent(
            String id,
            String tenantId,
            String policyKey,
            String name,
            String environment,
            Integer version,
            Object deletedPolicy,
            String deletedBy,
            String reason) {
        super(
            id,
            "RateLimitPolicyDeleted",
            tenantId,
            version
        );
        this.policyKey = policyKey;
        this.name = name;
        this.environment = environment;
        this.deletedPolicy = deletedPolicy;
        this.deletedBy = deletedBy;
        this.reason = reason;
    }

    public String policyKey() {
        return policyKey;
    }

    public String name() {
        return name;
    }

    public String environment() {
        return environment;
    }

    public Object deletedPolicy() {
        return deletedPolicy;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RateLimitPolicyDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", policyKey='" + policyKey + '\'' +
                ", name='" + name + '\'' +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
