package com.gogidix.rapidassist.ai.optimization.domain.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationJob;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationStatus;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving OptimizationJob aggregates.
 * Implementations are provided by the Infrastructure layer.
 */
public interface OptimizationJobRepositoryPort {

    /**
     * Save an optimization job (create or update).
     */
    OptimizationJob save(String tenantId, OptimizationJob job);

    /**
     * Find a job by ID and tenant.
     */
    Optional<OptimizationJob> findById(String tenantId, UUID jobId);

    /**
     * Find all jobs for a tenant.
     */
    List<OptimizationJob> findByTenantId(String tenantId);

    /**
     * Find jobs by type and tenant.
     */
    List<OptimizationJob> findByType(String tenantId, OptimizationType type);

    /**
     * Find jobs by status and tenant.
     */
    List<OptimizationJob> findByStatus(String tenantId, OptimizationStatus status);

    /**
     * Find jobs by model ID and tenant.
     */
    List<OptimizationJob> findByModelId(String tenantId, String modelId);

    /**
     * Find jobs by dataset ID and tenant.
     */
    List<OptimizationJob> findByDatasetId(String tenantId, String datasetId);

    /**
     * Find running jobs for a tenant.
     */
    List<OptimizationJob> findRunningJobs(String tenantId);

    /**
     * Find jobs by status and type.
     */
    List<OptimizationJob> findByStatusAndType(String tenantId, OptimizationStatus status, OptimizationType type);

    /**
     * Find jobs created within a date range.
     */
    List<OptimizationJob> findByCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find jobs by creator.
     */
    List<OptimizationJob> findByCreatedBy(String tenantId, String createdBy);

    /**
     * Find jobs by name pattern.
     */
    List<OptimizationJob> findByNameContaining(String tenantId, String namePattern);

    /**
     * Delete a job by ID and tenant.
     */
    void delete(String tenantId, UUID jobId);

    /**
     * Check if a job exists.
     */
    boolean exists(String tenantId, UUID jobId);

    /**
     * Count jobs by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count jobs by status and tenant.
     */
    long countByStatusAndTenantId(String tenantId, OptimizationStatus status);
}
