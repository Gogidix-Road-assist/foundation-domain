package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for OptimizationResultEntity.
 */
@Repository
public interface SpringDataOptimizationResultRepository extends MongoRepository<OptimizationResultEntity, String> {

    /**
     * Find result by UUID and tenant.
     */
    Optional<OptimizationResultEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all results for a job.
     */
    List<OptimizationResultEntity> findByOptimizationJobIdAndTenantId(UUID jobId, String tenantId);

    /**
     * Find results for a job ordered by iteration.
     */
    List<OptimizationResultEntity> findByOptimizationJobIdAndTenantIdOrderByIterationAsc(UUID jobId, String tenantId);

    /**
     * Find a specific iteration result for a job.
     */
    Optional<OptimizationResultEntity> findByOptimizationJobIdAndTenantIdAndIteration(UUID jobId, String tenantId, Integer iteration);

    /**
     * Find results by status and tenant.
     */
    List<OptimizationResultEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * Find best result (minimum objective value) for a job.
     */
    Optional<OptimizationResultEntity> findFirstByOptimizationJobIdAndTenantIdOrderByObjectiveValueAsc(UUID jobId, String tenantId);

    /**
     * Count results for a job.
     */
    long countByOptimizationJobIdAndTenantId(UUID jobId, String tenantId);

    /**
     * Delete results by job ID and tenant.
     */
    void deleteByOptimizationJobIdAndTenantId(UUID jobId, String tenantId);

    /**
     * Delete result by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
