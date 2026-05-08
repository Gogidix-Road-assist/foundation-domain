package com.gogidix.rapidassist.release.rollout.config.service.domain.event;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

/**
 * Domain event published when a ReleaseRollout is updated.
 * This event can be consumed by other services to react to rollout configuration changes.
 */
public class ReleaseRolloutUpdatedEvent extends DomainEvent<ReleaseRollout> {

    private final String releaseId;
    private final String rolloutVersion;
    private final ReleaseRollout.RolloutStatus oldStatus;
    private final ReleaseRollout.RolloutStatus newStatus;
    private final String environment;
    private final String updatedBy;
    private final String reason;

    public ReleaseRolloutUpdatedEvent(
            ReleaseRollout oldRollout,
            ReleaseRollout newRollout,
            String updatedBy,
            String reason) {
        super(
            newRollout.id(),
            "ReleaseRolloutUpdated",
            newRollout.tenantId(),
            newRollout.recordVersion()
        );
        this.releaseId = newRollout.releaseId();
        this.rolloutVersion = newRollout.version();
        this.oldStatus = oldRollout.status();
        this.newStatus = newRollout.status();
        this.environment = newRollout.environment();
        this.updatedBy = updatedBy;
        this.reason = reason;
    }

    public String releaseId() {
        return releaseId;
    }

    public String rolloutVersion() {
        return rolloutVersion;
    }

    public ReleaseRollout.RolloutStatus oldStatus() {
        return oldStatus;
    }

    public ReleaseRollout.RolloutStatus newStatus() {
        return newStatus;
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

    @Override
    public String toString() {
        return "ReleaseRolloutUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", releaseId='" + releaseId + '\'' +
                ", rolloutVersion='" + rolloutVersion + '\'' +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
