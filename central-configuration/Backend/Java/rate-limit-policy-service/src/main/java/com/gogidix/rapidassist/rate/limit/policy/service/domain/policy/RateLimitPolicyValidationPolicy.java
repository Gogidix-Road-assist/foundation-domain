package com.gogidix.rapidassist.rate.limit.policy.service.domain.policy;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;

import java.util.Set;

/**
 * Validation policy for Rate Limit Policy entities.
 *
 * <p>This policy encapsulates business rules for validating rate limit policies
 * before they are created or updated.
 */
public class RateLimitPolicyValidationPolicy {

    /**
     * Validates a rate limit policy according to business rules.
     *
     * @param policy the policy to validate
     * @return set of validation error messages, empty if valid
     */
    public static Set<String> validate(RateLimitPolicy policy) {
        Set<String> errors = new java.util.HashSet<>();

        if (policy == null) {
            errors.add("Policy cannot be null");
            return errors;
        }

        // Validate tenant ID
        if (policy.tenantId() == null || policy.tenantId().isBlank()) {
            errors.add("Tenant ID is required");
        }

        // Validate policy key
        if (policy.policyKey() == null || policy.policyKey().isBlank()) {
            errors.add("Policy key is required");
        } else if (!policy.policyKey().matches("^[a-zA-Z0-9._-]+$")) {
            errors.add("Policy key must contain only alphanumeric characters, dots, hyphens, and underscores");
        } else if (policy.policyKey().length() > 255) {
            errors.add("Policy key cannot exceed 255 characters");
        }

        // Validate name
        if (policy.name() == null || policy.name().isBlank()) {
            errors.add("Policy name is required");
        } else if (policy.name().length() > 255) {
            errors.add("Policy name cannot exceed 255 characters");
        }

        // Validate limit type
        if (policy.limitType() == null) {
            errors.add("Limit type is required");
        }

        // Validate configuration
        if (policy.config() == null) {
            errors.add("Rate limit configuration is required");
        } else {
            validateConfiguration(policy.config(), errors);
        }

        // Validate scope
        if (policy.scope() == null) {
            errors.add("Policy scope is required");
        }

        // Validate environment
        if (policy.environment() == null || policy.environment().isBlank()) {
            errors.add("Environment is required");
        }

        return errors;
    }

    /**
     * Validates the rate limit configuration.
     *
     * @param config the configuration to validate
     * @param errors the error set to add errors to
     */
    private static void validateConfiguration(RateLimitPolicy.RateLimitConfig config, Set<String> errors) {
        if (config.requestsPerMinute() <= 0) {
            errors.add("Requests per minute must be positive");
        }

        if (config.requestsPerHour() <= 0) {
            errors.add("Requests per hour must be positive");
        }

        if (config.requestsPerDay() <= 0) {
            errors.add("Requests per day must be positive");
        }

        if (config.burstCapacity() <= 0) {
            errors.add("Burst capacity must be positive");
        }

        if (config.windowSizeMs() <= 0) {
            errors.add("Window size must be positive");
        }

        if (config.algorithm() == null || config.algorithm().isBlank()) {
            errors.add("Algorithm cannot be blank");
        }

        // Validate allowed algorithms
        Set<String> validAlgorithms = Set.of(
            "token-bucket", "leaky-bucket", "fixed-window", "sliding-window", "sliding-log"
        );

        if (config.algorithm() != null && !validAlgorithms.contains(config.algorithm())) {
            errors.add("Algorithm must be one of: token-bucket, leaky-bucket, fixed-window, sliding-window, sliding-log");
        }
    }

    /**
     * Validates that a policy can be safely deleted.
     *
     * @param policy the policy to check for deletion
     * @return true if the policy can be deleted, false otherwise
     */
    public static boolean canDelete(RateLimitPolicy policy) {
        if (policy == null) {
            return false;
        }

        // Global policies cannot be deleted
        if (policy.limitType() == RateLimitPolicy.LimitType.GLOBAL) {
            return false;
        }

        // Enabled policies cannot be deleted
        if (policy.enabled()) {
            return false;
        }

        return true;
    }
}
