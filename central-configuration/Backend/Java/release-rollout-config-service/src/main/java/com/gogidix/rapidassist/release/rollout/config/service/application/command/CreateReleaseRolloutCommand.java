package com.gogidix.rapidassist.release.rollout.config.service.application.command;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

import java.util.List;

/**
 * Command object for creating a new ReleaseRollout.
 *
 * <p>This is a CQRS command object that encapsulates all parameters
 * needed to create a new rollout entity.
 *
 * <p>Commands are immutable and self-validating.
 */
public record CreateReleaseRolloutCommand(
    String tenantId,
    String releaseId,
    String version,
    ReleaseRollout.RolloutStrategy strategy,
    RolloutConfigDto config,
    String environment,
    String createdBy,
    String reason
) {
    /**
     * DTO for rollout configuration.
     */
    public record RolloutConfigDto(
        int batchSize,
        int batchIntervalMinutes,
        int initialPercentage,
        int maxPercentage,
        List<String> targetSegments,
        String criteria,
        boolean autoPromote,
        boolean requireApproval
    ) {
        public RolloutConfigDto {
            if (batchSize <= 0) {
                throw new IllegalArgumentException("Batch size must be positive");
            }
            if (batchIntervalMinutes <= 0) {
                throw new IllegalArgumentException("Batch interval must be positive");
            }
        }

        /**
         * Converts to domain RolloutConfig.
         */
        public ReleaseRollout.RolloutConfig toDomain() {
            return new ReleaseRollout.RolloutConfig(
                batchSize,
                batchIntervalMinutes,
                initialPercentage,
                maxPercentage,
                targetSegments != null ? targetSegments : List.of(),
                criteria,
                autoPromote,
                requireApproval
            );
        }
    }

    /**
     * Validates the command parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return tenantId != null && !tenantId.isBlank()
            && releaseId != null && !releaseId.isBlank()
            && version != null && !version.isBlank()
            && strategy != null
            && config != null
            && environment != null && !environment.isBlank()
            && createdBy != null && !createdBy.isBlank();
    }

    /**
     * Creates a validated command with defaults applied.
     *
     * @return a new CreateReleaseRolloutCommand with defaults
     */
    public CreateReleaseRolloutCommand withDefaults() {
        RolloutConfigDto configDto = new RolloutConfigDto(
            this.config.batchSize(),
            this.config.batchIntervalMinutes(),
            this.config.initialPercentage() > 0 ? this.config.initialPercentage() : 10,
            this.config.maxPercentage() > 0 ? this.config.maxPercentage() : 100,
            this.config.targetSegments() != null ? this.config.targetSegments() : List.of("all-users"),
            this.config.criteria(),
            this.config.autoPromote(),
            this.config.requireApproval()
        );

        return new CreateReleaseRolloutCommand(
            this.tenantId,
            this.releaseId,
            this.version,
            this.strategy,
            configDto,
            this.environment,
            this.createdBy,
            this.reason != null ? this.reason : "Initial rollout creation"
        );
    }
}
