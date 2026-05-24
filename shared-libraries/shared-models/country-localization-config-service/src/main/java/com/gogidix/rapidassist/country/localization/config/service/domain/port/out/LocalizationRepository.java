package com.gogidix.rapidassist.country.localization.config.service.domain.port.out;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Output port for localization persistence operations
 */
public interface LocalizationRepository {

    // Country localization persistence
    CompletableFuture<CountryLocalization> saveCountry(CountryLocalization country);
    CompletableFuture<Optional<CountryLocalization>> findByCountryCode(String countryCode);
    CompletableFuture<List<CountryLocalization>> findAllCountries();
    CompletableFuture<List<CountryLocalization>> findActiveCountries();
    CompletableFuture<List<CountryLocalization>> findByMeasurementSystem(CountryLocalization.MeasurementSystem system);
    CompletableFuture<Boolean> deleteCountry(String countryCode);

    // Resource persistence
    CompletableFuture<LocalizedResource> saveResource(LocalizedResource resource);
    CompletableFuture<Optional<LocalizedResource>> findResourceByKey(String resourceKey, String countryCode);
    CompletableFuture<List<LocalizedResource>> findResourcesByCountry(String countryCode);
    CompletableFuture<List<LocalizedResource>> findResourcesByType(String resourceType);
    CompletableFuture<List<LocalizedResource>> findAllResources();
    CompletableFuture<Boolean> deleteResource(String resourceId);

    // Cache operations
    CompletableFuture<Void> cacheCountry(CountryLocalization country);
    CompletableFuture<Optional<CountryLocalization>> getCachedCountry(String countryCode);
    CompletableFuture<Void> cacheResource(LocalizedResource resource);
    CompletableFuture<Optional<LocalizedResource>> getCachedResource(String resourceKey, String countryCode);
    CompletableFuture<Void> evictCountry(String countryCode);
    CompletableFuture<Void> evictResource(String resourceKey, String countryCode);
    CompletableFuture<Void> evictAllCountryResources(String countryCode);
}
