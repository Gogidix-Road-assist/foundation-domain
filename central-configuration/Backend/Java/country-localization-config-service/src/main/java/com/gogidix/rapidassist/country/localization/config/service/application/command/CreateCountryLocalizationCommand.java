package com.gogidix.rapidassist.country.localization.config.service.application.command;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

import java.util.List;

/**
 * Command object for creating a new CountryLocalization.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new localization entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateCountryLocalizationCommand(
    String tenantId,
    String countryCode,
    String countryName,
    CountryLocalization.LocaleConfig locale,
    CountryLocalization.CurrencyConfig currency,
    CountryLocalization.DateTimeConfig dateTime,
    CountryLocalization.AddressFormat addressFormat,
    CountryLocalization.PhoneFormat phoneFormat,
    CountryLocalization.EmergencyServices emergencyServices,
    CountryLocalization.LegalRequirements legalRequirements,
    CountryLocalization.MeasurementSystem measurementSystem,
    boolean active,
    String createdBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && countryCode != null && !countryCode.isBlank()
            && countryName != null && !countryName.isBlank()
            && locale != null
            && currency != null
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateCountryLocalizationCommand with defaults
     */
    public CreateCountryLocalizationCommand withDefaults() {
        return new CreateCountryLocalizationCommand(
            this.tenantId,
            this.countryCode.toUpperCase(),
            this.countryName,
            this.locale,
            this.currency,
            this.dateTime != null ? this.dateTime : CountryLocalization.DateTimeConfig.of("UTC"),
            this.addressFormat != null ? this.addressFormat :
                CountryLocalization.AddressFormat.of("{street}\\n{city}\\n{postcode}", List.of("street", "city", "postcode")),
            this.phoneFormat != null ? this.phoneFormat : CountryLocalization.PhoneFormat.of("+000"),
            this.emergencyServices != null ? this.emergencyServices :
                CountryLocalization.EmergencyServices.of("000", "000"),
            this.legalRequirements != null ? this.legalRequirements : CountryLocalization.LegalRequirements.standard(),
            this.measurementSystem != null ? this.measurementSystem : CountryLocalization.MeasurementSystem.METRIC,
            this.active,
            this.createdBy,
            this.reason != null ? this.reason : "Initial country localization creation"
        );
    }
}
