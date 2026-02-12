package com.gogidix.rapidassist.country.localization.config.service.domain.event;

/**
 * Domain event published when a CountryLocalization is deleted.
 * This event contains the deleted localization data for audit trail.
 */
public class CountryLocalizationDeletedEvent extends DomainEvent<Object> {

    private final String id;
    private final String countryCode;
    private final String countryName;
    private final Integer version;
    private final String deletedBy;
    private final String reason;

    public CountryLocalizationDeletedEvent(
            String id,
            String tenantId,
            String countryCode,
            String countryName,
            Integer version,
            String deletedBy,
            String reason) {
        super(id, "CountryLocalizationDeleted", tenantId, version);
        this.id = id;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.version = version;
        this.deletedBy = deletedBy;
        this.reason = reason;
    }

    public String id() {
        return id;
    }

    public String countryCode() {
        return countryCode;
    }

    public String countryName() {
        return countryName;
    }

    public Integer version() {
        return version;
    }

    public String deletedBy() {
        return deletedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "CountryLocalizationDeletedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", tenantId='" + tenantId() + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", version=" + version +
                ", occurredAt=" + occurredAt() +
                ", deletedBy='" + deletedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
