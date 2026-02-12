package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.SimilarityScoreEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SimilarityScoreEntity.
 */
@Repository
public interface SpringDataSimilarityScoreRepository extends MongoRepository<SimilarityScoreEntity, String> {

    /**
     * Find score by UUID and tenant.
     */
    Optional<SimilarityScoreEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find score by score ID and tenant.
     */
    Optional<SimilarityScoreEntity> findByScoreIdAndTenantId(String scoreId, String tenantId);

    /**
     * Find all scores by tenant.
     */
    List<SimilarityScoreEntity> findByTenantId(String tenantId);

    /**
     * Find scores by entity pair.
     */
    List<SimilarityScoreEntity> findBySourceEntityTypeAndSourceEntityIdAndTargetEntityTypeAndTargetEntityIdAndTenantId(
            String sourceEntityType, String sourceEntityId,
            String targetEntityType, String targetEntityId, String tenantId);

    /**
     * Check if score exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete score by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count scores by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find scores with minimum similarity threshold.
     */
    List<SimilarityScoreEntity> findByOverallSimilarityGreaterThanEqualAndTenantId(
            double minSimilarity, String tenantId);
}
