package com.gogidix.rapidassist.country.localization.config.service.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

/**
 * Domain event published when a CountryLocalization is updated.
 * This event contains the old and new values for audit purposes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryLocalizationUpdatedEvent extends DomainEvent<CountryLocalization> {

    @JsonProperty("countryCode")
    private final String countryCode;

    @JsonProperty("oldValue")
    private final CountryLocalization oldValue;

    @JsonProperty("newValue")
    private final CountryLocalization newValue;

    @JsonProperty("updatedBy")
    private final String updatedBy;

    @JsonProperty("reason")
    private final String reason;

    public CountryLocalizationUpdatedEvent(
            CountryLocalization oldValue,
            CountryLocalization newValue,
            String updatedBy,
            String reason) {
        super(
            newValue.id(),
            "CountryLocalizationUpdated",
            newValue.tenantId(),
            newValue.version()
        );
        this.countryCode = newValue.countryCode();
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.updatedBy = updatedBy;
        this.reason = reason;
    }

    public String countryCode() {
        return countryCode;
    }

    public CountryLocalization oldValue() {
        return oldValue;
    }

    public CountryLocalization newValue() {
        return newValue;
    }

    public String updatedBy() {
        return updatedBy;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "CountryLocalizationUpdatedEvent{" +
                "eventId='" + eventId() + '\'' +
                ", aggregateId='" + aggregateId() + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", version=" + version() +
                ", occurredAt=" + occurredAt() +
                ", updatedBy='" + updatedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
