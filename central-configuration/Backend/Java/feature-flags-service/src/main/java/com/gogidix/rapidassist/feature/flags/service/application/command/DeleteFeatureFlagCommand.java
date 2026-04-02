package com.gogidix.rapidassist.feature.flags.service.application.command;

/**
 * Command object for deleting a FeatureFlag.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete a feature flag entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeleteFeatureFlagCommand(
    String id,
    String deletedBy,
    String reason
) {
    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.isBlank()
            && deletedBy != null && !deletedBy.isBlank();
    }
}
