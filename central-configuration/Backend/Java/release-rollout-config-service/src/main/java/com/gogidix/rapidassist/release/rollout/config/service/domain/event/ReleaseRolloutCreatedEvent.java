package com.gogidix.rapidassist.release.rollout.config.service.domain.event;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

/**
 * Domain event published when a new ReleaseRollout is created.
 * This event can be consumed by other services to react to new rollout configurations.
 */
public class ReleaseRolloutCreatedEvent extends DomainEvent<ReleaseRollout> {

    private final String releaseId;
    private final String rolloutVersion;
    private final ReleaseRollout.RolloutStrategy strategy;
    private final ReleaseRollout.RolloutConfig config;
    private final String environment;
    private final String createdBy;

    public ReleaseRolloutCreatedEvent(ReleaseRollout rollout, String createdBy) {
        super(
            rollout.id(),
            "ReleaseRolloutCreated",
            rollout.tenantId(),
            rollout.recordVersion()
        );
        this.releaseId = rollout.releaseId();
        this.rolloutVersion = rollout.version();
        this.strategy = rollout.strategy();
        this.config = rollout.config();
        this.environment = rollout.environment();
        this.createdBy = createdBy;
    }

    public String releaseId() {
        return releaseId;
    }

    public String rolloutVersion() {
        return rolloutVersion;
    }

    public ReleaseRollout.RolloutStrategy strategy() {
        return strategy;
    }

    public ReleaseRollout.RolloutConfig config() {
        return config;
    }

    public String environment() {
        return environment;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "ReleaseRolloutCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", releaseId='" + releaseId + '\'' +
                ", rolloutVersion='" + rolloutVersion + '\'' +
                ", strategy=" + strategy +
                ", environment='" + environment + '\'' +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
