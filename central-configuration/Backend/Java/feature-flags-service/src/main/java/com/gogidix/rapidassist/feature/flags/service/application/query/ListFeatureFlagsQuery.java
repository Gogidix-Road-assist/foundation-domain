package com.gogidix.rapidassist.feature.flags.service.application.query;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.util.Set;

/**
 * Query object for listing FeatureFlags with filters.
 *
 * <p>This is a CQRS query object that encapsulates parameters
 * for searching and filtering feature flags.
 */
public record ListFeatureFlagsQuery(
    String tenantId,
    String environment,
    FeatureFlag.FlagStatus status,
    FeatureFlag.FlagType type,
    FeatureFlag.RolloutStrategy rolloutStrategy,
    Boolean enabled,
    Set<String> tags,
    String keyword,
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    /**
     * Creates a query with defaults applied.
     *
     * @return a new ListFeatureFlagsQuery with defaults
     */
    public ListFeatureFlagsQuery withDefaults() {
        return new ListFeatureFlagsQuery(
            this.tenantId,
            this.environment,
            this.status,
            this.type,
            this.rolloutStrategy,
            this.enabled,
            this.tags,
            this.keyword,
            this.page > 0 ? this.page : 0,
            this.size > 0 ? this.size : 20,
            this.sortBy != null ? this.sortBy : "updatedAt",
            this.sortDirection != null ? this.sortDirection : "desc"
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
