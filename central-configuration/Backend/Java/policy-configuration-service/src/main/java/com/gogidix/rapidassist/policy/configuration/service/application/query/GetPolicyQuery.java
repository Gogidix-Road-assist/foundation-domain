package com.gogidix.rapidassist.policy.configuration.service.application.query;

/**
 * Query object for retrieving a single Policy.
 *
 * <p>This is a CQRS query object that encapsulates parameters
 * needed to retrieve a policy entity.
 *
 * <p>Queries are immutable and can be cached.
 */
public record GetPolicyQuery(
    String tenantId,
    String policyId,
    String policyKey,
    String environment
) {
    /**
     * Checks if this is a query by ID.
     *
     * @return true if querying by ID
     */
    public boolean isById() {
        return policyId != null && !policyId.isBlank();
    }

    /**
     * Checks if this is a query by natural key.
     *
     * @return true if querying by natural key
     */
    public boolean isByNaturalKey() {
        return !isById()
            && policyKey != null && !policyKey.isBlank()
            && environment != null && !environment.isBlank();
    }

    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return isById() || isByNaturalKey();
    }
}
