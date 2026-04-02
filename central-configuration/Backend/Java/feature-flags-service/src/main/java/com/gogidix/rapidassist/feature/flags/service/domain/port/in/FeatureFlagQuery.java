package com.gogidix.rapidassist.feature.flags.service.domain.port.in;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagChange;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagEvaluation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for feature flag query operations
 */
public interface FeatureFlagQuery {

    // Evaluation operations
    CompletableFuture<FeatureFlagEvaluation> evaluateFlag(String flagKey, String tenantId, EvaluationContext context);
    CompletableFuture<List<FeatureFlagEvaluation>> evaluateAllFlags(String tenantId, EvaluationContext context);
    CompletableFuture<Boolean> isFlagEnabled(String flagKey, String tenantId, String userId);

    // Retrieval operations
    CompletableFuture<Optional<FeatureFlag>> getFlagById(String flagId);
    CompletableFuture<Optional<FeatureFlag>> getFlagByKey(String tenantId, String key, String environment);
    CompletableFuture<List<FeatureFlag>> getAllFlags(String tenantId);
    CompletableFuture<List<FeatureFlag>> getFlagsByEnvironment(String tenantId, String environment);
    CompletableFuture<List<FeatureFlag>> getFlagsByStatus(String tenantId, FeatureFlag.FlagStatus status);
    CompletableFuture<List<FeatureFlag>> getActiveFlags(String tenantId, String environment);
    CompletableFuture<List<FeatureFlag>> getFlagsByTags(String tenantId, Set<String> tags);

    // Audit operations
    CompletableFuture<List<FeatureFlagChange>> getFlagHistory(String flagId);
    CompletableFuture<List<FeatureFlagChange>> getChangesByTimeRange(String tenantId, Instant from, Instant to);
    CompletableFuture<List<FeatureFlagChange>> getPendingApprovals(String tenantId);

    // Search operations
    CompletableFuture<List<FeatureFlag>> searchFlags(String tenantId, String keyword);
    CompletableFuture<List<FeatureFlag>> getFlagsCreatedSince(String tenantId, Instant since);
    CompletableFuture<List<FeatureFlag>> getFlagsExpiringBefore(String tenantId, Instant before);

    // Statistics
    CompletableFuture<FlagStatistics> getStatistics(String tenantId);

    // Evaluation context
    record EvaluationContext(
        String userId,
        String tenantId,
        String countryCode,
        String environment,
        java.util.Map<String, Object> attributes
    ) {
        public static EvaluationContext of(String userId, String tenantId) {
            return new EvaluationContext(userId, tenantId, null, "production", java.util.Map.of());
        }
    }

    // Statistics record
    record FlagStatistics(
        long totalFlags,
        long activeFlags,
        long enabledFlags,
        long disabledFlags,
        long expiringSoon,
        long requiringApproval
    ) {}
}
