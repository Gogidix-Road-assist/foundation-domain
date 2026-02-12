package com.gogidix.rapidassist.feature.flags.service.domain.port.in;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagChange;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for feature flag command operations
 */
public interface FeatureFlagCommand {

    // Create operations
    CompletableFuture<FeatureFlag> createFlag(CreateFlagCommand command);

    // Update operations
    CompletableFuture<Optional<FeatureFlag>> updateFlag(UpdateFlagCommand command);
    CompletableFuture<Optional<FeatureFlag>> enableFlag(String flagId, String changedBy, String reason);
    CompletableFuture<Optional<FeatureFlag>> disableFlag(String flagId, String changedBy, String reason);
    CompletableFuture<Optional<FeatureFlag>> archiveFlag(String flagId, String changedBy);

    // Rollout operations
    CompletableFuture<Optional<FeatureFlag>> updateRolloutPercentage(String flagId, int percentage, String changedBy);
    CompletableFuture<Optional<FeatureFlag>> addAllowedUsers(String flagId, Set<String> userIds, String changedBy);
    CompletableFuture<Optional<FeatureFlag>> removeAllowedUsers(String flagId, Set<String> userIds, String changedBy);
    CompletableFuture<Optional<FeatureFlag>> addAllowedTenants(String flagId, Set<String> tenantIds, String changedBy);
    CompletableFuture<Optional<FeatureFlag>> addAllowedCountries(String flagId, Set<String> countryCodes, String changedBy);

    // Approval operations
    CompletableFuture<FeatureFlagChange> approveChange(String changeId, String approvedBy);
    CompletableFuture<FeatureFlagChange> rejectChange(String changeId, String rejectedBy, String reason);

    // Bulk operations
    CompletableFuture<List<FeatureFlag>> bulkEnable(List<String> flagIds, String changedBy);
    CompletableFuture<List<FeatureFlag>> bulkDisable(List<String> flagIds, String changedBy);

    // Delete operations
    CompletableFuture<Boolean> deleteFlag(String flagId, String deletedBy);

    // Command records
    record CreateFlagCommand(
        String tenantId,
        String key,
        String name,
        String description,
        FeatureFlag.FlagType type,
        FeatureFlag.RolloutStrategy rolloutStrategy,
        String environment,
        String createdBy,
        Set<String> tags,
        Boolean requiresApproval,
        java.time.Instant expiresAt,
        Set<String> allowedTenants,
        Set<String> allowedUsers,
        Set<String> allowedCountries,
        FeatureFlag.PercentageRollout percentageRollout
    ) {}

    record UpdateFlagCommand(
        String flagId,
        String name,
        String description,
        FeatureFlag.FlagType type,
        FeatureFlag.RolloutStrategy rolloutStrategy,
        String updatedBy,
        String reason
    ) {}
}
