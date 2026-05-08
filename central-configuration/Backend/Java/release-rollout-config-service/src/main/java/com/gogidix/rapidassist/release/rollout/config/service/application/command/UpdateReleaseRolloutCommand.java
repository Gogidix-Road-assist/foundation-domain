package com.gogidix.rapidassist.release.rollout.config.service.application.command;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

/**
 * Command object for updating a ReleaseRollout status.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to update a rollout's status.
 *
 * <p>Commands are immutable and self-validating.
 */
public record UpdateReleaseRolloutCommand(
    String id,
    ReleaseRollout.RolloutStatus newStatus,
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
            && newStatus != null
            && updatedBy != null && !updatedBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new UpdateReleaseRolloutCommand with defaults
     */
    public UpdateReleaseRolloutCommand withDefaults() {
        return new UpdateReleaseRolloutCommand(
            this.id,
            this.newStatus,
            this.updatedBy,
            this.reason != null ? this.reason : "Status update"
        );
    }
}
