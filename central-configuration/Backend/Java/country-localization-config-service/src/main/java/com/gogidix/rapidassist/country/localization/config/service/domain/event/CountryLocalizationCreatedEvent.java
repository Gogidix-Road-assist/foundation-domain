package com.gogidix.rapidassist.country.localization.config.service.domain.event;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

/**
 * Domain event published when a new CountryLocalization is created.
 * This event can be consumed by other services to react to new country localizations.
 */
public class CountryLocalizationCreatedEvent extends DomainEvent<CountryLocalization> {

    private final String countryCode;
    private final String countryName;
    private final String createdBy;

    public CountryLocalizationCreatedEvent(CountryLocalization localization, String createdBy) {
        super(
            localization.id(),
            "CountryLocalizationCreated",
            localization.tenantId(),
            localization.version()
        );
        this.countryCode = localization.countryCode();
        this.countryName = localization.countryName();
        this.createdBy = createdBy;
    }

    public String countryCode() {
        return countryCode;
    }

    public String countryName() {
        return countryName;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "CountryLocalizationCreatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
