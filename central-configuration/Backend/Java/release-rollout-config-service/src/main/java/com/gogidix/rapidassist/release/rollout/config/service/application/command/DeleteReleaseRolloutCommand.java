package com.gogidix.rapidassist.release.rollout.config.service.application.command;

/**
 * Command object for deleting a ReleaseRollout.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to delete a rollout entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record DeleteReleaseRolloutCommand(
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

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new DeleteReleaseRolloutCommand with defaults
     */
    public DeleteReleaseRolloutCommand withDefaults() {
        return new DeleteReleaseRolloutCommand(
            this.id,
            this.deletedBy,
            this.reason != null ? this.reason : "Rollout deletion"
        );
    }
}
