package com.gogidix.rapidassist.config.service.application.query;

import java.util.Set;

/**
 * Query object for listing configurations with optional filters.
 *
 * <p>This query object supports pagination and filtering for
 * listing configurations.
 */
public record ListConfigurationsQuery(
    String tenantId,
    String environment,
    String namespace,
    Set<String> tags,
    String keyword,
    Integer page,
    Integer size,
    String sortBy,
    String sortDirection
) {
    /**
     * Creates a new query with defaults applied.
     *
     * @return a new ListConfigurationsQuery with defaults
     */
    public ListConfigurationsQuery withDefaults() {
        return new ListConfigurationsQuery(
            this.tenantId,
            this.environment,
            this.namespace,
            this.tags,
            this.keyword,
            this.page != null ? this.page : 0,
            this.size != null ? this.size : 20,
            this.sortBy != null ? this.sortBy : "configKey",
            this.sortDirection != null ? this.sortDirection : "ASC"
        );
    }

    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && (page == null || page >= 0)
            && (size == null || (size > 0 && size <= 100));
    }
}
