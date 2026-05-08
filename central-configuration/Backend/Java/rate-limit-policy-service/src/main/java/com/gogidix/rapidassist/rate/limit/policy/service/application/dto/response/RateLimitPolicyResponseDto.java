package com.gogidix.rapidassist.rate.limit.policy.service.application.dto.response;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for Rate Limit Policy.
 *
 * <p>This DTO is used to return policy information through the API layer.
 */
public record RateLimitPolicyResponseDto(
    String id,
    String tenantId,
    String policyKey,
    String name,
    String description,
    String limitType,
    RateLimitConfigDto config,
    ScopeDto scope,
    boolean enabled,
    String environment,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Integer version
) {
    public record RateLimitConfigDto(
        int requestsPerMinute,
        int requestsPerHour,
        int requestsPerDay,
        int burstCapacity,
        long windowSizeMs,
        String algorithm
    ) {}

    public record ScopeDto(
        Set<String> endpoints,
        Set<String> userIds,
        Set<String> apiKeys,
        Map<String, String> attributes
    ) {}
}
