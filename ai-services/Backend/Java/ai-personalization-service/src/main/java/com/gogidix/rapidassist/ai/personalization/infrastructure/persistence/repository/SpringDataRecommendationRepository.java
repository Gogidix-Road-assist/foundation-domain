package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.RecommendationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for RecommendationEntity.
 */
@Repository
public interface SpringDataRecommendationRepository extends MongoRepository<RecommendationEntity, String> {

    Optional<RecommendationEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<RecommendationEntity> findByTenantId(String tenantId);

    List<RecommendationEntity> findByUserIdAndTenantId(String userId, String tenantId);

    List<RecommendationEntity> findByUserProfileIdAndTenantId(UUID userProfileId, String tenantId);

    List<RecommendationEntity> findBySegmentIdAndTenantId(String segmentId, String tenantId);

    List<RecommendationEntity> findByRecommendationTypeAndTenantId(String recommendationType, String tenantId);

    List<RecommendationEntity> findByStatusAndTenantId(String status, String tenantId);

    @Query("{ 'tenantId': ?0, 'userId': ?1, 'status': { $in: ?2 }, 'validFrom': { $lte: ?3 }, '$or': [ { 'validUntil': null }, { 'validUntil': { $gte: ?3 } } ] }")
    List<RecommendationEntity> findValidRecommendationsForUser(String tenantId, String userId, List<String> statuses, LocalDateTime dateTime);

    @Query("{ 'tenantId': ?0, 'userId': ?1, 'validFrom': { $lte: ?2 }, '$or': [ { 'validUntil': null }, { 'validUntil': { $gte: ?2 } } ] }")
    List<RecommendationEntity> findActiveRecommendationsForUser(String tenantId, String userId, LocalDateTime dateTime);

    @Query("{ 'tenantId': ?0, 'status': 'EXPIRED', 'validUntil': { $lt: ?1 } }")
    List<RecommendationEntity> findExpiredRecommendations(String tenantId, LocalDateTime dateTime);

    @Query(value = "{ 'tenantId': ?0, 'userId': ?1 }", sort = "{ 'relevanceScore': -1, 'rank': 1 }")
    List<RecommendationEntity> findByUserIdAndTenantIdOrderByRelevanceScoreDesc(String tenantId, String userId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    long countByUserIdAndTenantId(String userId, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUserIdAndTenantId(String userId, String tenantId);
}
