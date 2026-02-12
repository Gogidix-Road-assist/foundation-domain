package com.gogidix.rapidassist.rate.limit.policy.service.application.query;

/**
 * Query object for retrieving a single Rate Limit Policy.
 *
 * <p>This is a CQRS query object that encapsulates parameters
 * for retrieving a rate limit policy.
 */
public record GetRateLimitPolicyQuery(
    String policyId,
    String tenantId,
    String policyKey
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return (policyId != null && !policyId.isBlank())
            || (tenantId != null && !tenantId.isBlank() && policyKey != null && !policyKey.isBlank());
    }
}
