package com.gogidix.rapidassist.country.localization.config.service.domain.port.in;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for localization command operations
 */
public interface LocalizationCommand {

    // Country localization commands
    CompletableFuture<CountryLocalization> createCountryLocalization(CreateCountryCommand command);
    CompletableFuture<Optional<CountryLocalization>> updateCountryLocalization(String countryCode, UpdateCountryCommand command);
    CompletableFuture<Optional<CountryLocalization>> activateCountry(String countryCode, String updatedBy);
    CompletableFuture<Optional<CountryLocalization>> deactivateCountry(String countryCode, String updatedBy);
    CompletableFuture<Boolean> deleteCountryLocalization(String countryCode);

    // Resource translation commands
    CompletableFuture<LocalizedResource> createResource(CreateResourceCommand command);
    CompletableFuture<Optional<LocalizedResource>> updateResource(String resourceId, UpdateResourceCommand command);
    CompletableFuture<Optional<LocalizedResource>> addTranslation(String resourceId, String locale, String value, String updatedBy);
    CompletableFuture<Optional<LocalizedResource>> removeTranslation(String resourceId, String locale, String updatedBy);
    CompletableFuture<Boolean> deleteResource(String resourceId);

    // Bulk operations
    CompletableFuture<List<LocalizedResource>> bulkImportTranslations(String countryCode, Map<String, Map<String, String>> translations);

    // Command records
    record CreateCountryCommand(
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
        String createdBy
    ) {}

    record UpdateCountryCommand(
        String countryName,
        CountryLocalization.LocaleConfig locale,
        CountryLocalization.CurrencyConfig currency,
        CountryLocalization.DateTimeConfig dateTime,
        CountryLocalization.AddressFormat addressFormat,
        CountryLocalization.PhoneFormat phoneFormat,
        CountryLocalization.EmergencyServices emergencyServices,
        CountryLocalization.LegalRequirements legalRequirements,
        CountryLocalization.MeasurementSystem measurementSystem,
        String updatedBy
    ) {}

    record CreateResourceCommand(
        String resourceKey,
        String countryCode,
        String resourceType,
        Map<String, String> translations,
        String defaultValue,
        String context,
        String createdBy
    ) {}

    record UpdateResourceCommand(
        String resourceKey,
        String resourceType,
        String context,
        String updatedBy
    ) {}
}
