package com.gogidix.rapidassist.ai.summarization.domain.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SummarizationRequestRepositoryPort {
    SummarizationRequest save(String tenantId, SummarizationRequest request);
    Optional<SummarizationRequest> findById(String tenantId, UUID id);
    Optional<SummarizationRequest> findByRequestId(String tenantId, String requestId);
    List<SummarizationRequest> findByTenantId(String tenantId);
    List<SummarizationRequest> findByStatus(String tenantId, String status);
    List<SummarizationRequest> findByCreatedBy(String tenantId, String createdBy);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
    long countByTenantId(String tenantId);
}
