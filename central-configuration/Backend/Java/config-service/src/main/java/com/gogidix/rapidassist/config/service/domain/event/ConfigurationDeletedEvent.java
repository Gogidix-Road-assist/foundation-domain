package com.gogidix.rapidassist.config.service.domain.event;

/**
 * Domain event published when a Configuration is deleted.
 * This event allows other services to clean up or react to configuration deletions.
 */
public class ConfigurationDeletedEvent extends DomainEvent<Object> {

    private final String configKey;
    private final String environment;
    private final String namespace;
    private final Integer versionAtDeletion;
    private final Object valueBeforeDeletion;
    private final String deletedBy;
    private final String reason;

    public ConfigurationDeletedEvent(String configurationId,
                                     String tenantId,
                                     String configKey,
                                     String environment,
                                     String namespace,
                                     Integer versionAtDeletion,
                                     Object valueBeforeDeletion,
                                     String deletedBy,
                                     String reason) {
        super(
            configurationId,
            "ConfigurationDeleted",
            tenantId,
            versionAtDeletion
        );
        this.configKey = configKey;
        this.environment = environment;
        this.namespace = namespace;
        this.versionAtDeletion = versionAtDeletion;
        this.valueBeforeDeletion = valueBeforeDeletion;
        this.deletedBy = deletedBy;
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

    public Integer versionAtDeletion() {
        return versionAtDeletion;
    }

    public Object valueBeforeDeletion() {
        return valueBeforeDeletion;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ConfigurationDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", configKey='" + configKey + '\'' +
                ", environment='" + environment + '\'' +
                ", namespace='" + namespace + '\'' +
                ", versionAtDeletion=" + versionAtDeletion +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredAt=" + occurredAt() +
                '}';
    }
}
