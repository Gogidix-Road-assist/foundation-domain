package com.gogidix.rapidassist.country.localization.config.service.application.query;

/**
 * Query object for retrieving a CountryLocalization.
 *
 * <p>This is a CQRS query object that encapsulates the parameters
 * needed to retrieve a localization entity.
 *
 * <p>Queries are immutable and can be used with either ID or country code.
 */
public record GetCountryLocalizationQuery(
    String id,
    String countryCode
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return (id != null && !id.isBlank()) || (countryCode != null && !countryCode.isBlank());
    }

    /**
     * Creates a query by ID.
     *
     * @param id the localization ID
     * @return a new GetCountryLocalizationQuery
     */
    public static GetCountryLocalizationQuery byId(String id) {
        return new GetCountryLocalizationQuery(id, null);
    }

    /**
     * Creates a query by country code.
     *
     * @param countryCode the country code
     * @return a new GetCountryLocalizationQuery
     */
    public static GetCountryLocalizationQuery byCountryCode(String countryCode) {
        return new GetCountryLocalizationQuery(null, countryCode);
    }
}
