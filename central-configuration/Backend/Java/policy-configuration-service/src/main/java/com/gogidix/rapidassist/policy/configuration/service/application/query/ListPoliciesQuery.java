package com.gogidix.rapidassist.policy.configuration.service.application.query;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.util.Set;

/**
 * Query object for listing and filtering Policies.
 *
 * <p>This is a CQRS query object that encapsulates parameters
 * needed to retrieve a list of policy entities.
 *
 * <p>Queries are immutable and can be cached.
 */
public record ListPoliciesQuery(
    String tenantId,
    Policy.PolicyType type,
    Policy.PolicyStatus status,
    Boolean enforced,
    String environment,
    Set<String> tags,
    String keyword,
    Integer page,
    Integer size,
    String sortBy,
    String sortDirection
) {
    /**
     * Creates a new ListPoliciesQuery with defaults applied.
     *
     * @return a new ListPoliciesQuery with defaults
     */
    public ListPoliciesQuery withDefaults() {
        return new ListPoliciesQuery(
            this.tenantId,
            this.type,
            this.status,
            this.enforced,
            this.environment,
            this.tags,
            this.keyword,
            this.page != null ? this.page : 0,
            this.size != null ? this.size : 20,
            this.sortBy != null ? this.sortBy : "createdAt",
            this.sortDirection != null ? this.sortDirection : "DESC"
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
