package com.gogidix.rapidassist.feature.flags.service.domain.event;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

/**
 * Domain event published when an existing FeatureFlag is updated.
 * This event contains both the previous and new states for audit purposes.
 */
public class FeatureFlagUpdatedEvent extends DomainEvent<FeatureFlag> {

    private final String key;
    private final String name;
    private final boolean previousEnabled;
    private final boolean newEnabled;
    private final String environment;
    private final FeatureFlag.FlagStatus previousStatus;
    private final FeatureFlag.FlagStatus newStatus;
    private final Integer previousVersion;
    private final Integer newVersion;
    private final String updatedBy;
    private final String reason;

    public FeatureFlagUpdatedEvent(FeatureFlag previousFeatureFlag,
                                    FeatureFlag newFeatureFlag,
                                    String updatedBy,
                                    String reason) {
        super(
            newFeatureFlag.id(),
            "FeatureFlagUpdated",
            newFeatureFlag.tenantId(),
            newFeatureFlag.version()
        );
        this.key = newFeatureFlag.key();
        this.name = newFeatureFlag.name();
        this.previousEnabled = previousFeatureFlag.enabled();
        this.newEnabled = newFeatureFlag.enabled();
        this.environment = newFeatureFlag.environment();
        this.previousStatus = previousFeatureFlag.status();
        this.newStatus = newFeatureFlag.status();
        this.previousVersion = previousFeatureFlag.version();
        this.newVersion = newFeatureFlag.version();
        this.updatedBy = updatedBy;
        this.reason = reason;
    }

    public String key() {
        return key;
    }

    public String name() {
        return name;
    }

    public boolean previousEnabled() {
        return previousEnabled;
    }

    public boolean newEnabled() {
        return newEnabled;
    }

    public String environment() {
        return environment;
    }

    public FeatureFlag.FlagStatus previousStatus() {
        return previousStatus;
    }

    public FeatureFlag.FlagStatus newStatus() {
        return newStatus;
    }

    public Integer previousVersion() {
        return previousVersion;
    }

    public Integer newVersion() {
        return newVersion;
    }

    public String updatedBy() {
        return updatedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "FeatureFlagUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", environment='" + environment + '\'' +
                ", previousEnabled=" + previousEnabled +
                ", newEnabled=" + newEnabled +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", previousVersion=" + previousVersion +
                ", newVersion=" + newVersion +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredAt=" + occurredAt() +
                '}';
    }
}
