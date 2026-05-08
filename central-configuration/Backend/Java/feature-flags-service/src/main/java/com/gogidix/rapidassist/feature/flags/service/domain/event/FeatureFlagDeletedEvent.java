package com.gogidix.rapidassist.feature.flags.service.domain.event;

/**
 * Domain event published when a FeatureFlag is deleted.
 * This event allows other services to clean up or react to feature flag deletions.
 */
public class FeatureFlagDeletedEvent extends DomainEvent<Object> {

    private final String key;
    private final String name;
    private final String environment;
    private final Integer versionAtDeletion;
    private final boolean enabledBeforeDeletion;
    private final String deletedBy;
    private final String reason;

    public FeatureFlagDeletedEvent(String featureFlagId,
                                   String tenantId,
                                   String key,
                                   String name,
                                   String environment,
                                   Integer versionAtDeletion,
                                   boolean enabledBeforeDeletion,
                                   String deletedBy,
                                   String reason) {
        super(
            featureFlagId,
            "FeatureFlagDeleted",
            tenantId,
            versionAtDeletion
        );
        this.key = key;
        this.name = name;
        this.environment = environment;
        this.versionAtDeletion = versionAtDeletion;
        this.enabledBeforeDeletion = enabledBeforeDeletion;
        this.deletedBy = deletedBy;
        this.reason = reason;
    }

    public String key() {
        return key;
    }

    public String name() {
        return name;
    }

    public String environment() {
        return environment;
    }

    public Integer versionAtDeletion() {
        return versionAtDeletion;
    }

    public boolean enabledBeforeDeletion() {
        return enabledBeforeDeletion;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "FeatureFlagDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", environment='" + environment + '\'' +
                ", versionAtDeletion=" + versionAtDeletion +
                ", enabledBeforeDeletion=" + enabledBeforeDeletion +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredAt=" + occurredAt() +
                '}';
    }
}
