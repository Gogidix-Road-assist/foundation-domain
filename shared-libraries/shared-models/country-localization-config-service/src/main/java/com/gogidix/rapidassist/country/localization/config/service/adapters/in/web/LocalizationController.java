package com.gogidix.rapidassist.country.localization.config.service.adapters.in.web;

import com.gogidix.rapidassist.country.localization.config.service.application.LocalizationService;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.in.LocalizationCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST Controller for Country Localization Configuration Service
 */
@RestController
@RequestMapping("/api/localization")
@CrossOrigin(origins = "*")
public class LocalizationController {

    private final LocalizationCommand commandService;
    private final LocalizationService queryService;

    public LocalizationController(LocalizationService localizationService) {
        this.commandService = localizationService;
        this.queryService = localizationService;
    }

    // Health & Status

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "country-localization-config-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    // Country Localization Endpoints

    @GetMapping("/countries")
    public ResponseEntity<List<CountryLocalization>> getAllCountries(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(queryService.getAllActiveCountries().join());
        }
        return ResponseEntity.ok(queryService.getAllCountries().join());
    }

    @GetMapping("/countries/{countryCode}")
    public ResponseEntity<CountryLocalization> getCountry(@PathVariable String countryCode) {
        return queryService.getByCountryCode(countryCode).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/countries/measurement/{system}")
    public ResponseEntity<List<CountryLocalization>> getCountriesByMeasurementSystem(
            @PathVariable CountryLocalization.MeasurementSystem system) {
        return ResponseEntity.ok(queryService.getCountriesByMeasurementSystem(system).join());
    }

    @PostMapping("/countries")
    public ResponseEntity<CountryLocalization> createCountry(@Valid @RequestBody CreateCountryRequest request) {
        LocalizationCommand.CreateCountryCommand command = new LocalizationCommand.CreateCountryCommand(
            request.countryCode(),
            request.countryName(),
            request.locale(),
            request.currency(),
            request.dateTime(),
            request.addressFormat(),
            request.phoneFormat(),
            request.emergencyServices(),
            request.legalRequirements(),
            request.measurementSystem(),
            request.createdBy()
        );

        CountryLocalization country = commandService.createCountryLocalization(command).join();
        return ResponseEntity.created(URI.create("/api/localization/countries/" + country.countryCode())).body(country);
    }

    @PutMapping("/countries/{countryCode}")
    public ResponseEntity<CountryLocalization> updateCountry(
            @PathVariable String countryCode,
            @RequestBody UpdateCountryRequest request) {
        LocalizationCommand.UpdateCountryCommand command = new LocalizationCommand.UpdateCountryCommand(
            request.countryName(),
            request.locale(),
            request.currency(),
            request.dateTime(),
            request.addressFormat(),
            request.phoneFormat(),
            request.emergencyServices(),
            request.legalRequirements(),
            request.measurementSystem(),
            request.updatedBy()
        );

        return commandService.updateCountryLocalization(countryCode, command).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/countries/{countryCode}/activate")
    public ResponseEntity<CountryLocalization> activateCountry(
            @PathVariable String countryCode,
            @RequestBody Map<String, String> request) {
        return commandService.activateCountry(countryCode, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/countries/{countryCode}/deactivate")
    public ResponseEntity<CountryLocalization> deactivateCountry(
            @PathVariable String countryCode,
            @RequestBody Map<String, String> request) {
        return commandService.deactivateCountry(countryCode, request.get("updatedBy")).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/countries/{countryCode}")
    public ResponseEntity<Void> deleteCountry(@PathVariable String countryCode) {
        boolean deleted = commandService.deleteCountryLocalization(countryCode).join();
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Localization Helpers

    @GetMapping("/countries/{countryCode}/currency-symbol")
    public ResponseEntity<Map<String, String>> getCurrencySymbol(@PathVariable String countryCode) {
        String symbol = queryService.getCurrencySymbol(countryCode).join();
        return ResponseEntity.ok(Map.of("symbol", symbol));
    }

    @GetMapping("/countries/{countryCode}/date-format")
    public ResponseEntity<Map<String, String>> getDateFormat(@PathVariable String countryCode) {
        String format = queryService.getDateFormat(countryCode).join();
        return ResponseEntity.ok(Map.of("dateFormat", format));
    }

    @GetMapping("/countries/{countryCode}/time-format")
    public ResponseEntity<Map<String, String>> getTimeFormat(@PathVariable String countryCode) {
        String format = queryService.getTimeFormat(countryCode).join();
        return ResponseEntity.ok(Map.of("timeFormat", format));
    }

    @GetMapping("/countries/{countryCode}/phone-format")
    public ResponseEntity<Map<String, String>> getPhoneFormat(@PathVariable String countryCode) {
        String format = queryService.getPhoneFormat(countryCode).join();
        return ResponseEntity.ok(Map.of("phoneFormat", format));
    }

    @GetMapping("/countries/{countryCode}/emergency-number")
    public ResponseEntity<Map<String, String>> getEmergencyNumber(@PathVariable String countryCode) {
        String number = queryService.getEmergencyNumber(countryCode).join();
        return ResponseEntity.ok(Map.of("emergencyNumber", number));
    }

    // Translation Endpoints

    @GetMapping("/translations")
    public ResponseEntity<String> getTranslation(
            @RequestParam String resourceKey,
            @RequestParam String locale,
            @RequestParam String countryCode) {
        String translation = queryService.getTranslation(resourceKey, locale, countryCode).join();
        return ResponseEntity.ok(translation);
    }

    @GetMapping("/translations/{resourceKey}")
    public ResponseEntity<Map<String, String>> getTranslations(
            @PathVariable String resourceKey,
            @RequestParam String countryCode) {
        Map<String, String> translations = queryService.getTranslations(resourceKey, countryCode).join();
        return ResponseEntity.ok(translations);
    }

    @GetMapping("/resources")
    public ResponseEntity<List<LocalizedResource>> getResources(
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String resourceType) {
        if (countryCode != null) {
            return ResponseEntity.ok(queryService.getResourcesByCountry(countryCode).join());
        }
        if (resourceType != null) {
            return ResponseEntity.ok(queryService.getResourcesByType(resourceType).join());
        }
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/resources")
    public ResponseEntity<LocalizedResource> createResource(@Valid @RequestBody CreateResourceRequest request) {
        LocalizationCommand.CreateResourceCommand command = new LocalizationCommand.CreateResourceCommand(
            request.resourceKey(),
            request.countryCode(),
            request.resourceType(),
            request.translations(),
            request.defaultValue(),
            request.context(),
            request.createdBy()
        );

        LocalizedResource resource = commandService.createResource(command).join();
        return ResponseEntity.created(URI.create("/api/localization/resources/" + resource.id())).body(resource);
    }

    @PostMapping("/translations/bulk-import")
    public ResponseEntity<List<LocalizedResource>> bulkImport(
            @RequestParam String countryCode,
            @RequestBody Map<String, Map<String, String>> translations) {
        List<LocalizedResource> results = commandService.bulkImportTranslations(countryCode, translations).join();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/validate/country-code/{countryCode}")
    public ResponseEntity<Map<String, Boolean>> validateCountryCode(@PathVariable String countryCode) {
        boolean valid = queryService.isValidCountryCode(countryCode).join();
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @GetMapping("/validate/locale/{locale}")
    public ResponseEntity<Map<String, Boolean>> validateLocale(@PathVariable String locale) {
        boolean valid = queryService.isValidLocale(locale).join();
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    // Request DTOs

    record CreateCountryRequest(
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

    record UpdateCountryRequest(
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

    record CreateResourceRequest(
        String resourceKey,
        String countryCode,
        String resourceType,
        Map<String, String> translations,
        String defaultValue,
        String context,
        String createdBy
    ) {}
}
