package com.gogidix.rapidassist.policy.configuration.service.domain.policy;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Domain policy for validating policy configurations.
 *
 * <p>This policy encapsulates business rules for policy validation:
 * <ul>
 *   <li>Active policies must have valid rules</li>
 *   <li>Enforced policies must have constraints defined</li>
 *   <li>Policies must have appropriate scopes for their type</li>
 *   <li>Production environment policies require stricter validation</li>
 *   <li>Critical policy types require approval for activation</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any policy configuration.
 */
public class PolicyValidationPolicy {

    // Policy types that require special approval for activation
    private static final Set<Policy.PolicyType> CRITICAL_TYPES = Set.of(
        Policy.PolicyType.SECURITY,
        Policy.PolicyType.PRIVACY,
        Policy.PolicyType.COMPLIANCE
    );

    // Protected policy keys that cannot be modified without special approval
    private static final Set<String> PROTECTED_KEYS = Set.of(
        "system.global.security",
        "system.global.privacy",
        "system.global.compliance"
    );

    private final boolean requireApprovalForCritical;
    private final boolean enforceScopeValidation;

    private PolicyValidationPolicy(Builder builder) {
        this.requireApprovalForCritical = builder.requireApprovalForCritical;
        this.enforceScopeValidation = builder.enforceScopeValidation;
    }

    /**
     * Validates a policy according to this policy.
     *
     * @param policy the policy to validate
     * @return validation result
     */
    public ValidationResult validate(Policy policy) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Active policies must have rules
        if (policy.status() == Policy.PolicyStatus.ACTIVE
            && (policy.rules() == null || policy.rules().isEmpty())) {
            errors.add("Active policies must have at least one rule");
        }

        // Rule 2: Enforced policies must have constraints
        if (policy.enforced() && policy.constraints() == null) {
            errors.add("Enforced policies must have constraints defined");
        }

        // Rule 3: Validate scope is appropriate for policy type
        if (enforceScopeValidation && !isScopeValidForType(policy.type(), policy.scope())) {
            errors.add(
                "Policy scope is not appropriate for type " + policy.type() +
                ". Scope type: " + (policy.scope() != null ? policy.scope().type() : "null")
            );
        }

        // Rule 4: Production environments require additional validation
        if ("production".equalsIgnoreCase(policy.environment()) ||
            "prod".equalsIgnoreCase(policy.environment())) {
            validateProductionRequirements(policy, warnings);
        }

        // Rule 5: Critical policy types require approval
        if (requireApprovalForCritical && CRITICAL_TYPES.contains(policy.type())) {
            warnings.add(
                "Policy type '" + policy.type() + "' is critical and requires approval for activation"
            );
        }

        // Rule 6: Protected keys require approval
        if (isProtectedKey(policy.policyKey())) {
            warnings.add(
                "Policy key '" + policy.policyKey() + "' is protected and requires approval for modification"
            );
        }

        // Rule 7: Validate rules are appropriate for policy type
        if (policy.rules() != null && !policy.rules().isEmpty()) {
            try {
                validateRulesForType(policy.type(), policy.rules());
            } catch (ValidationException e) {
                errors.add(e.getMessage());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates a policy change request.
     *
     * @param oldPolicy     the existing policy (null for new policies)
     * @param newPolicy     the new policy state
     * @param changedBy     the user making the change
     * @return validation result
     */
    public ChangeValidationResult validateChange(
            Policy oldPolicy,
            Policy newPolicy,
            String changedBy) {

        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Cannot change policy type of existing policy
        if (oldPolicy != null && !oldPolicy.type().equals(newPolicy.type())) {
            errors.add(
                "Cannot change policy type from " + oldPolicy.type() +
                " to " + newPolicy.type() + ". Create a new policy instead."
            );
        }

        // Rule 2: Protected keys require approval
        if (isProtectedKey(newPolicy.policyKey()) && oldPolicy != null) {
            warnings.add(
                "Modification of protected key '" + newPolicy.policyKey() + "' requires approval"
            );
        }

        // Rule 3: Critical policy changes in production require special handling
        if (CRITICAL_TYPES.contains(newPolicy.type()) &&
            ("production".equalsIgnoreCase(newPolicy.environment()) ||
             "prod".equalsIgnoreCase(newPolicy.environment()))) {
            warnings.add(
                "Critical policy change in production requires audit logging"
            );
        }

        // Rule 4: Cannot activate policy without valid rules
        if (newPolicy.status() == Policy.PolicyStatus.ACTIVE) {
            if (newPolicy.rules() == null || newPolicy.rules().isEmpty()) {
                errors.add("Cannot activate a policy without rules");
            }
        }

        return new ChangeValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Checks if a policy key is protected.
     *
     * @param policyKey the policy key
     * @return true if protected
     */
    public boolean isProtectedKey(String policyKey) {
        return PROTECTED_KEYS.contains(policyKey.toLowerCase());
    }

    /**
     * Checks if a policy type is critical.
     *
     * @param type the policy type
     * @return true if critical
     */
    public boolean isCriticalType(Policy.PolicyType type) {
        return CRITICAL_TYPES.contains(type);
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private void validateProductionRequirements(Policy policy, Set<String> warnings) {
        // Production must have non-null rules
        if (policy.rules() == null || policy.rules().isEmpty()) {
            warnings.add("Production environment policies should have rules defined");
        }

        // Production policies should have descriptions
        if (policy.description() == null || policy.description().isBlank()) {
            warnings.add("Production policies should have descriptions");
        }

        // Production enforced policies should have constraints
        if (policy.enforced() && policy.constraints() == null) {
            warnings.add("Production enforced policies should have constraints");
        }
    }

    private boolean isScopeValidForType(Policy.PolicyType type, Policy.PolicyScope scope) {
        if (scope == null) {
            return false;
        }

        // Global policies are valid for all types
        if (scope.type() == Policy.PolicyScope.ScopeType.GLOBAL) {
            return true;
        }

        // Check if scope is appropriate for type
        return switch (type) {
            case SECURITY, PRIVACY, COMPLIANCE ->
                // These types typically use GLOBAL or ATTRIBUTE_BASED scopes
                scope.type() == Policy.PolicyScope.ScopeType.GLOBAL ||
                scope.type() == Policy.PolicyScope.ScopeType.ATTRIBUTE_BASED;
            case BUSINESS_RULE, RATE_LIMIT, ACCESS_CONTROL ->
                // These types can use any scope
                true;
        };
    }

    private void validateRulesForType(Policy.PolicyType type, Map<String, Object> rules) {
        switch (type) {
            case RATE_LIMIT -> {
                if (!rules.containsKey("maxRequests") || !rules.containsKey("timeWindowMs")) {
                    throw new ValidationException(
                        "RATE_LIMIT policies must have 'maxRequests' and 'timeWindowMs' rules"
                    );
                }
            }
            case ACCESS_CONTROL -> {
                if (!rules.containsKey("roles") && !rules.containsKey("permissions")) {
                    throw new ValidationException(
                        "ACCESS_CONTROL policies must have 'roles' or 'permissions' rules"
                    );
                }
            }
            case SECURITY -> {
                if (!rules.containsKey("encryptionLevel") && !rules.containsKey("authenticationRequired")) {
                    throw new ValidationException(
                        "SECURITY policies must have security-related rules"
                    );
                }
            }
            case COMPLIANCE -> {
                if (!rules.containsKey("complianceStandard") && !rules.containsKey("requirements")) {
                    throw new ValidationException(
                        "COMPLIANCE policies must define compliance requirements"
                    );
                }
            }
        }
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(PolicyValidationPolicy existing) {
        return new Builder()
            .requireApprovalForCritical(existing.requireApprovalForCritical)
            .enforceScopeValidation(existing.enforceScopeValidation);
    }

    public static class Builder {
        private boolean requireApprovalForCritical = true;
        private boolean enforceScopeValidation = true;

        public Builder requireApprovalForCritical(boolean value) {
            this.requireApprovalForCritical = value;
            return this;
        }

        public Builder enforceScopeValidation(boolean value) {
            this.enforceScopeValidation = value;
            return this;
        }

        public PolicyValidationPolicy build() {
            return new PolicyValidationPolicy(this);
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

    private static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
