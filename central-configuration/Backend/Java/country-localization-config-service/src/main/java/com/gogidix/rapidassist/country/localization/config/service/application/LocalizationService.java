package com.gogidix.rapidassist.country.localization.config.service.application;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.in.LocalizationCommand;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.in.LocalizationQuery;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.out.LocalizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class LocalizationService implements LocalizationCommand, LocalizationQuery {

    private static final Logger logger = LoggerFactory.getLogger(LocalizationService.class);
    private static final Set<String> VALID_COUNTRY_CODES = Set.of(
        "IE", "GB", "US", "CA", "AU", "DE", "FR", "ES", "IT", "NL", "BE",
        "PL", "CZ", "AT", "CH", "SE", "NO", "DK", "FI", "PT", "GR"
    );

    @Autowired
    private LocalizationRepository repository;

    // Command implementation

    @Override
    public CompletableFuture<CountryLocalization> createCountryLocalization(CreateCountryCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isValidCountryCode(command.countryCode()).join()) {
                throw new IllegalArgumentException("Invalid country code: " + command.countryCode());
            }

            // Check if already exists
            Optional<CountryLocalization> existing = repository.findByCountryCode(command.countryCode()).join();
            if (existing.isPresent()) {
                throw new IllegalStateException("Country localization already exists: " + command.countryCode());
            }

            CountryLocalization country = CountryLocalization.builder()
                .countryCode(command.countryCode())
                .countryName(command.countryName())
                .locale(command.locale())
                .currency(command.currency())
                .dateTime(command.dateTime())
                .addressFormat(command.addressFormat())
                .phoneFormat(command.phoneFormat())
                .emergencyServices(command.emergencyServices())
                .legalRequirements(command.legalRequirements())
                .measurementSystem(command.measurementSystem())
                .active(true)
                .createdBy(command.createdBy())
                .build();

            CountryLocalization saved = repository.saveCountry(country).join();
            repository.cacheCountry(saved).join();

            logger.info("Created country localization for: {}", command.countryCode());
            return saved;
        });
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> updateCountryLocalization(String countryCode, UpdateCountryCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<CountryLocalization> existing = repository.findByCountryCode(countryCode).join();
            if (existing.isEmpty()) {
                return Optional.empty();
            }

            CountryLocalization current = existing.get();

            CountryLocalization updated = CountryLocalization.builder()
                .id(current.id())
                .countryCode(countryCode)
                .countryName(command.countryName())
                .locale(command.locale())
                .currency(command.currency())
                .dateTime(command.dateTime())
                .addressFormat(command.addressFormat())
                .phoneFormat(command.phoneFormat())
                .emergencyServices(command.emergencyServices())
                .legalRequirements(command.legalRequirements())
                .measurementSystem(command.measurementSystem())
                .active(current.active())
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(command.updatedBy())
                .version(current.version() + 1)
                .build();

            CountryLocalization saved = repository.saveCountry(updated).join();
            repository.cacheCountry(saved).join();

            logger.info("Updated country localization for: {}", countryCode);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> activateCountry(String countryCode, String updatedBy) {
        return updateCountryStatus(countryCode, true, updatedBy);
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> deactivateCountry(String countryCode, String updatedBy) {
        return updateCountryStatus(countryCode, false, updatedBy);
    }

    private CompletableFuture<Optional<CountryLocalization>> updateCountryStatus(String countryCode, boolean active, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<CountryLocalization> existing = repository.findByCountryCode(countryCode).join();
            if (existing.isEmpty()) {
                return Optional.empty();
            }

            CountryLocalization current = existing.get();
            if (current.active() == active) {
                return Optional.of(current);
            }

            CountryLocalization updated = CountryLocalization.builder()
                .id(current.id())
                .countryCode(countryCode)
                .countryName(current.countryName())
                .locale(current.locale())
                .currency(current.currency())
                .dateTime(current.dateTime())
                .addressFormat(current.addressFormat())
                .phoneFormat(current.phoneFormat())
                .emergencyServices(current.emergencyServices())
                .legalRequirements(current.legalRequirements())
                .measurementSystem(current.measurementSystem())
                .active(active)
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(updatedBy)
                .version(current.version() + 1)
                .build();

            CountryLocalization saved = repository.saveCountry(updated).join();
            if (!active) {
                repository.evictCountry(countryCode).join();
            } else {
                repository.cacheCountry(saved).join();
            }

            logger.info("{} country localization for: {}", active ? "Activated" : "Deactivated", countryCode);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteCountryLocalization(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            boolean deleted = repository.deleteCountry(countryCode).join();
            repository.evictCountry(countryCode).join();
            repository.evictAllCountryResources(countryCode).join();
            logger.info("Deleted country localization for: {}", countryCode);
            return deleted;
        });
    }

    @Override
    public CompletableFuture<LocalizedResource> createResource(CreateResourceCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            LocalizedResource resource = LocalizedResource.builder()
                .resourceKey(command.resourceKey())
                .countryCode(command.countryCode())
                .resourceType(command.resourceType())
                .translations(command.translations())
                .defaultValue(command.defaultValue())
                .context(command.context())
                .active(true)
                .createdBy(command.createdBy())
                .build();

            LocalizedResource saved = repository.saveResource(resource).join();
            repository.cacheResource(saved).join();

            logger.info("Created resource: {} for country: {}", command.resourceKey(), command.countryCode());
            return saved;
        });
    }

    @Override
    public CompletableFuture<Optional<LocalizedResource>> updateResource(String resourceId, UpdateResourceCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            // For simplicity, assuming resourceId is a composite key: resourceKey:countryCode
            // In real implementation, you'd fetch by ID
            Optional<LocalizedResource> existing = repository.findResourceByKey(command.resourceKey(), "IE").join();
            if (existing.isEmpty()) {
                return Optional.empty();
            }

            LocalizedResource current = existing.get();

            LocalizedResource updated = LocalizedResource.builder()
                .id(current.id())
                .resourceKey(command.resourceKey())
                .countryCode(current.countryCode())
                .resourceType(command.resourceType())
                .translations(current.translations())
                .defaultValue(current.defaultValue())
                .context(command.context())
                .active(current.active())
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(command.updatedBy())
                .version(current.version() + 1)
                .build();

            LocalizedResource saved = repository.saveResource(updated).join();
            repository.cacheResource(saved).join();

            logger.info("Updated resource: {}", command.resourceKey());
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Optional<LocalizedResource>> addTranslation(String resourceId, String locale, String value, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            // Simplified - would need proper ID lookup
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<LocalizedResource>> removeTranslation(String resourceId, String locale, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            // Simplified - would need proper ID lookup
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteResource(String resourceId) {
        return CompletableFuture.supplyAsync(() -> {
            boolean deleted = repository.deleteResource(resourceId).join();
            logger.info("Deleted resource: {}", resourceId);
            return deleted;
        });
    }

    @Override
    public CompletableFuture<List<LocalizedResource>> bulkImportTranslations(String countryCode, Map<String, Map<String, String>> translations) {
        return CompletableFuture.supplyAsync(() -> {
            List<LocalizedResource> results = translations.entrySet().stream()
                .map(entry -> {
                    LocalizedResource resource = LocalizedResource.builder()
                        .resourceKey(entry.getKey())
                        .countryCode(countryCode)
                        .translations(entry.getValue())
                        .defaultValue(entry.getValue().values().stream().findFirst().orElse(""))
                        .createdBy("bulk-import")
                        .build();
                    return repository.saveResource(resource).join();
                })
                .toList();

            logger.info("Bulk imported {} resources for country: {}", results.size(), countryCode);
            return results;
        });
    }

    // Query implementation

    @Override
    public CompletableFuture<Optional<CountryLocalization>> getByCountryCode(String countryCode) {
        return repository.getCachedCountry(countryCode)
            .thenCompose(cached -> cached.isPresent()
                ? CompletableFuture.completedFuture(cached)
                : repository.findByCountryCode(countryCode)
                    .thenApply(opt -> opt.map(country -> {
                        repository.cacheCountry(country);
                        return country;
                    }))
            );
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> getAllActiveCountries() {
        return repository.findActiveCountries();
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> getAllCountries() {
        return repository.findAllCountries();
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> getCountriesByMeasurementSystem(CountryLocalization.MeasurementSystem system) {
        return repository.findByMeasurementSystem(system);
    }

    @Override
    public CompletableFuture<String> getTranslation(String resourceKey, String locale, String countryCode) {
        return repository.getCachedResource(resourceKey, countryCode)
            .thenCompose(cached -> cached.isPresent()
                ? CompletableFuture.completedFuture(cached.get().getTranslation(locale))
                : repository.findResourceByKey(resourceKey, countryCode)
                    .thenApply(opt -> opt.map(resource -> {
                        repository.cacheResource(resource);
                        return resource.getTranslation(locale);
                    }).orElse(""))
            );
    }

    @Override
    public CompletableFuture<Map<String, String>> getTranslations(String resourceKey, String countryCode) {
        return repository.findResourceByKey(resourceKey, countryCode)
            .thenApply(opt -> opt.map(LocalizedResource::translations).orElse(Map.of()));
    }

    @Override
    public CompletableFuture<List<LocalizedResource>> getResourcesByCountry(String countryCode) {
        return repository.findResourcesByCountry(countryCode);
    }

    @Override
    public CompletableFuture<List<LocalizedResource>> getResourcesByType(String resourceType) {
        return repository.findResourcesByType(resourceType);
    }

    @Override
    public CompletableFuture<Optional<LocalizedResource>> getResource(String resourceKey, String countryCode) {
        return repository.findResourceByKey(resourceKey, countryCode);
    }

    @Override
    public CompletableFuture<String> getCurrencySymbol(String countryCode) {
        return getByCountryCode(countryCode)
            .thenApply(opt -> opt.map(c -> c.currency().symbol()).orElse("€"));
    }

    @Override
    public CompletableFuture<String> getDateFormat(String countryCode) {
        return getByCountryCode(countryCode)
            .thenApply(opt -> opt.map(c -> c.dateTime().dateFormat()).orElse("dd/MM/yyyy"));
    }

    @Override
    public CompletableFuture<String> getTimeFormat(String countryCode) {
        return getByCountryCode(countryCode)
            .thenApply(opt -> opt.map(c -> c.dateTime().timeFormat()).orElse("HH:mm"));
    }

    @Override
    public CompletableFuture<String> getPhoneFormat(String countryCode) {
        return getByCountryCode(countryCode)
            .thenApply(opt -> opt.map(c -> c.phoneFormat().formatTemplate()).orElse("(XXX) XXX-XXXX"));
    }

    @Override
    public CompletableFuture<String> getEmergencyNumber(String countryCode) {
        return getByCountryCode(countryCode)
            .thenApply(opt -> opt.map(c -> c.emergencyServices().police()).orElse("112"));
    }

    @Override
    public CompletableFuture<Boolean> isValidCountryCode(String countryCode) {
        return CompletableFuture.completedFuture(VALID_COUNTRY_CODES.contains(countryCode.toUpperCase()));
    }

    @Override
    public CompletableFuture<Boolean> isValidLocale(String locale) {
        // Basic validation: should match pattern "xx-YY" or "xx_YY"
        return CompletableFuture.completedFuture(locale != null && locale.matches("^[a-z]{2}[-_][A-Z]{2}$"));
    }
}
