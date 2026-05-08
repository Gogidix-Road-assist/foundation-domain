package com.gogidix.rapidassist.rate.limit.policy.service.domain.event;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

/**
 * Domain event published when an existing RateLimitPolicy is updated.
 * This event can be consumed by other services to react to policy changes.
 */
public class RateLimitPolicyUpdatedEvent extends DomainEvent<RateLimitPolicy> {

    private final RateLimitPolicy oldPolicy;
    private final RateLimitPolicy newPolicy;
    private final String updatedBy;
    private final String reason;

    public RateLimitPolicyUpdatedEvent(RateLimitPolicy oldPolicy, RateLimitPolicy newPolicy, String updatedBy, String reason) {
        super(
            newPolicy.id(),
            "RateLimitPolicyUpdated",
            newPolicy.tenantId(),
            newPolicy.version()
        );
        this.oldPolicy = oldPolicy;
        this.newPolicy = newPolicy;
        this.updatedBy = updatedBy;
        this.reason = reason;
    }

    public RateLimitPolicy oldPolicy() {
        return oldPolicy;
    }

    public RateLimitPolicy newPolicy() {
        return newPolicy;
    }

    public String updatedBy() {
        return updatedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RateLimitPolicyUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", policyKey='" + newPolicy.policyKey() + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
