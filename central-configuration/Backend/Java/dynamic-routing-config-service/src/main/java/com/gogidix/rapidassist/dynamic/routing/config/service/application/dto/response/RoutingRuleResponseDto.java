package com.gogidix.rapidassist.dynamic.routing.config.service.application.dto.response;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for Routing Rule entities.
 *
 * <p>This DTO is used for API responses and contains only the
 * information that should be exposed to clients.
 */
public record RoutingRuleResponseDto(
    String id,
    String tenantId,
    String ruleName,
    PatternDto pattern,
    TargetDto target,
    String strategy,
    List<ConditionDto> conditions,
    RouteConfigDto config,
    int priority,
    boolean active,
    String environment,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Integer version
) {
    /**
     * Converts a domain RoutingRule entity to a Response DTO.
     *
     * @param routingRule the domain entity
     * @return the response DTO
     */
    public static RoutingRuleResponseDto fromDomain(RoutingRule routingRule) {
        return new RoutingRuleResponseDto(
            routingRule.id(),
            routingRule.tenantId(),
            routingRule.ruleName(),
            PatternDto.fromDomain(routingRule.pattern()),
            TargetDto.fromDomain(routingRule.target()),
            routingRule.strategy().name(),
            routingRule.conditions().stream()
                .map(ConditionDto::fromDomain)
                .toList(),
            RouteConfigDto.fromDomain(routingRule.config()),
            routingRule.priority(),
            routingRule.active(),
            routingRule.environment(),
            routingRule.createdBy(),
            routingRule.createdAt(),
            routingRule.updatedBy(),
            routingRule.updatedAt(),
            routingRule.version()
        );
    }

    public record PatternDto(
        String type,
        String value,
        List<String> methods
    ) {
        public static PatternDto fromDomain(RoutingRule.RoutePattern pattern) {
            return new PatternDto(
                pattern.type().name(),
                pattern.value(),
                pattern.methods()
            );
        }
    }

    public record TargetDto(
        String type,
        String endpoint,
        Map<String, String> metadata
    ) {
        public static TargetDto fromDomain(RoutingRule.RouteTarget target) {
            return new TargetDto(
                target.type(),
                target.endpoint(),
                target.metadata()
            );
        }
    }

    public record ConditionDto(
        String type,
        String key,
        String operator,
        String value
    ) {
        public static ConditionDto fromDomain(RoutingRule.Condition condition) {
            return new ConditionDto(
                condition.type().name(),
                condition.key(),
                condition.operator(),
                condition.value()
            );
        }
    }

    public record RouteConfigDto(
        int timeoutMs,
        int retryAttempts,
        boolean circuitBreakerEnabled,
        double circuitBreakerThreshold,
        boolean rateLimitEnabled,
        int rateLimitPerSecond
    ) {
        public static RouteConfigDto fromDomain(RoutingRule.RouteConfig config) {
            return new RouteConfigDto(
                config.timeoutMs(),
                config.retryAttempts(),
                config.circuitBreakerEnabled(),
                config.circuitBreakerThreshold(),
                config.rateLimitEnabled(),
                config.rateLimitPerSecond()
            );
        }
    }
}
