package com.gogidix.rapidassist.tenancy.configuration.service.domain.event;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

/**
 * Domain event published when a Tenant Configuration is updated.
 * This event can be consumed by other services to react to configuration changes.
 */
public class TenantConfigUpdatedEvent extends DomainEvent<TenantConfig> {

    private final String name;
    private final String domain;
    private final String environment;
    private final boolean active;
    private final String updatedBy;
    private final String reason;

    public TenantConfigUpdatedEvent(TenantConfig oldConfig, TenantConfig newConfig, String updatedBy, String reason) {
        super(
            newConfig.id(),
            "TenantConfigUpdated",
            newConfig.tenantId(),
            newConfig.version()
        );
        this.name = newConfig.name();
        this.domain = newConfig.domain();
        this.environment = newConfig.environment();
        this.active = newConfig.active();
        this.updatedBy = updatedBy;
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

    public boolean active() {
        return active;
    }

    public String updatedBy() {
        return updatedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "TenantConfigUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", environment='" + environment + '\'' +
                ", active=" + active +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
