package com.gogidix.rapidassist.country.localization.config.service.domain.aggregate;

import com.gogidix.rapidassist.country.localization.config.service.domain.event.CountryLocalizationCreatedEvent;
import com.gogidix.rapidassist.country.localization.config.service.domain.event.CountryLocalizationDeletedEvent;
import com.gogidix.rapidassist.country.localization.config.service.domain.event.CountryLocalizationUpdatedEvent;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

import java.util.ArrayList;
import java.util.List;

/**
 * CountryLocalization Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around country localization management:
 * <ul>
 *   <li>Tenant ID must be present for all operations (multi-tenancy)</li>
 *   <li>Country codes must be valid ISO 3166-1 alpha-2 codes</li>
 *   <li>Country localizations must have required configurations (locale, currency, etc.)</li>
 *   <li>Active country localizations cannot be deleted without deactivation</li>
 *   <li>Localization changes must be tracked with audit trail</li>
 * </ul>
 */
public class CountryLocalizationAggregate {

    private final CountryLocalization localization;
    private final List<CountryLocalizationUpdatedEvent> pendingEvents;
    private boolean isNew;

    private CountryLocalizationAggregate(CountryLocalization localization, boolean isNew) {
        this.localization = localization;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new CountryLocalizationAggregate for a new localization.
     *
     * @param tenantId    the tenant ID (required for multi-tenancy)
     * @param countryCode the ISO 3166-1 alpha-2 country code
     * @param countryName the country name
     * @param locale      the locale configuration
     * @param currency    the currency configuration
     * @param createdBy   the user creating the localization
     * @return a new CountryLocalizationAggregate
     */
    public static CountryLocalizationAggregate create(
            String tenantId,
            String countryCode,
            String countryName,
            CountryLocalization.LocaleConfig locale,
            CountryLocalization.CurrencyConfig currency,
            String createdBy) {

        // Validate tenant ID (CRITICAL for multi-tenancy)
        validateTenantId(tenantId);

        // Validate business rules
        validateCountryCode(countryCode);
        validateCountryName(countryName);
        validateLocaleConfig(locale);
        validateCurrencyConfig(currency);

        CountryLocalization loc = CountryLocalization.builder()
            .tenantId(tenantId)
            .countryCode(countryCode)
            .countryName(countryName)
            .locale(locale)
            .currency(currency)
            .dateTime(CountryLocalization.DateTimeConfig.of("UTC"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}\\n{postcode}",
                List.of("street", "city", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+000"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("000", "000"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy(createdBy)
            .build();

        return new CountryLocalizationAggregate(loc, true);
    }

    /**
     * Reconstructs an existing CountryLocalizationAggregate from persistence.
     *
     * @param localization the localization from persistence
     * @return a CountryLocalizationAggregate
     */
    public static CountryLocalizationAggregate fromExisting(CountryLocalization localization) {
        // Validate tenant ID exists
        if (localization.tenantId() == null || localization.tenantId().isBlank()) {
            throw new IllegalArgumentException("Country localization must have a tenant ID");
        }
        return new CountryLocalizationAggregate(localization, false);
    }

    /**
     * Updates the localization configuration.
     *
     * @param newLocalization the new localization configuration
     * @param updatedBy       the user making the change
     * @param reason          the reason for the change
     * @return the CountryLocalizationUpdatedEvent if successful
     */
    public CountryLocalizationUpdatedEvent update(CountryLocalization newLocalization, String updatedBy, String reason) {
        // Validate the update - tenant ID cannot change
        if (!localization.tenantId().equals(newLocalization.tenantId())) {
            throw new IllegalArgumentException("Tenant ID cannot be changed");
        }

        // Country code cannot change
        if (!localization.countryCode().equals(newLocalization.countryCode())) {
            throw new IllegalArgumentException("Country code cannot be changed");
        }

        CountryLocalization oldLocalization = this.localization;

        CountryLocalizationUpdatedEvent event = new CountryLocalizationUpdatedEvent(
            oldLocalization, newLocalization, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Changes the active status of this localization.
     *
     * @param active    the new active status
     * @param changedBy the user making the change
     * @return the updated localization
     */
    public CountryLocalization changeActiveStatus(boolean active, String changedBy) {
        // Business rule: Cannot deactivate active localization without verification
        if (!active && localization.active()) {
            // Could add additional validation here
        }

        return CountryLocalization.builder()
            .id(localization.id())
            .tenantId(localization.tenantId())
            .countryCode(localization.countryCode())
            .countryName(localization.countryName())
            .locale(localization.locale())
            .currency(localization.currency())
            .dateTime(localization.dateTime())
            .addressFormat(localization.addressFormat())
            .phoneFormat(localization.phoneFormat())
            .emergencyServices(localization.emergencyServices())
            .legalRequirements(localization.legalRequirements())
            .measurementSystem(localization.measurementSystem())
            .active(active)
            .createdBy(localization.createdBy())
            .createdAt(localization.createdAt())
            .updatedBy(changedBy)
            .updatedAt(java.time.Instant.now())
            .version(localization.version() + 1)
            .build();
    }

    /**
     * Marks this localization for deletion.
     *
     * @param deletedBy the user deleting the localization
     * @param reason    the reason for deletion
     * @return the CountryLocalizationDeletedEvent
     */
    public CountryLocalizationDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: Active localizations cannot be deleted
        if (localization.active()) {
            throw new IllegalStateException(
                "Active country localizations must be deactivated before deletion."
            );
        }

        return new CountryLocalizationDeletedEvent(
            localization.id(),
            localization.tenantId(),
            localization.countryCode(),
            localization.countryName(),
            localization.version(),
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current localization configuration.
     *
     * @return true if valid, throws exception if invalid
     */
    public boolean validate() {
        // Validate tenant ID
        validateTenantId(localization.tenantId());

        validateCountryCode(localization.countryCode());
        validateCountryName(localization.countryName());

        if (localization.locale() == null) {
            throw new IllegalStateException("Locale configuration is required");
        }

        if (localization.currency() == null) {
            throw new IllegalStateException("Currency configuration is required");
        }

        if (localization.dateTime() == null) {
            throw new IllegalStateException("DateTime configuration is required");
        }

        return true;
    }

    /**
     * Gets the underlying localization entity.
     *
     * @return the localization
     */
    public CountryLocalization localization() {
        return localization;
    }

    /**
     * Gets the localization ID.
     *
     * @return the ID
     */
    public String id() {
        return localization.id();
    }

    /**
     * Gets the tenant ID.
     *
     * @return the tenant ID
     */
    public String tenantId() {
        return localization.tenantId();
    }

    /**
     * Checks if this is a newly created aggregate.
     *
     * @return true if new, false if loaded from persistence
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Gets all pending events that haven't been published yet.
     *
     * @return list of pending events
     */
    public List<CountryLocalizationUpdatedEvent> getPendingEvents() {
        return new ArrayList<>(pendingEvents);
    }

    /**
     * Clears pending events after they've been published.
     */
    public void clearPendingEvents() {
        pendingEvents.clear();
    }

    // ========================================================================
    // Private validation methods
    // ========================================================================

    private static void validateTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be blank");
        }
    }

    private static void validateCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            throw new IllegalArgumentException("Country code cannot be blank");
        }

        // ISO 3166-1 alpha-2: exactly 2 letters
        if (!countryCode.matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException(
                "Country code must be a valid ISO 3166-1 alpha-2 code (2 uppercase letters)"
            );
        }
    }

    private static void validateCountryName(String countryName) {
        if (countryName == null || countryName.isBlank()) {
            throw new IllegalArgumentException("Country name cannot be blank");
        }

        if (countryName.length() > 100) {
            throw new IllegalArgumentException("Country name cannot exceed 100 characters");
        }
    }

    private static void validateLocaleConfig(CountryLocalization.LocaleConfig locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale configuration is required");
        }

        if (locale.languageCode() == null || locale.languageCode().isBlank()) {
            throw new IllegalArgumentException("Language code is required in locale configuration");
        }

        if (locale.regionCode() == null || locale.regionCode().isBlank()) {
            throw new IllegalArgumentException("Region code is required in locale configuration");
        }
    }

    private static void validateCurrencyConfig(CountryLocalization.CurrencyConfig currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency configuration is required");
        }

        if (currency.currencyCode() == null || currency.currencyCode().isBlank()) {
            throw new IllegalArgumentException("Currency code is required in currency configuration");
        }

        if (currency.currencyCode().length() != 3) {
            throw new IllegalArgumentException("Currency code must be a valid ISO 4217 code (3 letters)");
        }
    }
}
