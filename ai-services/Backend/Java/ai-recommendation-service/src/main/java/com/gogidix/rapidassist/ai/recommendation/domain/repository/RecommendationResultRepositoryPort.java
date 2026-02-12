package com.gogidix.rapidassist.ai.recommendation.domain.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for RecommendationResult aggregate.
 */
public interface RecommendationResultRepositoryPort {

    RecommendationResult save(String tenantId, RecommendationResult result);

    Optional<RecommendationResult> findById(String tenantId, UUID id);

    Optional<RecommendationResult> findByRequestId(String tenantId, UUID requestId);

    Optional<RecommendationResult> findByCacheKey(String tenantId, String cacheKey);

    List<RecommendationResult> findByUserId(String tenantId, String userId);

    List<RecommendationResult> findByUserIdAndItemType(String tenantId, String userId, String itemType);

    void delete(String tenantId, UUID id);

    void deleteByRequestId(String tenantId, UUID requestId);

    boolean exists(String tenantId, UUID id);

    List<RecommendationResult> findExpiredResults(String tenantId);

    void deleteExpiredResults(String tenantId);
}
