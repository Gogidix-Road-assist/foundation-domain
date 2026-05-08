package com.gogidix.rapidassist.dynamic.routing.config.service.application.query;

/**
 * Query object for retrieving a single Routing Rule.
 *
 * <p>This is a CQRS query object that encapsulates all parameters
 * needed to retrieve a routing rule entity.
 *
 * <p>Queries are immutable and self-validating.
 */
public record GetRoutingRuleQuery(
    String id,
    String tenantId,
    String ruleName,
    String environment
) {
    /**
     * Creates a query by ID.
     *
     * @param id the routing rule ID
     * @param tenantId the tenant ID
     * @return a new GetRoutingRuleQuery
     */
    public static GetRoutingRuleQuery byId(String id, String tenantId) {
        return new GetRoutingRuleQuery(id, tenantId, null, null);
    }

    /**
     * Creates a query by natural key.
     *
     * @param tenantId the tenant ID
     * @param ruleName the rule name
     * @param environment the environment
     * @return a new GetRoutingRuleQuery
     */
    public static GetRoutingRuleQuery byNaturalKey(String tenantId, String ruleName, String environment) {
        return new GetRoutingRuleQuery(null, tenantId, ruleName, environment);
    }

    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && ((id != null && !id.isBlank()) || (ruleName != null && !ruleName.isBlank() && environment != null));
    }

    /**
     * Checks if this is a query by ID.
     *
     * @return true if querying by ID
     */
    public boolean isById() {
        return id != null && !id.isBlank();
    }

    /**
     * Checks if this is a query by natural key.
     *
     * @return true if querying by natural key
     */
    public boolean isByNaturalKey() {
        return ruleName != null && !ruleName.isBlank() && environment != null;
    }
}
