package com.gogidix.rapidassist.ai.summization.domain.repository;

import com.gogidix.rapidassist.ai.summization.domain.model.SummarizationTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for SummarizationTask aggregate.
 */
public interface SummarizationTaskRepositoryPort {

    SummarizationTask save(String tenantId, SummarizationTask task);

    Optional<SummarizationTask> findById(String tenantId, UUID taskId);

    Optional<SummarizationTask> findByTaskId(String tenantId, String taskId);

    List<SummarizationTask> findByTenantId(String tenantId);

    List<SummarizationTask> findByUserId(String tenantId, String userId);

    List<SummarizationTask> findByStatus(String tenantId, String status);

    List<SummarizationTask> findPendingTasks(String tenantId);

    List<SummarizationTask> findInProgressTasks(String tenantId);

    void delete(String tenantId, UUID taskId);

    boolean exists(String tenantId, UUID taskId);

    long countByTenantId(String tenantId);

    List<SummarizationTask> findHighPriorityTasks(String tenantId);
}
