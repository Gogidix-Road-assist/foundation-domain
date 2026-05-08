package com.gogidix.rapidassist.rate.limit.policy.service.domain.event;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

/**
 * Domain event published when a new RateLimitPolicy is created.
 * This event can be consumed by other services to react to new rate limit policies.
 */
public class RateLimitPolicyCreatedEvent extends DomainEvent<RateLimitPolicy> {

    private final String policyKey;
    private final String name;
    private final RateLimitPolicy.LimitType limitType;
    private final boolean enabled;
    private final String environment;
    private final String createdBy;

    public RateLimitPolicyCreatedEvent(RateLimitPolicy policy, String createdBy) {
        super(
            policy.id(),
            "RateLimitPolicyCreated",
            policy.tenantId(),
            policy.version()
        );
        this.policyKey = policy.policyKey();
        this.name = policy.name();
        this.limitType = policy.limitType();
        this.enabled = policy.enabled();
        this.environment = policy.environment();
        this.createdBy = createdBy;
    }

    public String policyKey() {
        return policyKey;
    }

    public String name() {
        return name;
    }

    public RateLimitPolicy.LimitType limitType() {
        return limitType;
    }

    public boolean enabled() {
        return enabled;
    }

    public String environment() {
        return environment;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "RateLimitPolicyCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", policyKey='" + policyKey + '\'' +
                ", name='" + name + '\'' +
                ", limitType=" + limitType +
                ", enabled=" + enabled +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
