package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ABTestStatus;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ABTestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ABTestEntity.
 */
@Repository
public interface SpringDataABTestRepository extends MongoRepository<ABTestEntity, String> {

    /**
     * Find A/B test by UUID and tenant.
     */
    Optional<ABTestEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all tests for a tenant.
     */
    List<ABTestEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    /**
     * Find tests by status.
     */
    List<ABTestEntity> findByTenantIdAndStatus(String tenantId, ABTestStatus status);

    /**
     * Find tests by control model version.
     */
    List<ABTestEntity> findByTenantIdAndControlModelVersionId(String tenantId, UUID controlModelVersionId);

    /**
     * Find tests by treatment model version.
     */
    List<ABTestEntity> findByTenantIdAndTreatmentModelVersionId(String tenantId, UUID treatmentModelVersionId);

    /**
     * Find test by name.
     */
    Optional<ABTestEntity> findByTenantIdAndName(String tenantId, String name);

    /**
     * Find running tests.
     */
    List<ABTestEntity> findByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, ABTestStatus status);

    /**
     * Check if test exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete test by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
