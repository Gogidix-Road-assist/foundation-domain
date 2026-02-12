package com.gogidix.rapidassist.dynamic.routing.config.service.domain.policy;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Domain policy for validating routing rules.
 *
 * <p>This policy encapsulates business rules for routing rule validation:
 * <ul>
 *   <li>Rules must have valid patterns and targets</li>
 *   <li>Production environment rules require additional validation</li>
 *   <li>High priority rules require special approval</li>
 *   <li>Critical paths need circuit breaker enabled</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any routing rule.
 */
public class RoutingRuleValidationPolicy {

    // Pattern for validating endpoint URLs
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://[^\\s/$.?#].[^\\s]*|/[^\\s]*)$"
    );

    // Protected route patterns that require special approval
    private static final Set<String> PROTECTED_PATTERNS = Set.of(
        "/api/v1/auth/*",
        "/api/v1/admin/*",
        "/api/v1/payments/*"
    );

    private final boolean requireApprovalForProtectedRoutes;
    private final boolean enforceCircuitBreakerForCritical;

    private RoutingRuleValidationPolicy(Builder builder) {
        this.requireApprovalForProtectedRoutes = builder.requireApprovalForProtectedRoutes;
        this.enforceCircuitBreakerForCritical = builder.enforceCircuitBreakerForCritical;
    }

    /**
     * Validates a routing rule according to this policy.
     *
     * @param routingRule the routing rule to validate
     * @return validation result
     */
    public ValidationResult validate(RoutingRule routingRule) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Validate rule name
        if (routingRule.ruleName() == null || routingRule.ruleName().isBlank()) {
            errors.add("Routing rule name is required");
        }

        // Rule 2: Validate pattern
        if (routingRule.pattern() == null) {
            errors.add("Routing pattern is required");
        } else if (routingRule.pattern().value() == null || routingRule.pattern().value().isBlank()) {
            errors.add("Routing pattern value cannot be blank");
        }

        // Rule 3: Validate target endpoint
        if (routingRule.target() == null || routingRule.target().endpoint() == null) {
            errors.add("Routing target endpoint is required");
        } else {
            String endpoint = routingRule.target().endpoint();
            if (!URL_PATTERN.matcher(endpoint).matches()) {
                errors.add("Routing target endpoint must be a valid URL or path");
            }
        }

        // Rule 4: Validate priority range
        if (routingRule.priority() < 0 || routingRule.priority() > 1000) {
            errors.add("Routing rule priority must be between 0 and 1000");
        }

        // Rule 5: Critical routes must have circuit breaker enabled
        if (enforceCircuitBreakerForCritical && isCriticalRoute(routingRule)) {
            if (!routingRule.config().circuitBreakerEnabled()) {
                errors.add("Critical routing rules must have circuit breaker enabled");
            }
        }

        // Rule 6: Protected routes require approval
        if (requireApprovalForProtectedRoutes && isProtectedRoute(routingRule)) {
            warnings.add(
                "Routing rule for protected path '" + routingRule.pattern().value() +
                "' requires approval for modification"
            );
        }

        // Rule 7: Production environments require additional validation
        if ("production".equalsIgnoreCase(routingRule.environment()) ||
            "prod".equalsIgnoreCase(routingRule.environment())) {
            validateProductionRequirements(routingRule, warnings);
        }

        // Rule 8: High priority rules need conditions
        if (routingRule.priority() < 10 && routingRule.conditions().isEmpty()) {
            warnings.add(
                "High priority routing rules (priority < 10) should have conditions defined"
            );
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates a routing rule change request.
     *
     * @param oldRule      the existing routing rule (null for new rules)
     * @param newTarget    the new target endpoint
     * @param newPriority  the new priority
     * @param changedBy    the user making the change
     * @return validation result
     */
    public ChangeValidationResult validateChange(
            RoutingRule oldRule,
            String newTarget,
            int newPriority,
            String changedBy) {

        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Cannot change priority of active critical rules without approval
        if (oldRule != null && oldRule.active() && oldRule.priority() != newPriority) {
            if (oldRule.priority() < 10) {
                warnings.add(
                    "Priority change for active high-priority rule requires approval"
                );
            }
        }

        // Rule 2: Protected routes require approval for changes
        if (oldRule != null && isProtectedRoute(oldRule)) {
            warnings.add(
                "Modification of protected routing rule '" + oldRule.ruleName() +
                "' requires approval"
            );
        }

        // Rule 3: Validate new target if provided
        if (newTarget != null && !URL_PATTERN.matcher(newTarget).matches()) {
            errors.add("New target endpoint must be a valid URL or path");
        }

        // Rule 4: Validate new priority range
        if (newPriority < 0 || newPriority > 1000) {
            errors.add("New priority must be between 0 and 1000");
        }

        return new ChangeValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Checks if a routing rule is for a critical route.
     *
     * @param routingRule the routing rule
     * @return true if critical
     */
    public boolean isCriticalRoute(RoutingRule routingRule) {
        return routingRule.priority() < 10 ||
               routingRule.pattern().value().startsWith("/api/v1/auth") ||
               routingRule.pattern().value().startsWith("/api/v1/payments");
    }

    /**
     * Checks if a routing rule is for a protected route.
     *
     * @param routingRule the routing rule
     * @return true if protected
     */
    public boolean isProtectedRoute(RoutingRule routingRule) {
        return PROTECTED_PATTERNS.stream()
            .anyMatch(pattern -> matchesPattern(routingRule.pattern().value(), pattern));
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private void validateProductionRequirements(RoutingRule routingRule, Set<String> warnings) {
        // Production rules should have conditions
        if (routingRule.conditions().isEmpty()) {
            warnings.add("Production routing rules should have conditions defined");
        }

        // Production rules should have circuit breaker
        if (!routingRule.config().circuitBreakerEnabled()) {
            warnings.add("Production routing rules should have circuit breaker enabled");
        }

        // Production rules should have reasonable timeout
        if (routingRule.config().timeoutMs() < 1000 || routingRule.config().timeoutMs() > 60000) {
            warnings.add("Production routing rules should have timeout between 1000ms and 60000ms");
        }

        // Production rules should have retry attempts configured
        if (routingRule.config().retryAttempts() < 0 || routingRule.config().retryAttempts() > 5) {
            warnings.add("Production routing rules should have retry attempts between 0 and 5");
        }
    }

    private boolean matchesPattern(String path, String pattern) {
        // Simple wildcard matching
        String regex = pattern.replace("*", ".*");
        return path.matches(regex);
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RoutingRuleValidationPolicy existing) {
        return new Builder()
            .requireApprovalForProtectedRoutes(existing.requireApprovalForProtectedRoutes)
            .enforceCircuitBreakerForCritical(existing.enforceCircuitBreakerForCritical);
    }

    public static class Builder {
        private boolean requireApprovalForProtectedRoutes = true;
        private boolean enforceCircuitBreakerForCritical = true;

        public Builder requireApprovalForProtectedRoutes(boolean value) {
            this.requireApprovalForProtectedRoutes = value;
            return this;
        }

        public Builder enforceCircuitBreakerForCritical(boolean value) {
            this.enforceCircuitBreakerForCritical = value;
            return this;
        }

        public RoutingRuleValidationPolicy build() {
            return new RoutingRuleValidationPolicy(this);
        }
    }

    // ========================================================================
    // Result classes
    // ========================================================================

    public record ValidationResult(
        boolean isValid,
        Set<String> errors,
        Set<String> warnings
    ) {
        public ValidationResult {
            errors = Set.copyOf(errors);
            warnings = Set.copyOf(warnings);
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, Set.of(), Set.of());
        }

        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, Set.of(error), Set.of());
        }
    }

    public record ChangeValidationResult(
        boolean isValid,
        Set<String> errors,
        Set<String> warnings
    ) {
        public ChangeValidationResult {
            errors = Set.copyOf(errors);
            warnings = Set.copyOf(warnings);
        }

        public static ChangeValidationResult valid() {
            return new ChangeValidationResult(true, Set.of(), Set.of());
        }
    }
}
