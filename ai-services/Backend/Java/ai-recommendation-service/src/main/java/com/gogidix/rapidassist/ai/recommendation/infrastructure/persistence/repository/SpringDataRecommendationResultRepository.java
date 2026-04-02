package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.RecommendationResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for RecommendationResultEntity.
 */
@Repository
public interface SpringDataRecommendationResultRepository extends MongoRepository<RecommendationResultEntity, UUID> {

    RecommendationResultEntity findByTenantIdAndRequestId(String tenantId, UUID requestId);

    RecommendationResultEntity findByTenantIdAndCacheKey(String tenantId, String cacheKey);

    List<RecommendationResultEntity> findByTenantIdAndUserId(String tenantId, String userId);

    List<RecommendationResultEntity> findByTenantIdAndUserIdAndItemType(String tenantId, String userId, String itemType);

    List<RecommendationResultEntity> findByTenantIdAndExpiresAtBefore(String tenantId, LocalDateTime threshold);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndRequestId(String tenantId, UUID requestId);

    void deleteByTenantIdAndExpiresAtBefore(String tenantId, LocalDateTime threshold);
}
