package com.gogidix.rapidassist.dynamic.routing.config.service.application.query;

/**
 * Query object for listing multiple Routing Rules.
 *
 * <p>This is a CQRS query object that encapsulates all parameters
 * needed to retrieve a list of routing rule entities.
 *
 * <p>Queries are immutable and support pagination.
 */
public record ListRoutingRulesQuery(
    String tenantId,
    String environment,
    Boolean active,
    Integer minPriority,
    Integer maxPriority,
    String keyword,
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    /**
     * Creates a basic query for a tenant.
     *
     * @param tenantId the tenant ID
     * @return a new ListRoutingRulesQuery
     */
    public static ListRoutingRulesQuery forTenant(String tenantId) {
        return new ListRoutingRulesQuery(
            tenantId, null, null, null, null, null,
            0, 20, "priority", "asc"
        );
    }

    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && page >= 0
            && size > 0 && size <= 100;
    }

    /**
     * Creates a new query with updated pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return a new ListRoutingRulesQuery
     */
    public ListRoutingRulesQuery withPagination(int page, int size) {
        return new ListRoutingRulesQuery(
            tenantId, environment, active, minPriority, maxPriority, keyword,
            page, size, sortBy, sortDirection
        );
    }

    /**
     * Creates a new query with environment filter.
     *
     * @param environment the environment
     * @return a new ListRoutingRulesQuery
     */
    public ListRoutingRulesQuery withEnvironment(String environment) {
        return new ListRoutingRulesQuery(
            tenantId, environment, active, minPriority, maxPriority, keyword,
            page, size, sortBy, sortDirection
        );
    }

    /**
     * Creates a new query with active filter.
     *
     * @param active the active status
     * @return a new ListRoutingRulesQuery
     */
    public ListRoutingRulesQuery withActive(Boolean active) {
        return new ListRoutingRulesQuery(
            tenantId, environment, active, minPriority, maxPriority, keyword,
            page, size, sortBy, sortDirection
        );
    }

    /**
     * Calculates the offset for database queries.
     *
     * @return the offset
     */
    public int getOffset() {
        return page * size;
    }
}
