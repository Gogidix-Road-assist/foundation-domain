package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.BatchMatchingJobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for BatchMatchingJobEntity.
 */
@Repository
public interface SpringDataBatchMatchingJobRepository extends MongoRepository<BatchMatchingJobEntity, String> {

    /**
     * Find job by UUID and tenant.
     */
    Optional<BatchMatchingJobEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find job by job code and tenant.
     */
    Optional<BatchMatchingJobEntity> findByJobCodeAndTenantId(String jobCode, String tenantId);

    /**
     * Find all jobs by tenant.
     */
    List<BatchMatchingJobEntity> findByTenantId(String tenantId);

    /**
     * Find jobs by status and tenant.
     */
    List<BatchMatchingJobEntity> findByStatusAndTenantId(BatchJobStatus status, String tenantId);

    /**
     * Find active jobs (not completed/failed/cancelled) by tenant.
     */
    List<BatchMatchingJobEntity> findByStatusInAndTenantId(
            List<BatchJobStatus> statuses, String tenantId);

    /**
     * Check if job exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete job by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count jobs by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Check if job code exists.
     */
    boolean existsByJobCodeAndTenantId(String jobCode, String tenantId);

    /**
     * Find jobs by entity types.
     */
    List<BatchMatchingJobEntity> findBySourceEntityTypeAndTargetEntityTypeAndTenantId(
            String sourceEntityType, String targetEntityType, String tenantId);
}
