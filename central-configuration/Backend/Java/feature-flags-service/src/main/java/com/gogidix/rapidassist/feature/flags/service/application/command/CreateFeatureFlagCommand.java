package com.gogidix.rapidassist.feature.flags.service.application.command;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.time.Instant;
import java.util.Set;

/**
 * Command object for creating a new FeatureFlag.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new feature flag entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateFeatureFlagCommand(
    String tenantId,
    String key,
    String name,
    String description,
    FeatureFlag.FlagType type,
    FeatureFlag.RolloutStrategy rolloutStrategy,
    Set<String> allowedTenants,
    Set<String> allowedUsers,
    Set<String> allowedCountries,
    FeatureFlag.PercentageRollout percentageRollout,
    String environment,
    Set<String> tags,
    Boolean requiresApproval,
    Instant expiresAt,
    String createdBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && key != null && !key.isBlank()
            && name != null && !name.isBlank()
            && type != null
            && rolloutStrategy != null
            && environment != null && !environment.isBlank()
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateFeatureFlagCommand with defaults
     */
    public CreateFeatureFlagCommand withDefaults() {
        return new CreateFeatureFlagCommand(
            this.tenantId,
            this.key,
            this.name,
            this.description != null ? this.description : "",
            this.type,
            this.rolloutStrategy,
            this.allowedTenants != null ? this.allowedTenants : Set.of(),
            this.allowedUsers != null ? this.allowedUsers : Set.of(),
            this.allowedCountries != null ? this.allowedCountries : Set.of(),
            this.percentageRollout,
            this.environment != null ? this.environment : "production",
            this.tags != null ? this.tags : Set.of(),
            this.requiresApproval != null ? this.requiresApproval : false,
            this.expiresAt,
            this.createdBy,
            this.reason != null ? this.reason : "Initial feature flag creation"
        );
    }
}
