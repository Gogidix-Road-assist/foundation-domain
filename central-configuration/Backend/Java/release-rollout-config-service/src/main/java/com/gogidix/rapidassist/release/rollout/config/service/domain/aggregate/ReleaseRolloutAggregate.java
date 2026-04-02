package com.gogidix.rapidassist.release.rollout.config.service.domain.aggregate;

import com.gogidix.rapidassist.release.rollout.config.service.domain.event.ReleaseRolloutCreatedEvent;
import com.gogidix.rapidassist.release.rollout.config.service.domain.event.ReleaseRolloutDeletedEvent;
import com.gogidix.rapidassist.release.rollout.config.service.domain.event.ReleaseRolloutUpdatedEvent;
import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ReleaseRollout Aggregate Root.
 *
 * <p>This aggregate represents a cluster of domain objects that can be treated as a unit.
 * The aggregate root is the only object that external code can hold references to.
 *
 * <p>This aggregate enforces business rules and invariants around release rollout management:
 * <ul>
 *   <li>Rollouts must be validated before creation</li>
 *   <li>Rollout status changes must follow valid transitions</li>
 *   <li>In-progress rollouts cannot be deleted without rollback</li>
 *   <li>Completed rollouts cannot be modified</li>
 * </ul>
 */
public class ReleaseRolloutAggregate {

    private final ReleaseRollout rollout;
    private final List<ReleaseRolloutUpdatedEvent> pendingEvents;
    private boolean isNew;

    private ReleaseRolloutAggregate(ReleaseRollout rollout, boolean isNew) {
        this.rollout = rollout;
        this.isNew = isNew;
        this.pendingEvents = new ArrayList<>();
    }

    /**
     * Creates a new ReleaseRolloutAggregate for a new rollout.
     *
     * @param tenantId      the tenant ID
     * @param releaseId     the release ID
     * @param version       the version
     * @param strategy      the rollout strategy
     * @param config        the rollout configuration
     * @param environment   the environment
     * @param createdBy     the user creating the rollout
     * @return a new ReleaseRolloutAggregate
     */
    public static ReleaseRolloutAggregate create(
            String tenantId,
            String releaseId,
            String version,
            ReleaseRollout.RolloutStrategy strategy,
            ReleaseRollout.RolloutConfig config,
            String environment,
            String createdBy) {

        // Validate business rules
        validateReleaseId(releaseId);
        validateVersion(version);
        validateStrategy(strategy);
        validateConfig(config, strategy);

        ReleaseRollout newRollout = ReleaseRollout.builder()
            .tenantId(tenantId)
            .releaseId(releaseId)
            .version(version)
            .strategy(strategy)
            .config(config)
            .status(ReleaseRollout.RolloutStatus.PLANNED)
            .environment(environment)
            .createdBy(createdBy)
            .build();

        return new ReleaseRolloutAggregate(newRollout, true);
    }

    /**
     * Reconstructs an existing ReleaseRolloutAggregate from persistence.
     *
     * @param rollout the rollout from persistence
     * @return a ReleaseRolloutAggregate
     */
    public static ReleaseRolloutAggregate fromExisting(ReleaseRollout rollout) {
        return new ReleaseRolloutAggregate(rollout, false);
    }

    /**
     * Updates the rollout status.
     *
     * @param newStatus the new status
     * @param updatedBy the user making the change
     * @param reason    the reason for the change
     * @return the ReleaseRolloutUpdatedEvent if successful
     */
    public ReleaseRolloutUpdatedEvent updateStatus(
            ReleaseRollout.RolloutStatus newStatus,
            String updatedBy,
            String reason) {

        // Business rule: Validate status transitions
        validateStatusTransition(rollout.status(), newStatus);

        ReleaseRollout oldRollout = rollout;
        ReleaseRollout newRollout = ReleaseRollout.builder()
            .id(rollout.id())
            .tenantId(rollout.tenantId())
            .releaseId(rollout.releaseId())
            .version(rollout.version())
            .strategy(rollout.strategy())
            .config(rollout.config())
            .status(newStatus)
            .environment(rollout.environment())
            .createdBy(rollout.createdBy())
            .createdAt(rollout.createdAt())
            .updatedBy(updatedBy)
            .updatedAt(java.time.Instant.now())
            .recordVersion(rollout.recordVersion() + 1)
            .build();

        ReleaseRolloutUpdatedEvent event = new ReleaseRolloutUpdatedEvent(
            oldRollout, newRollout, updatedBy, reason
        );

        pendingEvents.add(event);
        return event;
    }

    /**
     * Marks this rollout for deletion.
     *
     * @param deletedBy the user deleting the rollout
     * @param reason    the reason for deletion
     * @return the ReleaseRolloutDeletedEvent
     */
    public ReleaseRolloutDeletedEvent delete(String deletedBy, String reason) {
        // Business rule: In-progress rollouts cannot be deleted
        if (rollout.status() == ReleaseRollout.RolloutStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "In-progress rollouts cannot be deleted. Rollback first."
            );
        }

        // Business rule: Completed rollouts require audit trail
        if (rollout.status() == ReleaseRollout.RolloutStatus.COMPLETED && reason == null) {
            throw new IllegalArgumentException(
                "Deletion of completed rollouts requires a reason"
            );
        }

        return new ReleaseRolloutDeletedEvent(
            rollout.id(),
            rollout.tenantId(),
            rollout.releaseId(),
            rollout.version(),
            rollout.environment(),
            rollout.recordVersion(),
            deletedBy,
            reason
        );
    }

    /**
     * Validates the current rollout configuration.
     *
     * @return set of validation errors, empty if valid
     */
    public Set<String> validate() {
        Set<String> errors = new java.util.HashSet<>();

        // Validate release ID format
        if (rollout.releaseId() == null || rollout.releaseId().isBlank()) {
            errors.add("Release ID is required");
        }

        // Validate version format
        if (rollout.version() == null || rollout.version().isBlank()) {
            errors.add("Version is required");
        }

        // Validate strategy is set
        if (rollout.strategy() == null) {
            errors.add("Rollout strategy is required");
        }

        // Validate configuration matches strategy
        if (rollout.config() == null) {
            errors.add("Rollout configuration is required");
        }

        // Validate environment
        if (rollout.environment() == null || rollout.environment().isBlank()) {
            errors.add("Environment is required");
        }

        return errors;
    }

    /**
     * Gets the underlying rollout entity.
     *
     * @return the rollout
     */
    public ReleaseRollout rollout() {
        return rollout;
    }

    /**
     * Gets the rollout ID.
     *
     * @return the ID
     */
    public String id() {
        return rollout.id();
    }

    /**
     * Checks if this is a newly created aggregate.
     *
     * @return true if new, false if loaded from persistence
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Gets all pending events that haven't been published yet.
     *
     * @return list of pending events
     */
    public List<ReleaseRolloutUpdatedEvent> getPendingEvents() {
        return new ArrayList<>(pendingEvents);
    }

    /**
     * Clears pending events after they've been published.
     */
    public void clearPendingEvents() {
        pendingEvents.clear();
    }

    // ========================================================================
    // Private validation methods
    // ========================================================================

    private static void validateReleaseId(String releaseId) {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("Release ID cannot be blank");
        }

        if (releaseId.length() > 255) {
            throw new IllegalArgumentException("Release ID cannot exceed 255 characters");
        }
    }

    private static void validateVersion(String version) {
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be blank");
        }

        // Basic semantic version validation
        if (!version.matches("^\\d+\\.\\d+\\.\\d+.*$")) {
            throw new IllegalArgumentException(
                "Version must follow semantic versioning (e.g., 1.0.0)"
            );
        }
    }

    private static void validateStrategy(ReleaseRollout.RolloutStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Rollout strategy is required");
        }
    }

    private static void validateConfig(
            ReleaseRollout.RolloutConfig config,
            ReleaseRollout.RolloutStrategy strategy) {
        if (config == null) {
            throw new IllegalArgumentException("Rollout configuration is required");
        }

        // Strategy-specific validation
        switch (strategy) {
            case CANARY, GRADUAL -> {
                if (config.batchSize() <= 0 || config.batchSize() > 100) {
                    throw new IllegalArgumentException(
                        "Batch size must be between 1 and 100 for " + strategy + " strategy"
                    );
                }
                if (config.batchIntervalMinutes() <= 0) {
                    throw new IllegalArgumentException("Batch interval must be positive");
                }
            }
            case BLUE_GREEN -> {
                // Blue-green doesn't need batch configuration
            }
            case BIG_BANG -> {
                // Big-bang doesn't need gradual rollout config
            }
            case AB_TESTING -> {
                if (config.targetSegments() == null || config.targetSegments().isEmpty()) {
                    throw new IllegalArgumentException(
                        "A/B testing requires target segments"
                    );
                }
            }
        }
    }

    private static void validateStatusTransition(
            ReleaseRollout.RolloutStatus currentStatus,
            ReleaseRollout.RolloutStatus newStatus) {

        // Business rule: Certain transitions are not allowed
        if (currentStatus == ReleaseRollout.RolloutStatus.COMPLETED) {
            throw new IllegalStateException(
                "Completed rollouts cannot change status"
            );
        }

        if (currentStatus == ReleaseRollout.RolloutStatus.ROLLED_BACK) {
            throw new IllegalStateException(
                "Rolled back rollouts cannot change status"
            );
        }

        if (currentStatus == ReleaseRollout.RolloutStatus.FAILED
                && newStatus != ReleaseRollout.RolloutStatus.PLANNED) {
            throw new IllegalStateException(
                "Failed rollouts can only be reset to PLANNED status"
            );
        }

        if (currentStatus == ReleaseRollout.RolloutStatus.IN_PROGRESS
                && newStatus == ReleaseRollout.RolloutStatus.PLANNED) {
            throw new IllegalStateException(
                "In-progress rollouts cannot be reset to PLANNED. Rollback first."
            );
        }
    }
}
