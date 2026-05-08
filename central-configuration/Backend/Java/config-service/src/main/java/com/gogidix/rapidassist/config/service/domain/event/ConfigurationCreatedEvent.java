package com.gogidix.rapidassist.config.service.domain.event;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;

/**
 * Domain event published when a new Configuration is created.
 * This event can be consumed by other services to react to new configurations.
 */
public class ConfigurationCreatedEvent extends DomainEvent<Configuration> {

    private final String configKey;
    private final String environment;
    private final String namespace;
    private final Object value;
    private final String createdBy;

    public ConfigurationCreatedEvent(Configuration configuration, String createdBy) {
        super(
            configuration.id(),
            "ConfigurationCreated",
            configuration.tenantId(),
            configuration.version()
        );
        this.configKey = configuration.configKey();
        this.environment = configuration.environment();
        this.namespace = configuration.namespace();
        this.value = configuration.value();
        this.createdBy = createdBy;
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

    public Object value() {
        return value;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "ConfigurationCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", configKey='" + configKey + '\'' +
                ", environment='" + environment + '\'' +
                ", namespace='" + namespace + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
