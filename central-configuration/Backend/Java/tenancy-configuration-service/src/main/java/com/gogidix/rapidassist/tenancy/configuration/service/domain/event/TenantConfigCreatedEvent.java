package com.gogidix.rapidassist.tenancy.configuration.service.domain.event;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

/**
 * Domain event published when a new Tenant Configuration is created.
 * This event can be consumed by other services to react to new tenant configurations.
 */
public class TenantConfigCreatedEvent extends DomainEvent<TenantConfig> {

    private final String name;
    private final String domain;
    private final String environment;
    private final boolean active;
    private final String createdBy;

    public TenantConfigCreatedEvent(TenantConfig tenantConfig, String createdBy) {
        super(
            tenantConfig.id(),
            "TenantConfigCreated",
            tenantConfig.tenantId(),
            tenantConfig.version()
        );
        this.name = tenantConfig.name();
        this.domain = tenantConfig.domain();
        this.environment = tenantConfig.environment();
        this.active = tenantConfig.active();
        this.createdBy = createdBy;
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

    public boolean active() {
        return active;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "TenantConfigCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", environment='" + environment + '\'' +
                ", active=" + active +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
