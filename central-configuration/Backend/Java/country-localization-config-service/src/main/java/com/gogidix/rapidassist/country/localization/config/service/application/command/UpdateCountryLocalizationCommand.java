package com.gogidix.rapidassist.country.localization.config.service.application.command;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

/**
 * Command object for updating an existing CountryLocalization.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to update a localization entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record UpdateCountryLocalizationCommand(
    String id,
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
    String updatedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank()
            && countryName != null && !countryName.isBlank()
            && locale != null
            && currency != null
            && updatedBy != null && !updatedBy.isBlank();
    }
}
