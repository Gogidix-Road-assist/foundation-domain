package com.gogidix.rapidassist.ai.recommendation.domain.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for RecommendationRequest aggregate.
 */
public interface RecommendationRequestRepositoryPort {

    RecommendationRequest save(String tenantId, RecommendationRequest request);

    Optional<RecommendationRequest> findById(String tenantId, UUID id);

    List<RecommendationRequest> findByUserId(String tenantId, String userId);

    List<RecommendationRequest> findByStatus(String tenantId, com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus status);

    List<RecommendationRequest> findPendingRequests(String tenantId);

    List<RecommendationRequest> findExpiredRequests(String tenantId);

    void delete(String tenantId, UUID id);

    void deleteByUserId(String tenantId, String userId);

    boolean exists(String tenantId, UUID id);
}
