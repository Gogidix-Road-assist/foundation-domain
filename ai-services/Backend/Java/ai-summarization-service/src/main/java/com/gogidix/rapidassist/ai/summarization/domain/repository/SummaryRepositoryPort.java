package com.gogidix.rapidassist.ai.summarization.domain.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.Summary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SummaryRepositoryPort {
    Summary save(String tenantId, Summary summary);
    Optional<Summary> findById(String tenantId, UUID id);
    Optional<Summary> findBySummarizationRequestId(String tenantId, UUID requestId);
    List<Summary> findByTenantId(String tenantId);
    List<Summary> findByCreatedBy(String tenantId, String createdBy);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
    long countByTenantId(String tenantId);
}
