package com.gogidix.rapidassist.country.localization.config.service.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Domain model representing country-specific localization configuration
 *
 * <p>This model is tenant-scoped for multi-tenancy isolation.
 * All queries and operations must respect tenant boundaries.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryLocalization {

    private final String id;
    private final String tenantId; // Multi-tenancy: isolates data by tenant
    private final String countryCode; // ISO 3166-1 alpha-2 (e.g., "IE", "GB", "US")
    private final String countryName;
    private final LocaleConfig locale;
    private final CurrencyConfig currency;
    private final DateTimeConfig dateTime;
    private final AddressFormat addressFormat;
    private final PhoneFormat phoneFormat;
    private final EmergencyServices emergencyServices;
    private final LegalRequirements legalRequirements;
    private final MeasurementSystem measurementSystem;
    private final boolean active;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    private CountryLocalization(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.countryCode = builder.countryCode;
        this.countryName = builder.countryName;
        this.locale = builder.locale;
        this.currency = builder.currency;
        this.dateTime = builder.dateTime;
        this.addressFormat = builder.addressFormat;
        this.phoneFormat = builder.phoneFormat;
        this.emergencyServices = builder.emergencyServices;
        this.legalRequirements = builder.legalRequirements;
        this.measurementSystem = builder.measurementSystem;
        this.active = builder.active;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String countryCode() { return countryCode; }
    public String countryName() { return countryName; }
    public LocaleConfig locale() { return locale; }
    public CurrencyConfig currency() { return currency; }
    public DateTimeConfig dateTime() { return dateTime; }
    public AddressFormat addressFormat() { return addressFormat; }
    public PhoneFormat phoneFormat() { return phoneFormat; }
    public EmergencyServices emergencyServices() { return emergencyServices; }
    public LegalRequirements legalRequirements() { return legalRequirements; }
    public MeasurementSystem measurementSystem() { return measurementSystem; }
    public boolean active() { return active; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    // Nested configurations

    public record LocaleConfig(
        String languageCode,    // ISO 639-1 (e.g., "en")
        String regionCode,      // ISO 3166-1 alpha-2 (e.g., "GB")
        String variant,         // e.g., "en-GB"
        String scriptCode,      // ISO 15924 (e.g., "Latn")
        String numberFormat,    // decimal pattern
        String dateFormat,      // date pattern
        String timeFormat,      // time pattern
        String dateTimeFormat   // combined pattern
    ) {
        public static LocaleConfig of(String language, String region) {
            return new LocaleConfig(language, region, language + "-" + region, null,
                "#,##0.###", "dd/MM/yyyy", "HH:mm", "dd/MM/yyyy HH:mm");
        }

        public String getLocaleTag() {
            return variant != null ? variant : languageCode + "-" + regionCode;
        }
    }

    public record CurrencyConfig(
        String currencyCode,    // ISO 4217 (e.g., "EUR", "GBP")
        String symbol,          // e.g., "€", "£"
        String symbolPosition,  // "BEFORE" or "AFTER"
        int decimalPlaces,
        String thousandsSeparator,
        String decimalSeparator,
        boolean requireSymbolForInternational
    ) {
        public static CurrencyConfig of(String code, String symbol) {
            return new CurrencyConfig(code, symbol, "BEFORE", 2, ",", ".", true);
        }
    }

    public record DateTimeConfig(
        String timezone,        // IANA timezone ID (e.g., "Europe/Dublin")
        String dateFormat,
        String timeFormat,
        String dateTimeFormat,
        boolean use24HourFormat,
        String firstDayOfWeek,  // "Monday", "Sunday", etc.
        List<String> holidays,  // List of holiday dates or patterns
        boolean observeDST      // Daylight Saving Time
    ) {
        public static DateTimeConfig of(String timezone) {
            return new DateTimeConfig(timezone, "dd/MM/yyyy", "HH:mm",
                "dd/MM/yyyy HH:mm", true, "Monday", List.of(), true);
        }
    }

    public record AddressFormat(
        String formatTemplate,  // e.g., "{street}\n{city}\n{postcode}"
        List<String> fieldOrder,
        boolean postcodeBeforeCity,
        String stateLabel,      // "State", "Province", "County", etc.
        boolean requireState
    ) {
        public static AddressFormat of(String template, List<String> order) {
            return new AddressFormat(template, order, false, "County", false);
        }
    }

    public record PhoneFormat(
        String countryCode,     // +353, +44, etc.
        int dialingCodeLength,
        int numberLength,
        String formatTemplate,  // e.g., "(XXX) XXX-XXXX"
        boolean requireTrunkPrefix,
        String trunkPrefix      // e.g., "0"
    ) {
        public static PhoneFormat of(String code) {
            return new PhoneFormat(code, 2, 7, "(XXX) XXX-XXXX", true, "0");
        }

        public String format(String localNumber) {
            if (localNumber == null || localNumber.isEmpty()) {
                return countryCode;
            }
            return countryCode + " " + localNumber;
        }
    }

    public record EmergencyServices(
        String police,          // Emergency phone number
        String ambulance,
        String fire,
        String nonEmergency
    ) {
        public static EmergencyServices of(String emergencyNumber, String nonEmergency) {
            return new EmergencyServices(emergencyNumber, emergencyNumber,
                emergencyNumber, nonEmergency);
        }
    }

    public record LegalRequirements(
        boolean requireVAT,
        String vatRate,
        boolean requireDataConsent,
        String privacyPolicyVersion,
        boolean requireCookieConsent,
        String minimumAge,
        Set<String> gdprCountryCodes  // Countries subject to GDPR
    ) {
        public static LegalRequirements standard() {
            return new LegalRequirements(true, "23%", true, "1.0",
                true, "18", Set.of("IE", "GB", "FR", "DE", "IT", "ES", "NL", "BE"));
        }
    }

    public enum MeasurementSystem {
        METRIC, IMPERIAL, MIXED
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String countryCode;
        private String countryName;
        private LocaleConfig locale;
        private CurrencyConfig currency;
        private DateTimeConfig dateTime;
        private AddressFormat addressFormat;
        private PhoneFormat phoneFormat;
        private EmergencyServices emergencyServices;
        private LegalRequirements legalRequirements;
        private MeasurementSystem measurementSystem = MeasurementSystem.METRIC;
        private boolean active = true;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public Builder countryName(String countryName) { this.countryName = countryName; return this; }
        public Builder locale(LocaleConfig locale) { this.locale = locale; return this; }
        public Builder currency(CurrencyConfig currency) { this.currency = currency; return this; }
        public Builder dateTime(DateTimeConfig dateTime) { this.dateTime = dateTime; return this; }
        public Builder addressFormat(AddressFormat addressFormat) { this.addressFormat = addressFormat; return this; }
        public Builder phoneFormat(PhoneFormat phoneFormat) { this.phoneFormat = phoneFormat; return this; }
        public Builder emergencyServices(EmergencyServices emergencyServices) { this.emergencyServices = emergencyServices; return this; }
        public Builder legalRequirements(LegalRequirements legalRequirements) { this.legalRequirements = legalRequirements; return this; }
        public Builder measurementSystem(MeasurementSystem measurementSystem) { this.measurementSystem = measurementSystem; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public CountryLocalization build() {
            return new CountryLocalization(this);
        }
    }
}
