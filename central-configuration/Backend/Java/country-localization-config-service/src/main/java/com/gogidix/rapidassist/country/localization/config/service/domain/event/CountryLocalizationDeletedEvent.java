package com.gogidix.rapidassist.country.localization.config.service.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Domain event published when a CountryLocalization is deleted.
 * This event contains the deleted localization data for audit trail.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryLocalizationDeletedEvent extends DomainEvent<Object> {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("countryCode")
    private final String countryCode;

    @JsonProperty("countryName")
    private final String countryName;

    @JsonProperty("version")
    private final Integer version;

    @JsonProperty("deletedBy")
    private final String deletedBy;

    @JsonProperty("reason")
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
