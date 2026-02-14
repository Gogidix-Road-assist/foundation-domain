package com.gogidix.rapidassist.analytics.domain.port.out;

import com.gogidix.rapidassist.analytics.domain.model.AnalyticsBatchJob;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for AnalyticsBatchJob operations.
 * Following hexagonal architecture principles.
 */
public interface AnalyticsBatchJobRepositoryPort {

    AnalyticsBatchJob save(String tenantId, AnalyticsBatchJob job);

    Optional<AnalyticsBatchJob> findById(String tenantId, UUID id);

    List<AnalyticsBatchJob> findByTenantId(String tenantId);

    List<AnalyticsBatchJob> findByTenantIdAndStatus(String tenantId, String status);

    List<AnalyticsBatchJob> findByTenantIdAndJobType(String tenantId, String jobType);

    List<AnalyticsBatchJob> findStaleJobs(String tenantId, String status, LocalDateTime before);

    List<AnalyticsBatchJob> findScheduledJobs(String tenantId);

    boolean exists(String tenantId, UUID id);

    void delete(String tenantId, UUID id);
}
