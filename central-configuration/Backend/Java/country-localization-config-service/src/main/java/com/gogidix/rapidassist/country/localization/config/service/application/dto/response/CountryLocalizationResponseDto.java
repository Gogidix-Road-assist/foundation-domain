package com.gogidix.rapidassist.country.localization.config.service.application.dto.response;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for CountryLocalization entities.
 *
 * <p>This DTO is used for API responses and contains only the
 * information that should be exposed to clients.
 */
public record CountryLocalizationResponseDto(
    String id,
    String countryCode,
    String countryName,
    LocaleConfigDto locale,
    CurrencyConfigDto currency,
    DateTimeConfigDto dateTime,
    AddressFormatDto addressFormat,
    PhoneFormatDto phoneFormat,
    EmergencyServicesDto emergencyServices,
    LegalRequirementsDto legalRequirements,
    String measurementSystem,
    boolean active,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Integer version
) {
    public record LocaleConfigDto(
        String languageCode,
        String regionCode,
        String variant,
        String scriptCode,
        String numberFormat,
        String dateFormat,
        String timeFormat,
        String dateTimeFormat
    ) {}

    public record CurrencyConfigDto(
        String currencyCode,
        String symbol,
        String symbolPosition,
        int decimalPlaces,
        String thousandsSeparator,
        String decimalSeparator,
        boolean requireSymbolForInternational
    ) {}

    public record DateTimeConfigDto(
        String timezone,
        String dateFormat,
        String timeFormat,
        String dateTimeFormat,
        boolean use24HourFormat,
        String firstDayOfWeek,
        List<String> holidays,
        boolean observeDST
    ) {}

    public record AddressFormatDto(
        String formatTemplate,
        List<String> fieldOrder,
        boolean postcodeBeforeCity,
        String stateLabel,
        boolean requireState
    ) {}

    public record PhoneFormatDto(
        String countryCode,
        int dialingCodeLength,
        int numberLength,
        String formatTemplate,
        boolean requireTrunkPrefix,
        String trunkPrefix
    ) {}

    public record EmergencyServicesDto(
        String police,
        String ambulance,
        String fire,
        String nonEmergency
    ) {}

    public record LegalRequirementsDto(
        boolean requireVAT,
        String vatRate,
        boolean requireDataConsent,
        String privacyPolicyVersion,
        boolean requireCookieConsent,
        String minimumAge,
        List<String> gdprCountryCodes
    ) {}

    /**
     * Converts a domain CountryLocalization entity to a Response DTO.
     *
     * @param localization the domain entity
     * @return the response DTO
     */
    public static CountryLocalizationResponseDto fromDomain(CountryLocalization localization) {
        return new CountryLocalizationResponseDto(
            localization.id(),
            localization.countryCode(),
            localization.countryName(),
            toLocaleConfigDto(localization.locale()),
            toCurrencyConfigDto(localization.currency()),
            toDateTimeConfigDto(localization.dateTime()),
            toAddressFormatDto(localization.addressFormat()),
            toPhoneFormatDto(localization.phoneFormat()),
            toEmergencyServicesDto(localization.emergencyServices()),
            toLegalRequirementsDto(localization.legalRequirements()),
            localization.measurementSystem().name(),
            localization.active(),
            localization.createdBy(),
            localization.createdAt(),
            localization.updatedBy(),
            localization.updatedAt(),
            localization.version()
        );
    }

    private static LocaleConfigDto toLocaleConfigDto(CountryLocalization.LocaleConfig locale) {
        if (locale == null) return null;
        return new LocaleConfigDto(
            locale.languageCode(),
            locale.regionCode(),
            locale.variant(),
            locale.scriptCode(),
            locale.numberFormat(),
            locale.dateFormat(),
            locale.timeFormat(),
            locale.dateTimeFormat()
        );
    }

    private static CurrencyConfigDto toCurrencyConfigDto(CountryLocalization.CurrencyConfig currency) {
        if (currency == null) return null;
        return new CurrencyConfigDto(
            currency.currencyCode(),
            currency.symbol(),
            currency.symbolPosition(),
            currency.decimalPlaces(),
            currency.thousandsSeparator(),
            currency.decimalSeparator(),
            currency.requireSymbolForInternational()
        );
    }

    private static DateTimeConfigDto toDateTimeConfigDto(CountryLocalization.DateTimeConfig dateTime) {
        if (dateTime == null) return null;
        return new DateTimeConfigDto(
            dateTime.timezone(),
            dateTime.dateFormat(),
            dateTime.timeFormat(),
            dateTime.dateTimeFormat(),
            dateTime.use24HourFormat(),
            dateTime.firstDayOfWeek(),
            dateTime.holidays(),
            dateTime.observeDST()
        );
    }

    private static AddressFormatDto toAddressFormatDto(CountryLocalization.AddressFormat addressFormat) {
        if (addressFormat == null) return null;
        return new AddressFormatDto(
            addressFormat.formatTemplate(),
            addressFormat.fieldOrder(),
            addressFormat.postcodeBeforeCity(),
            addressFormat.stateLabel(),
            addressFormat.requireState()
        );
    }

    private static PhoneFormatDto toPhoneFormatDto(CountryLocalization.PhoneFormat phoneFormat) {
        if (phoneFormat == null) return null;
        return new PhoneFormatDto(
            phoneFormat.countryCode(),
            phoneFormat.dialingCodeLength(),
            phoneFormat.numberLength(),
            phoneFormat.formatTemplate(),
            phoneFormat.requireTrunkPrefix(),
            phoneFormat.trunkPrefix()
        );
    }

    private static EmergencyServicesDto toEmergencyServicesDto(CountryLocalization.EmergencyServices emergency) {
        if (emergency == null) return null;
        return new EmergencyServicesDto(
            emergency.police(),
            emergency.ambulance(),
            emergency.fire(),
            emergency.nonEmergency()
        );
    }

    private static LegalRequirementsDto toLegalRequirementsDto(CountryLocalization.LegalRequirements legal) {
        if (legal == null) return null;
        return new LegalRequirementsDto(
            legal.requireVAT(),
            legal.vatRate(),
            legal.requireDataConsent(),
            legal.privacyPolicyVersion(),
            legal.requireCookieConsent(),
            legal.minimumAge(),
            legal.gdprCountryCodes() != null ? List.copyOf(legal.gdprCountryCodes()) : List.of()
        );
    }
}
