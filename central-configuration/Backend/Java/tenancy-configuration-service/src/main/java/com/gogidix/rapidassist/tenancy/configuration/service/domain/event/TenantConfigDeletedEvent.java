package com.gogidix.rapidassist.tenancy.configuration.service.domain.event;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

/**
 * Domain event published when a Tenant Configuration is deleted.
 * This event can be consumed by other services to react to configuration deletions.
 */
public class TenantConfigDeletedEvent extends DomainEvent<TenantConfig> {

    private final String name;
    private final String domain;
    private final String environment;
    private final Object configSnapshot;
    private final String deletedBy;
    private final String reason;

    public TenantConfigDeletedEvent(String id, String tenantId, String name, String domain,
                                    String environment, Integer version, Object configSnapshot,
                                    String deletedBy, String reason) {
        super(
            id,
            "TenantConfigDeleted",
            tenantId,
            version
        );
        this.name = name;
        this.domain = domain;
        this.environment = environment;
        this.configSnapshot = configSnapshot;
        this.deletedBy = deletedBy;
        this.reason = reason;
    }

    public String name() {
        return name;
    }

    public String domain() {
        return domain;
    }

    public String environment() {
        return environment;
    }

    public Object configSnapshot() {
        return configSnapshot;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "TenantConfigDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", environment='" + environment + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
