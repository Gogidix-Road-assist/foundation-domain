package com.gogidix.rapidassist.release.rollout.config.service.domain.event;

/**
 * Domain event published when a ReleaseRollout is deleted.
 * This event can be consumed by other services to react to rollout configuration deletions.
 */
public class ReleaseRolloutDeletedEvent extends DomainEvent<Object> {

    private final String releaseId;
    private final String rolloutVersion;
    private final String environment;
    private final String deletedBy;
    private final String reason;

    public ReleaseRolloutDeletedEvent(
            String id,
            String tenantId,
            String releaseId,
            String version,
            String environment,
            Integer recordVersion,
            String deletedBy,
            String reason) {
        super(
            id,
            "ReleaseRolloutDeleted",
            tenantId,
            recordVersion
        );
        this.releaseId = releaseId;
        this.rolloutVersion = version;
        this.environment = environment;
        this.deletedBy = deletedBy;
        this.reason = reason;
    }

    public String releaseId() {
        return releaseId;
    }

    public String rolloutVersion() {
        return rolloutVersion;
    }

    public String environment() {
        return environment;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ReleaseRolloutDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", releaseId='" + releaseId + '\'' +
                ", rolloutVersion='" + rolloutVersion + '\'' +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
