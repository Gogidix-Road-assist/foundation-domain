package com.gogidix.rapidassist.tenancy.configuration.service.application.query;

/**
 * Query object for listing tenant configurations with pagination.
 *
 * <p>This query object encapsulates the parameters needed to retrieve
 * a paginated list of tenant configurations.
 */
public record ListTenantConfigsQuery(
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return page >= 0 && size > 0 && size <= 100;
    }

    /**
     * Creates a validated query with defaults applied.
     *
     * @return a new ListTenantConfigsQuery with defaults
     */
    public ListTenantConfigsQuery withDefaults() {
        return new ListTenantConfigsQuery(
            this.page >= 0 ? this.page : 0,
            this.size > 0 && this.size <= 100 ? this.size : 20,
            this.sortBy != null ? this.sortBy : "name",
            this.sortDirection != null ? this.sortDirection : "asc"
        );
    }
}
