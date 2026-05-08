package com.gogidix.rapidassist.release.rollout.config.service.application.dto.response;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for ReleaseRollout entities.
 */
public record ReleaseRolloutResponseDto(
    String id,
    String tenantId,
    String releaseId,
    String version,
    ReleaseRollout.RolloutStrategy strategy,
    RolloutConfigDto config,
    ReleaseRollout.RolloutStatus status,
    String environment,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Integer recordVersion
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
        /**
         * Creates DTO from domain RolloutConfig.
         */
        public static RolloutConfigDto fromDomain(ReleaseRollout.RolloutConfig config) {
            return new RolloutConfigDto(
                config.batchSize(),
                config.batchIntervalMinutes(),
                config.initialPercentage(),
                config.maxPercentage(),
                config.targetSegments(),
                config.criteria(),
                config.autoPromote(),
                config.requireApproval()
            );
        }
    }

    /**
     * Creates a response DTO from a domain entity.
     *
     * @param rollout the domain entity
     * @return the response DTO
     */
    public static ReleaseRolloutResponseDto fromDomain(ReleaseRollout rollout) {
        return new ReleaseRolloutResponseDto(
            rollout.id(),
            rollout.tenantId(),
            rollout.releaseId(),
            rollout.version(),
            rollout.strategy(),
            RolloutConfigDto.fromDomain(rollout.config()),
            rollout.status(),
            rollout.environment(),
            rollout.createdBy(),
            rollout.createdAt(),
            rollout.updatedBy(),
            rollout.updatedAt(),
            rollout.recordVersion()
        );
    }
}
