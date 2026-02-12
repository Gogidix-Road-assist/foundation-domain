package com.gogidix.rapidassist.release.rollout.config.service.application.query;

/**
 * Query object for retrieving a single ReleaseRollout.
 *
 * <p>This is a CQRS query object that encapsulates all parameters
 * needed to retrieve a rollout entity.
 *
 * <p>Queries are immutable.
 */
public record GetReleaseRolloutQuery(
    String id
) {
    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank();
    }

    /**
     * Creates a query by natural key.
     *
     * @param tenantId    the tenant ID
     * @param releaseId   the release ID
     * @param version     the version
     * @param environment the environment
     * @return a GetReleaseRolloutQuery for natural key lookup
     */
    public static GetReleaseRolloutQuery byNaturalKey(
            String tenantId,
            String releaseId,
            String version,
            String environment) {
        return new GetReleaseRolloutQuery(
            tenantId + ":" + releaseId + ":" + version + ":" + environment
        );
    }
}
