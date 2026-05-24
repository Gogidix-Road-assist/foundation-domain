package com.gogidix.rapidassist.country.localization.config.service.domain.port.in;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for localization query operations
 */
public interface LocalizationQuery {

    // Country localization queries
    CompletableFuture<Optional<CountryLocalization>> getByCountryCode(String countryCode);
    CompletableFuture<List<CountryLocalization>> getAllActiveCountries();
    CompletableFuture<List<CountryLocalization>> getAllCountries();
    CompletableFuture<List<CountryLocalization>> getCountriesByMeasurementSystem(CountryLocalization.MeasurementSystem system);

    // Translation queries
    CompletableFuture<String> getTranslation(String resourceKey, String locale, String countryCode);
    CompletableFuture<Map<String, String>> getTranslations(String resourceKey, String countryCode);
    CompletableFuture<List<LocalizedResource>> getResourcesByCountry(String countryCode);
    CompletableFuture<List<LocalizedResource>> getResourcesByType(String resourceType);
    CompletableFuture<Optional<LocalizedResource>> getResource(String resourceKey, String countryCode);

    // Locale/currency helpers
    CompletableFuture<String> getCurrencySymbol(String countryCode);
    CompletableFuture<String> getDateFormat(String countryCode);
    CompletableFuture<String> getTimeFormat(String countryCode);
    CompletableFuture<String> getPhoneFormat(String countryCode);
    CompletableFuture<String> getEmergencyNumber(String countryCode);

    // Validation
    CompletableFuture<Boolean> isValidCountryCode(String countryCode);
    CompletableFuture<Boolean> isValidLocale(String locale);
}
