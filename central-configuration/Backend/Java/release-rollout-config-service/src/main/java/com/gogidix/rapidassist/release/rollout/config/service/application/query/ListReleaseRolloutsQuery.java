package com.gogidix.rapidassist.release.rollout.config.service.application.query;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

/**
 * Query object for listing ReleaseRollouts.
 *
 * <p>This is a CQRS query object that encapsulates all parameters
 * needed to list rollout entities.
 *
 * <p>Queries are immutable.
 */
public record ListReleaseRolloutsQuery(
    String tenantId,
    String releaseId,
    ReleaseRollout.RolloutStatus status,
    ReleaseRollout.RolloutStrategy strategy,
    String environment,
    Integer pageNumber,
    Integer pageSize
) {
    /**
     * Creates a basic tenant-scoped query.
     *
     * @param tenantId the tenant ID
     * @return a new ListReleaseRolloutsQuery
     */
    public static ListReleaseRolloutsQuery forTenant(String tenantId) {
        return new ListReleaseRolloutsQuery(
            tenantId,
            null,
            null,
            null,
            null,
            0,
            20
        );
    }

    /**
     * Creates a query with pagination.
     *
     * @param tenantId   the tenant ID
     * @param pageNumber the page number (0-based)
     * @param pageSize   the page size
     * @return a new ListReleaseRolloutsQuery
     */
    public static ListReleaseRolloutsQuery forTenantPaginated(
            String tenantId,
            int pageNumber,
            int pageSize) {
        return new ListReleaseRolloutsQuery(
            tenantId,
            null,
            null,
            null,
            null,
            pageNumber,
            pageSize
        );
    }

    /**
     * Validates the query parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && (pageNumber == null || pageNumber >= 0)
            && (pageSize == null || (pageSize > 0 && pageSize <= 100));
    }

    /**
     * Returns a validated query with defaults applied.
     *
     * @return a new ListReleaseRolloutsQuery with defaults
     */
    public ListReleaseRolloutsQuery withDefaults() {
        return new ListReleaseRolloutsQuery(
            this.tenantId,
            this.releaseId,
            this.status,
            this.strategy,
            this.environment,
            this.pageNumber != null ? this.pageNumber : 0,
            this.pageSize != null ? this.pageSize : 20
        );
    }

    /**
     * Creates a new query filtered by release ID.
     *
     * @param releaseId the release ID
     * @return a new ListReleaseRolloutsQuery
     */
    public ListReleaseRolloutsQuery withReleaseId(String releaseId) {
        return new ListReleaseRolloutsQuery(
            this.tenantId,
            releaseId,
            this.status,
            this.strategy,
            this.environment,
            this.pageNumber,
            this.pageSize
        );
    }

    /**
     * Creates a new query filtered by status.
     *
     * @param status the status
     * @return a new ListReleaseRolloutsQuery
     */
    public ListReleaseRolloutsQuery withStatus(ReleaseRollout.RolloutStatus status) {
        return new ListReleaseRolloutsQuery(
            this.tenantId,
            this.releaseId,
            status,
            this.strategy,
            this.environment,
            this.pageNumber,
            this.pageSize
        );
    }

    /**
     * Creates a new query filtered by strategy.
     *
     * @param strategy the strategy
     * @return a new ListReleaseRolloutsQuery
     */
    public ListReleaseRolloutsQuery withStrategy(ReleaseRollout.RolloutStrategy strategy) {
        return new ListReleaseRolloutsQuery(
            this.tenantId,
            this.releaseId,
            this.status,
            strategy,
            this.environment,
            this.pageNumber,
            this.pageSize
        );
    }

    /**
     * Creates a new query filtered by environment.
     *
     * @param environment the environment
     * @return a new ListReleaseRolloutsQuery
     */
    public ListReleaseRolloutsQuery withEnvironment(String environment) {
        return new ListReleaseRolloutsQuery(
            this.tenantId,
            this.releaseId,
            this.status,
            this.strategy,
            environment,
            this.pageNumber,
            this.pageSize
        );
    }
}
