package com.gogidix.rapidassist.config.service.domain.event;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;

/**
 * Domain event published when an existing Configuration is updated.
 * This event contains both the previous and new values for audit purposes.
 */
public class ConfigurationUpdatedEvent extends DomainEvent<Configuration> {

    private final String configKey;
    private final String environment;
    private final String namespace;
    private final Object previousValue;
    private final Object newValue;
    private final Integer previousVersion;
    private final Integer newVersion;
    private final String updatedBy;
    private final String reason;

    public ConfigurationUpdatedEvent(Configuration previousConfiguration,
                                     Configuration newConfiguration,
                                     String updatedBy,
                                     String reason) {
        super(
            newConfiguration.id(),
            "ConfigurationUpdated",
            newConfiguration.tenantId(),
            newConfiguration.version()
        );
        this.configKey = newConfiguration.configKey();
        this.environment = newConfiguration.environment();
        this.namespace = newConfiguration.namespace();
        this.previousValue = previousConfiguration.value();
        this.newValue = newConfiguration.value();
        this.previousVersion = previousConfiguration.version();
        this.newVersion = newConfiguration.version();
        this.updatedBy = updatedBy;
        this.reason = reason;
    }

    public String configKey() {
        return configKey;
    }

    public String environment() {
        return environment;
    }

    public String namespace() {
        return namespace;
    }

    public Object previousValue() {
        return previousValue;
    }

    public Object newValue() {
        return newValue;
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
        return "ConfigurationUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", configKey='" + configKey + '\'' +
                ", environment='" + environment + '\'' +
                ", namespace='" + namespace + '\'' +
                ", previousVersion=" + previousVersion +
                ", newVersion=" + newVersion +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredAt=" + occurredAt() +
                '}';
    }
}
