package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.Recommendation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving Recommendation entities.
 */
public interface RecommendationRepositoryPort {

    /**
     * Save a recommendation (create or update).
     */
    Recommendation save(String tenantId, Recommendation recommendation);

    /**
     * Find a recommendation by ID and tenant.
     */
    Optional<Recommendation> findById(String tenantId, UUID recommendationId);

    /**
     * Find all recommendations for a tenant.
     */
    List<Recommendation> findByTenantId(String tenantId);

    /**
     * Find recommendations by user ID and tenant.
     */
    List<Recommendation> findByUserId(String tenantId, String userId);

    /**
     * Find recommendations by profile ID and tenant.
     */
    List<Recommendation> findByUserProfileId(String tenantId, UUID userProfileId);

    /**
     * Find recommendations by segment ID and tenant.
     */
    List<Recommendation> findBySegmentId(String tenantId, String segmentId);

    /**
     * Find recommendations by type and tenant.
     */
    List<Recommendation> findByRecommendationType(String tenantId, String recommendationType);

    /**
     * Find recommendations by status and tenant.
     */
    List<Recommendation> findByStatus(String tenantId, String status);

    /**
     * Find valid recommendations for a user.
     */
    List<Recommendation> findValidRecommendationsForUser(String tenantId, String userId, List<String> statuses, LocalDateTime dateTime);

    /**
     * Find active recommendations for a user.
     */
    List<Recommendation> findActiveRecommendationsForUser(String tenantId, String userId, LocalDateTime dateTime);

    /**
     * Find expired recommendations.
     */
    List<Recommendation> findExpiredRecommendations(String tenantId, LocalDateTime dateTime);

    /**
     * Find recommendations for a user ordered by relevance.
     */
    List<Recommendation> findByUserIdOrderByRelevance(String tenantId, String userId);

    /**
     * Delete a recommendation by ID and tenant.
     */
    void delete(String tenantId, UUID recommendationId);

    /**
     * Delete all recommendations for a user.
     */
    void deleteByUserId(String tenantId, String userId);

    /**
     * Check if a recommendation exists.
     */
    boolean exists(String tenantId, UUID recommendationId);

    /**
     * Count recommendations by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count recommendations for a user.
     */
    long countByUserId(String tenantId, String userId);
}
