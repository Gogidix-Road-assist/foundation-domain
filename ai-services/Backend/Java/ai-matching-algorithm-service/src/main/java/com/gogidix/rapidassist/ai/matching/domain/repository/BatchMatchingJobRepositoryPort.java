package com.gogidix.rapidassist.ai.matching.domain.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchMatchingJob;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for BatchMatchingJob operations.
 * Defines the contract for batch job persistence.
 */
public interface BatchMatchingJobRepositoryPort {

    BatchMatchingJob save(String tenantId, BatchMatchingJob batchJob);

    Optional<BatchMatchingJob> findById(String tenantId, UUID id);

    Optional<BatchMatchingJob> findByJobCode(String tenantId, String jobCode);

    List<BatchMatchingJob> findByTenantId(String tenantId);

    List<BatchMatchingJob> findByStatus(String tenantId, String status);

    List<BatchMatchingJob> findActiveJobs(String tenantId);

    void delete(String tenantId, UUID id);

    boolean exists(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
