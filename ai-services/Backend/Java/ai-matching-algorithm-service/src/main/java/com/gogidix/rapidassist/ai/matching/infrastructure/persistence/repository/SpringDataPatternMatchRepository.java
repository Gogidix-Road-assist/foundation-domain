package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.PatternMatchEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for PatternMatchEntity.
 */
@Repository
public interface SpringDataPatternMatchRepository extends MongoRepository<PatternMatchEntity, String> {

    /**
     * Find pattern match by UUID and tenant.
     */
    Optional<PatternMatchEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find pattern match by pattern ID and tenant.
     */
    Optional<PatternMatchEntity> findByPatternIdAndTenantId(String patternId, String tenantId);

    /**
     * Find all pattern matches by tenant.
     */
    List<PatternMatchEntity> findByTenantId(String tenantId);

    /**
     * Find pattern matches by entity type.
     */
    List<PatternMatchEntity> findByEntityTypeAndTenantId(String entityType, String tenantId);

    /**
     * Find pattern matches by entity ID.
     */
    List<PatternMatchEntity> findByEntityTypeAndEntityIdAndTenantId(
            String entityType, String entityId, String tenantId);

    /**
     * Find pattern matches by pattern type.
     */
    List<PatternMatchEntity> findByPatternTypeAndTenantId(String patternType, String tenantId);

    /**
     * Check if pattern match exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete pattern match by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count pattern matches by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find pattern matches with minimum confidence threshold.
     */
    List<PatternMatchEntity> findByConfidenceGreaterThanEqualAndTenantId(
            double minConfidence, String tenantId);
}
