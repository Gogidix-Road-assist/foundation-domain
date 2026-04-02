package com.gogidix.rapidassist.feature.flags.service.application.command;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.time.Instant;
import java.util.Set;

/**
 * Command object for updating an existing FeatureFlag.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to update a feature flag entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record UpdateFeatureFlagCommand(
    String id,
    String name,
    String description,
    boolean enabled,
    FeatureFlag.FlagStatus status,
    FeatureFlag.RolloutStrategy rolloutStrategy,
    Set<String> allowedTenants,
    Set<String> allowedUsers,
    Set<String> allowedCountries,
    FeatureFlag.PercentageRollout percentageRollout,
    Set<String> tags,
    Boolean requiresApproval,
    Instant expiresAt,
    String updatedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank()
            && updatedBy != null && !updatedBy.isBlank();
    }
}
