package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.RecommendationRequestEntity;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for RecommendationRequestEntity.
 */
@Repository
public interface SpringDataRecommendationRequestRepository extends MongoRepository<RecommendationRequestEntity, UUID> {

    List<RecommendationRequestEntity> findByTenantIdAndUserId(String tenantId, String userId);

    List<RecommendationRequestEntity> findByTenantIdAndStatus(String tenantId, RecommendationStatus status);

    List<RecommendationRequestEntity> findByTenantIdAndExpiresAtBefore(String tenantId, LocalDateTime threshold);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUserId(String tenantId, String userId);
}
