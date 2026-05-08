package com.gogidix.rapidassist.feature.flags.service.application.query;

/**
 * Query object for retrieving a single FeatureFlag.
 *
 * <p>This is a CQRS query object that encapsulates parameters
 * for fetching a feature flag by ID or natural key.
 */
public record GetFeatureFlagQuery(
    String id,
    String tenantId,
    String key,
    String environment
) {
    /**
     * Validates that either ID or natural key is provided.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        boolean hasId = id != null && !id.isBlank();
        boolean hasNaturalKey = tenantId != null && !tenantId.isBlank()
            && key != null && !key.isBlank()
            && environment != null && !environment.isBlank();
        return hasId || hasNaturalKey;
    }
}
