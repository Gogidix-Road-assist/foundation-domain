package com.gogidix.rapidassist.feature.flags.service.domain.event;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

/**
 * Domain event published when a new FeatureFlag is created.
 * This event can be consumed by other services to react to new feature flags.
 */
public class FeatureFlagCreatedEvent extends DomainEvent<FeatureFlag> {

    private final String key;
    private final String name;
    private final boolean enabled;
    private final String environment;
    private final FeatureFlag.FlagType type;
    private final FeatureFlag.RolloutStrategy rolloutStrategy;
    private final String createdBy;

    public FeatureFlagCreatedEvent(FeatureFlag featureFlag, String createdBy) {
        super(
            featureFlag.id(),
            "FeatureFlagCreated",
            featureFlag.tenantId(),
            featureFlag.version()
        );
        this.key = featureFlag.key();
        this.name = featureFlag.name();
        this.enabled = featureFlag.enabled();
        this.environment = featureFlag.environment();
        this.type = featureFlag.type();
        this.rolloutStrategy = featureFlag.rolloutStrategy();
        this.createdBy = createdBy;
    }

    public String key() {
        return key;
    }

    public String name() {
        return name;
    }

    public boolean enabled() {
        return enabled;
    }

    public String environment() {
        return environment;
    }

    public FeatureFlag.FlagType type() {
        return type;
    }

    public FeatureFlag.RolloutStrategy rolloutStrategy() {
        return rolloutStrategy;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "FeatureFlagCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", environment='" + environment + '\'' +
                ", type=" + type +
                ", rolloutStrategy=" + rolloutStrategy +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
