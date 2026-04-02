package com.gogidix.rapidassist.country.localization.config.service.domain.policy;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain policy for validating country localizations.
 *
 * <p>This policy encapsulates business rules for localization validation:
 * <ul>
 *   <li>Country codes must be valid ISO 3166-1 alpha-2 codes</li>
 *   <li>Required configurations must be present (locale, currency, dateTime)</li>
 *   <li>Emergency services numbers must be provided for active countries</li>
 *   <li>Legal requirements must be configured for EU countries</li>
 *   <li>Measurement system must be consistent with country region</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any localization.
 */
public class CountryLocalizationValidationPolicy {

    // EU country codes requiring GDPR compliance
    private static final Set<String> EU_COUNTRY_CODES = Set.of(
        "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
        "DE", "GR", "HU", "IS", "IE", "IT", "LV", "LI", "LT", "LU",
        "MT", "NL", "NO", "PL", "PT", "RO", "SK", "SI", "ES", "SE"
    );

    // Countries using Imperial system
    private static final Set<String> IMPERIAL_SYSTEM_COUNTRIES = Set.of(
        "US", "LR", "MM"
    );

    // Countries using mixed systems (e.g., UK uses metric for most things but imperial for some)
    private static final Set<String> MIXED_SYSTEM_COUNTRIES = Set.of(
        "GB", "CA", "AU", "IN", "PK"
    );

    private final boolean requireEmergencyServices;
    private final boolean enforceGDPRForEU;
    private final boolean validateMeasurementSystem;

    private CountryLocalizationValidationPolicy(Builder builder) {
        this.requireEmergencyServices = builder.requireEmergencyServices;
        this.enforceGDPRForEU = builder.enforceGDPRForEU;
        this.validateMeasurementSystem = builder.validateMeasurementSystem;
    }

    /**
     * Validates a localization according to this policy.
     *
     * @param localization the localization to validate
     * @return validation result
     */
    public ValidationResult validate(CountryLocalization localization) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Country code must be valid ISO 3166-1 alpha-2
        if (!isValidCountryCode(localization.countryCode())) {
            errors.add("Invalid country code: must be ISO 3166-1 alpha-2 format (2 uppercase letters)");
        }

        // Rule 2: Country name must be present
        if (localization.countryName() == null || localization.countryName().isBlank()) {
            errors.add("Country name is required");
        }

        // Rule 3: Required configurations must be present
        if (localization.locale() == null) {
            errors.add("Locale configuration is required");
        } else {
            validateLocale(localization.locale(), errors);
        }

        if (localization.currency() == null) {
            errors.add("Currency configuration is required");
        } else {
            validateCurrency(localization.currency(), errors);
        }

        if (localization.dateTime() == null) {
            errors.add("DateTime configuration is required");
        }

        if (localization.addressFormat() == null) {
            errors.add("Address format configuration is required");
        }

        if (localization.phoneFormat() == null) {
            errors.add("Phone format configuration is required");
        }

        // Rule 4: Active countries must have emergency services
        if (requireEmergencyServices && localization.active()) {
            if (localization.emergencyServices() == null) {
                errors.add("Emergency services configuration is required for active countries");
            } else {
                validateEmergencyServices(localization.emergencyServices(), errors);
            }
        }

        // Rule 5: EU countries must have GDPR configuration
        if (enforceGDPRForEU && EU_COUNTRY_CODES.contains(localization.countryCode())) {
            if (localization.legalRequirements() == null) {
                warnings.add("Legal requirements configuration is recommended for EU countries (GDPR compliance)");
            } else {
                validateGDPR(localization.legalRequirements(), warnings);
            }
        }

        // Rule 6: Measurement system validation
        if (validateMeasurementSystem) {
            validateMeasurementSystem(localization.countryCode(), localization.measurementSystem(), warnings);
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates a localization change request.
     *
     * @param oldValue the existing localization (null for new configs)
     * @param newValue the new value
     * @param changedBy the user making the change
     * @return validation result
     */
    public ChangeValidationResult validateChange(
            CountryLocalization oldValue,
            CountryLocalization newValue,
            String changedBy) {

        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Cannot change country code
        if (oldValue != null && !oldValue.countryCode().equals(newValue.countryCode())) {
            errors.add("Country code cannot be changed. Create a new localization instead.");
        }

        // Rule 2: Deactivating a localization requires verification
        if (oldValue != null && oldValue.active() && !newValue.active()) {
            warnings.add("Deactivating a country localization will affect all services using this configuration");
        }

        // Rule 3: Emergency services changes for active countries require audit
        if (newValue.active() && oldValue != null) {
            if (!oldValue.emergencyServices().equals(newValue.emergencyServices())) {
                warnings.add("Emergency services changes for active countries require audit logging");
            }
        }

        return new ChangeValidationResult(errors.isEmpty(), errors, warnings);
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private boolean isValidCountryCode(String countryCode) {
        return countryCode != null && countryCode.matches("^[A-Z]{2}$");
    }

    private void validateLocale(CountryLocalization.LocaleConfig locale, Set<String> errors) {
        if (locale.languageCode() == null || locale.languageCode().isBlank()) {
            errors.add("Language code is required in locale configuration");
        }

        if (locale.regionCode() == null || locale.regionCode().isBlank()) {
            errors.add("Region code is required in locale configuration");
        }

        if (locale.numberFormat() == null || locale.numberFormat().isBlank()) {
            errors.add("Number format is required in locale configuration");
        }

        if (locale.dateFormat() == null || locale.dateFormat().isBlank()) {
            errors.add("Date format is required in locale configuration");
        }
    }

    private void validateCurrency(CountryLocalization.CurrencyConfig currency, Set<String> errors) {
        if (currency.currencyCode() == null || currency.currencyCode().isBlank()) {
            errors.add("Currency code is required in currency configuration");
        } else if (currency.currencyCode().length() != 3) {
            errors.add("Currency code must be a valid ISO 4217 code (3 letters)");
        }

        if (currency.symbol() == null || currency.symbol().isBlank()) {
            errors.add("Currency symbol is required");
        }

        if (currency.decimalPlaces() < 0 || currency.decimalPlaces() > 4) {
            errors.add("Currency decimal places must be between 0 and 4");
        }
    }

    private void validateEmergencyServices(CountryLocalization.EmergencyServices emergency, Set<String> errors) {
        if (emergency.police() == null || emergency.police().isBlank()) {
            errors.add("Police emergency number is required");
        }

        if (emergency.ambulance() == null || emergency.ambulance().isBlank()) {
            errors.add("Ambulance emergency number is required");
        }

        if (emergency.fire() == null || emergency.fire().isBlank()) {
            errors.add("Fire emergency number is required");
        }
    }

    private void validateGDPR(CountryLocalization.LegalRequirements legal, Set<String> warnings) {
        if (!legal.requireDataConsent()) {
            warnings.add("Data consent requirement should be enabled for GDPR compliance");
        }

        if (!legal.requireCookieConsent()) {
            warnings.add("Cookie consent requirement should be enabled for GDPR compliance");
        }

        if (legal.gdprCountryCodes() == null || legal.gdprCountryCodes().isEmpty()) {
            warnings.add("GDPR country codes should be specified");
        }
    }

    private void validateMeasurementSystem(String countryCode, CountryLocalization.MeasurementSystem system, Set<String> warnings) {
        CountryLocalization.MeasurementSystem expected = getExpectedMeasurementSystem(countryCode);

        if (system != expected) {
            warnings.add("Measurement system '" + system + "' may not match typical usage for country " + countryCode + " (expected: " + expected + ")");
        }
    }

    private CountryLocalization.MeasurementSystem getExpectedMeasurementSystem(String countryCode) {
        if (IMPERIAL_SYSTEM_COUNTRIES.contains(countryCode)) {
            return CountryLocalization.MeasurementSystem.IMPERIAL;
        } else if (MIXED_SYSTEM_COUNTRIES.contains(countryCode)) {
            return CountryLocalization.MeasurementSystem.MIXED;
        } else {
            return CountryLocalization.MeasurementSystem.METRIC;
        }
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(CountryLocalizationValidationPolicy existing) {
        return new Builder()
            .requireEmergencyServices(existing.requireEmergencyServices)
            .enforceGDPRForEU(existing.enforceGDPRForEU)
            .validateMeasurementSystem(existing.validateMeasurementSystem);
    }

    public static class Builder {
        private boolean requireEmergencyServices = true;
        private boolean enforceGDPRForEU = true;
        private boolean validateMeasurementSystem = true;

        public Builder requireEmergencyServices(boolean value) {
            this.requireEmergencyServices = value;
            return this;
        }

        public Builder enforceGDPRForEU(boolean value) {
            this.enforceGDPRForEU = value;
            return this;
        }

        public Builder validateMeasurementSystem(boolean value) {
            this.validateMeasurementSystem = value;
            return this;
        }

        public CountryLocalizationValidationPolicy build() {
            return new CountryLocalizationValidationPolicy(this);
        }
    }

    // ========================================================================
    // Result classes
    // ========================================================================

    public record ValidationResult(
        boolean isValid,
        Set<String> errors,
        Set<String> warnings
    ) {
        public ValidationResult {
            errors = Set.copyOf(errors);
            warnings = Set.copyOf(warnings);
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, Set.of(), Set.of());
        }

        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, Set.of(error), Set.of());
        }
    }

    public record ChangeValidationResult(
        boolean isValid,
        Set<String> errors,
        Set<String> warnings
    ) {
        public ChangeValidationResult {
            errors = Set.copyOf(errors);
            warnings = Set.copyOf(warnings);
        }

        public static ChangeValidationResult valid() {
            return new ChangeValidationResult(true, Set.of(), Set.of());
        }
    }
}
