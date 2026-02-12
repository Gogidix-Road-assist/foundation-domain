package com.gogidix.rapidassist.ai.summarization.domain.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.BatchSummarizationJob;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchSummarizationJobRepositoryPort {
    BatchSummarizationJob save(String tenantId, BatchSummarizationJob job);
    Optional<BatchSummarizationJob> findById(String tenantId, UUID id);
    Optional<BatchSummarizationJob> findByJobId(String tenantId, String jobId);
    List<BatchSummarizationJob> findByTenantId(String tenantId);
    List<BatchSummarizationJob> findByStatus(String tenantId, String status);
    List<BatchSummarizationJob> findByCreatedBy(String tenantId, String createdBy);
    void delete(String tenantId, UUID id);
}
