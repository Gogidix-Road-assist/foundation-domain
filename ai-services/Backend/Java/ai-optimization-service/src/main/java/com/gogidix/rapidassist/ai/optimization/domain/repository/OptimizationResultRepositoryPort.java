package com.gogidix.rapidassist.ai.optimization.domain.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving OptimizationResult entities.
 * Implementations are provided by the Infrastructure layer.
 */
public interface OptimizationResultRepositoryPort {

    /**
     * Save an optimization result.
     */
    OptimizationResult save(String tenantId, OptimizationResult result);

    /**
     * Find a result by ID and tenant.
     */
    Optional<OptimizationResult> findById(String tenantId, UUID resultId);

    /**
     * Find all results for a job.
     */
    List<OptimizationResult> findByJobId(String tenantId, UUID jobId);

    /**
     * Find results by job ID ordered by iteration.
     */
    List<OptimizationResult> findByJobIdOrderByIteration(String tenantId, UUID jobId);

    /**
     * Find a specific iteration result for a job.
     */
    Optional<OptimizationResult> findByJobIdAndIteration(String tenantId, UUID jobId, Integer iteration);

    /**
     * Find best result for a job (by objective value).
     */
    Optional<OptimizationResult> findBestResultByJobId(String tenantId, UUID jobId);

    /**
     * Find results by status.
     */
    List<OptimizationResult> findByStatus(String tenantId, String status);

    /**
     * Delete all results for a job.
     */
    void deleteByJobId(String tenantId, UUID jobId);

    /**
     * Delete a result by ID.
     */
    void delete(String tenantId, UUID resultId);

    /**
     * Count results for a job.
     */
    long countByJobId(String tenantId, UUID jobId);
}
