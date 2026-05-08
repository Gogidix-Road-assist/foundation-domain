package com.gogidix.rapidassist.feature.flags.service.application.dto.response;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;

import java.time.Instant;
import java.util.Set;

/**
 * Response DTO for FeatureFlag entities.
 *
 * <p>This DTO is used for API responses and contains only the
 * information that should be exposed to clients.
 */
public record FeatureFlagResponseDto(
    String id,
    String tenantId,
    String key,
    String name,
    String description,
    boolean enabled,
    String type,
    String rolloutStrategy,
    Set<String> allowedTenants,
    Set<String> allowedUsers,
    Set<String> allowedCountries,
    Integer percentage,
    String percentageBucketingKey,
    String environment,
    String status,
    Set<String> tags,
    Boolean requiresApproval,
    Instant expiresAt,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Integer version
) {
    /**
     * Converts a domain FeatureFlag entity to a Response DTO.
     *
     * @param featureFlag the domain entity
     * @return the response DTO
     */
    public static FeatureFlagResponseDto fromDomain(FeatureFlag featureFlag) {
        Integer percentage = null;
        String bucketingKey = null;
        if (featureFlag.percentageRollout() != null) {
            percentage = featureFlag.percentageRollout().percentage();
            bucketingKey = featureFlag.percentageRollout().bucketingKey();
        }

        return new FeatureFlagResponseDto(
            featureFlag.id(),
            featureFlag.tenantId(),
            featureFlag.key(),
            featureFlag.name(),
            featureFlag.description(),
            featureFlag.enabled(),
            featureFlag.type().name(),
            featureFlag.rolloutStrategy().name(),
            featureFlag.allowedTenants(),
            featureFlag.allowedUsers(),
            featureFlag.allowedCountries(),
            percentage,
            bucketingKey,
            featureFlag.environment(),
            featureFlag.status().name(),
            featureFlag.tags(),
            featureFlag.requiresApproval(),
            featureFlag.expiresAt(),
            featureFlag.createdBy(),
            featureFlag.createdAt(),
            featureFlag.updatedBy(),
            featureFlag.updatedAt(),
            featureFlag.version()
        );
    }
}
