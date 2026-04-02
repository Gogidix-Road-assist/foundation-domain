package com.gogidix.rapidassist.policy.configuration.service.application.dto.response;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for Policy entities.
 *
 * <p>This DTO is used for API responses and contains only the
 * information that should be exposed to clients.
 */
public record PolicyResponseDto(
    String id,
    String tenantId,
    String policyKey,
    String name,
    String description,
    String type,
    Map<String, Object> scope,
    Map<String, Object> rules,
    Map<String, Object> constraints,
    boolean enforced,
    int priority,
    String environment,
    String status,
    Set<String> tags,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Integer version
) {
    /**
     * Converts a domain Policy entity to a Response DTO.
     *
     * @param policy the domain entity
     * @return the response DTO
     */
    public static PolicyResponseDto fromDomain(Policy policy) {
        return new PolicyResponseDto(
            policy.id(),
            policy.tenantId(),
            policy.policyKey(),
            policy.name(),
            policy.description(),
            policy.type().name(),
            scopeToMap(policy.scope()),
            policy.rules(),
            constraintsToMap(policy.constraints()),
            policy.enforced(),
            policy.priority(),
            policy.environment(),
            policy.status().name(),
            policy.tags(),
            policy.createdBy(),
            policy.createdAt(),
            policy.updatedBy(),
            policy.updatedAt(),
            policy.version()
        );
    }

    private static Map<String, Object> scopeToMap(Policy.PolicyScope scope) {
        if (scope == null) {
            return Map.of();
        }
        return Map.of(
            "type", scope.type().name(),
            "entityTypes", scope.entityTypes(),
            "entityIds", scope.entityIds(),
            "attributes", scope.attributes()
        );
    }

    private static Map<String, Object> constraintsToMap(Policy.PolicyConstraints constraints) {
        if (constraints == null) {
            return Map.of();
        }
        return Map.of(
            "maxRetries", constraints.maxRetries(),
            "timeoutMs", constraints.timeoutMs(),
            "requireApproval", constraints.requireApproval(),
            "requiredRoles", constraints.requiredRoles()
        );
    }
}
