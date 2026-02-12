package com.gogidix.rapidassist.country.localization.config.service.application.query;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;

/**
 * Query object for listing CountryLocalizations with filtering.
 *
 * <p>This is a CQRS query object that encapsulates the parameters
 * needed to list and filter localization entities.
 *
 * <p>Queries are immutable and all filter parameters are optional.
 */
public record ListCountryLocalizationsQuery(
    boolean activeOnly,
    CountryLocalization.MeasurementSystem measurementSystem,
    String keyword,
    int page,
    int size
) {
    /**
     * Creates a query with default pagination.
     *
     * @return a new ListCountryLocalizationsQuery with defaults
     */
    public static ListCountryLocalizationsQuery withDefaults() {
        return new ListCountryLocalizationsQuery(false, null, null, 0, 50);
    }

    /**
     * Creates a query for active localizations only.
     *
     * @return a new ListCountryLocalizationsQuery
     */
    public static ListCountryLocalizationsQuery forActiveOnly() {
        return new ListCountryLocalizationsQuery(true, null, null, 0, 50);
    }

    /**
     * Creates a query with measurement system filter.
     *
     * @param measurementSystem the measurement system to filter by
     * @return a new ListCountryLocalizationsQuery
     */
    public static ListCountryLocalizationsQuery byMeasurementSystem(CountryLocalization.MeasurementSystem measurementSystem) {
        return new ListCountryLocalizationsQuery(false, measurementSystem, null, 0, 50);
    }

    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return page >= 0 && size > 0 && size <= 100;
    }
}
