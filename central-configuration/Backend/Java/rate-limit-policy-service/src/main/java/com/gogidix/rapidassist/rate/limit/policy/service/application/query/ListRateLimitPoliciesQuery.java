package com.gogidix.rapidassist.rate.limit.policy.service.application.query;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

/**
 * Query object for listing Rate Limit Policies.
 *
 * <p>This is a CQRS query object that encapsulates parameters
 * for listing rate limit policies with filtering and pagination.
 */
public record ListRateLimitPoliciesQuery(
    String tenantId,
    String environment,
    RateLimitPolicy.LimitType limitType,
    Boolean enabled,
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    /**
     * Creates a query with default values.
     *
     * @param tenantId the tenant ID (required)
     * @return a new ListRateLimitPoliciesQuery with defaults
     */
    public static ListRateLimitPoliciesQuery withDefaults(String tenantId) {
        return new ListRateLimitPoliciesQuery(
            tenantId,
            null,
            null,
            null,
            0,
            20,
            "createdAt",
            "desc"
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
}
