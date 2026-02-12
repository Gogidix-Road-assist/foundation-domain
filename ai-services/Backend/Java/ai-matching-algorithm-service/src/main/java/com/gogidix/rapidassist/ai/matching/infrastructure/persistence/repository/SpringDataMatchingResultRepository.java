package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingStatus;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.MatchingResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for MatchingResultEntity.
 */
@Repository
public interface SpringDataMatchingResultRepository extends MongoRepository<MatchingResultEntity, String> {

    /**
     * Find result by UUID and tenant.
     */
    Optional<MatchingResultEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find result by match ID and tenant.
     */
    Optional<MatchingResultEntity> findByMatchIdAndTenantId(String matchId, String tenantId);

    /**
     * Find all results by tenant.
     */
    List<MatchingResultEntity> findByTenantId(String tenantId);

    /**
     * Find results by source entity.
     */
    List<MatchingResultEntity> findBySourceEntityTypeAndSourceEntityIdAndTenantId(
            String sourceEntityType, String sourceEntityId, String tenantId);

    /**
     * Find results by target entity.
     */
    List<MatchingResultEntity> findByTargetEntityTypeAndTargetEntityIdAndTenantId(
            String targetEntityType, String targetEntityId, String tenantId);

    /**
     * Find results by status and tenant.
     */
    List<MatchingResultEntity> findByStatusAndTenantId(MatchingStatus status, String tenantId);

    /**
     * Check if result exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete result by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count results by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find results by algorithm type and tenant.
     */
    List<MatchingResultEntity> findByAlgorithmTypeAndTenantId(
            com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType, String tenantId);
}
