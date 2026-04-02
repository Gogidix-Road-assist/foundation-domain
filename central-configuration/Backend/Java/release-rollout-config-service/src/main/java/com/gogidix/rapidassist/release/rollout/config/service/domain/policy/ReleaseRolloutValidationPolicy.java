package com.gogidix.rapidassist.release.rollout.config.service.domain.policy;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Domain policy for validating release rollouts.
 *
 * <p>This policy encapsulates business rules for rollout validation:
 * <ul>
 *   <li>Production rollouts require approval</li>
 *   <li>Canary deployments must have valid batch configuration</li>
 *   <li>Blue-green deployments require sufficient resources</li>
 *   <li>A/B testing requires proper segment configuration</li>
 *   <li>Rollout strategies must match environment constraints</li>
 * </ul>
 *
 * <p>Policies are stateless and can be applied to any rollout.
 */
public class ReleaseRolloutValidationPolicy {

    // Protected environments that require additional validation
    private static final Set<String> PROTECTED_ENVIRONMENTS = Set.of(
        "production", "prod", "staging"
    );

    // Minimum batch intervals for different strategies (in minutes)
    private static final int MIN_CANARY_INTERVAL = 15;
    private static final int MIN_GRADUAL_INTERVAL = 30;

    private final boolean requireApprovalForProduction;
    private final boolean validateBatchConfiguration;
    private final boolean enforceEnvironmentConstraints;

    private ReleaseRolloutValidationPolicy(Builder builder) {
        this.requireApprovalForProduction = builder.requireApprovalForProduction;
        this.validateBatchConfiguration = builder.validateBatchConfiguration;
        this.enforceEnvironmentConstraints = builder.enforceEnvironmentConstraints;
    }

    /**
     * Validates a rollout according to this policy.
     *
     * @param rollout the rollout to validate
     * @return validation result
     */
    public ValidationResult validate(ReleaseRollout rollout) {
        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Validate release ID format
        if (rollout.releaseId() == null || rollout.releaseId().isBlank()) {
            errors.add("Release ID is required");
        } else if (!isValidReleaseId(rollout.releaseId())) {
            errors.add("Release ID must follow format: [project]-[component]-[version]");
        }

        // Rule 2: Validate version format
        if (rollout.version() == null || rollout.version().isBlank()) {
            errors.add("Version is required");
        } else if (!isValidVersion(rollout.version())) {
            errors.add("Version must follow semantic versioning (e.g., 1.0.0)");
        }

        // Rule 3: Validate rollout configuration
        if (rollout.config() == null) {
            errors.add("Rollout configuration is required");
        } else if (validateBatchConfiguration) {
            validateRolloutConfig(rollout, errors, warnings);
        }

        // Rule 4: Production environment requires approval
        if (requireApprovalForProduction && isProtectedEnvironment(rollout.environment())) {
            warnings.add(
                "Rollout to " + rollout.environment() +
                " environment requires approval before starting"
            );
        }

        // Rule 5: Validate strategy-specific constraints
        validateStrategyConstraints(rollout, errors, warnings);

        // Rule 6: Validate status transitions
        if (rollout.status() == null) {
            errors.add("Rollout status is required");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validates a rollout change request.
     *
     * @param oldRollout  the existing rollout (null for new rollouts)
     * @param newStatus   the new status
     * @param changedBy   the user making the change
     * @return validation result
     */
    public ChangeValidationResult validateStatusChange(
            ReleaseRollout oldRollout,
            ReleaseRollout.RolloutStatus newStatus,
            String changedBy) {

        Set<String> errors = new HashSet<>();
        Set<String> warnings = new HashSet<>();

        // Rule 1: Cannot change status of completed rollouts
        if (oldRollout != null && oldRollout.status() == ReleaseRollout.RolloutStatus.COMPLETED) {
            errors.add("Cannot change status of completed rollouts");
        }

        // Rule 2: Cannot change status of rolled back rollouts
        if (oldRollout != null && oldRollout.status() == ReleaseRollout.RolloutStatus.ROLLED_BACK) {
            errors.add("Cannot change status of rolled back rollouts");
        }

        // Rule 3: Production status changes require approval
        if (requireApprovalForProduction
                && oldRollout != null
                && isProtectedEnvironment(oldRollout.environment())
                && newStatus == ReleaseRollout.RolloutStatus.IN_PROGRESS) {
            warnings.add("Status change to IN_PROGRESS in production requires approval");
        }

        // Rule 4: Validate status transition is allowed
        if (oldRollout != null && !isValidStatusTransition(oldRollout.status(), newStatus)) {
            errors.add(
                "Invalid status transition from " + oldRollout.status() +
                " to " + newStatus
            );
        }

        return new ChangeValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Checks if an environment is protected (requires additional validation).
     *
     * @param environment the environment
     * @return true if protected
     */
    public boolean isProtectedEnvironment(String environment) {
        return PROTECTED_ENVIRONMENTS.contains(environment.toLowerCase());
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private void validateRolloutConfig(
            ReleaseRollout rollout,
            Set<String> errors,
            Set<String> warnings) {

        ReleaseRollout.RolloutConfig config = rollout.config();
        ReleaseRollout.RolloutStrategy strategy = rollout.strategy();

        // Validate batch configuration for gradual strategies
        if (strategy == ReleaseRollout.RolloutStrategy.CANARY
                || strategy == ReleaseRollout.RolloutStrategy.GRADUAL) {

            if (config.batchSize() <= 0 || config.batchSize() > 100) {
                errors.add("Batch size must be between 1 and 100");
            }

            int minInterval = strategy == ReleaseRollout.RolloutStrategy.CANARY
                ? MIN_CANARY_INTERVAL
                : MIN_GRADUAL_INTERVAL;

            if (config.batchIntervalMinutes() < minInterval) {
                warnings.add(
                    "Batch interval of " + config.batchIntervalMinutes() +
                    " minutes is below recommended minimum of " + minInterval +
                    " minutes for " + strategy
                );
            }

            if (config.initialPercentage() < 1 || config.initialPercentage() > 50) {
                warnings.add(
                    "Initial percentage of " + config.initialPercentage() +
                    "% is outside recommended range of 1-50% for " + strategy
                );
            }

            if (config.maxPercentage() < config.initialPercentage()) {
                errors.add("Max percentage must be greater than or equal to initial percentage");
            }
        }

        // Validate A/B testing configuration
        if (strategy == ReleaseRollout.RolloutStrategy.AB_TESTING) {
            if (config.targetSegments() == null || config.targetSegments().isEmpty()) {
                errors.add("A/B testing requires at least one target segment");
            }

            if (config.criteria() == null || config.criteria().isBlank()) {
                warnings.add("A/B testing should define success criteria");
            }
        }

        // Validate auto-promotion settings
        if (config.autoPromote() && isProtectedEnvironment(rollout.environment())) {
            warnings.add(
                "Auto-promotion in " + rollout.environment() +
                " environment requires careful monitoring"
            );
        }

        // Validate approval requirements
        if (config.requireApproval() && rollout.environment() != null) {
            if (!isProtectedEnvironment(rollout.environment())) {
                warnings.add(
                    "Approval requirement set for non-protected environment: " +
                    rollout.environment()
                );
            }
        }
    }

    private void validateStrategyConstraints(
            ReleaseRollout rollout,
            Set<String> errors,
            Set<String> warnings) {

        ReleaseRollout.RolloutStrategy strategy = rollout.strategy();
        String environment = rollout.environment();

        if (strategy == null) {
            errors.add("Rollout strategy is required");
            return;
        }

        // Strategy-specific environment constraints
        if (enforceEnvironmentConstraints) {
            if (isProtectedEnvironment(environment)) {
                switch (strategy) {
                    case BIG_BANG -> warnings.add(
                        "Big-bang deployment in " + environment +
                        " environment carries high risk"
                    );
                    case BLUE_GREEN -> {
                        // Blue-green is safe for production
                    }
                    case CANARY, GRADUAL -> {
                        // Gradual strategies are safe for production
                    }
                    case AB_TESTING -> warnings.add(
                        "A/B testing in " + environment +
                        " requires proper monitoring setup"
                    );
                }
            }
        }
    }

    private boolean isValidReleaseId(String releaseId) {
        // Release ID format: project-component-version (e.g., auth-service-1.0.0)
        Pattern pattern = Pattern.compile("^[a-z][a-z0-9-]*-[a-z][a-z0-9-]*-\\d+\\.\\d+\\.\\d+");
        return pattern.matcher(releaseId).matches();
    }

    private boolean isValidVersion(String version) {
        // Semantic versioning: major.minor.patch (e.g., 1.0.0)
        Pattern pattern = Pattern.compile("^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9.]+)?$");
        return pattern.matcher(version).matches();
    }

    private boolean isValidStatusTransition(
            ReleaseRollout.RolloutStatus currentStatus,
            ReleaseRollout.RolloutStatus newStatus) {

        return switch (currentStatus) {
            case PLANNED -> newStatus == ReleaseRollout.RolloutStatus.IN_PROGRESS
                || newStatus == ReleaseRollout.RolloutStatus.PAUSED
                || newStatus == ReleaseRollout.RolloutStatus.FAILED;
            case IN_PROGRESS -> newStatus == ReleaseRollout.RolloutStatus.COMPLETED
                || newStatus == ReleaseRollout.RolloutStatus.PAUSED
                || newStatus == ReleaseRollout.RolloutStatus.FAILED
                || newStatus == ReleaseRollout.RolloutStatus.ROLLED_BACK;
            case PAUSED -> newStatus == ReleaseRollout.RolloutStatus.IN_PROGRESS
                || newStatus == ReleaseRollout.RolloutStatus.ROLLED_BACK
                || newStatus == ReleaseRollout.RolloutStatus.FAILED;
            case COMPLETED, ROLLED_BACK, FAILED -> false; // Terminal states
        };
    }

    // ========================================================================
    // Builder
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ReleaseRolloutValidationPolicy existing) {
        return new Builder()
            .requireApprovalForProduction(existing.requireApprovalForProduction)
            .validateBatchConfiguration(existing.validateBatchConfiguration)
            .enforceEnvironmentConstraints(existing.enforceEnvironmentConstraints);
    }

    public static class Builder {
        private boolean requireApprovalForProduction = true;
        private boolean validateBatchConfiguration = true;
        private boolean enforceEnvironmentConstraints = true;

        public Builder requireApprovalForProduction(boolean value) {
            this.requireApprovalForProduction = value;
            return this;
        }

        public Builder validateBatchConfiguration(boolean value) {
            this.validateBatchConfiguration = value;
            return this;
        }

        public Builder enforceEnvironmentConstraints(boolean value) {
            this.enforceEnvironmentConstraints = value;
            return this;
        }

        public ReleaseRolloutValidationPolicy build() {
            return new ReleaseRolloutValidationPolicy(this);
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
