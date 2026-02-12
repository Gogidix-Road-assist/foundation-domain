package com.gogidix.rapidassist.ai.summization.domain.repository;

import com.gogidix.rapidassist.ai.summization.domain.model.DocumentSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DocumentSummary.
 */
public interface DocumentSummaryRepositoryPort {

    DocumentSummary save(String tenantId, DocumentSummary summary);

    Optional<DocumentSummary> findById(String tenantId, UUID summaryId);

    List<DocumentSummary> findByTenantId(String tenantId);

    List<DocumentSummary> findByTaskId(String tenantId, UUID taskId);

    List<DocumentSummary> findByUserId(String tenantId, String userId);

    List<DocumentSummary> findByStyle(String tenantId, String style);

    List<DocumentSummary> findByLanguage(String tenantId, String language);

    void delete(String tenantId, UUID summaryId);

    boolean exists(String tenantId, UUID summaryId);

    long countByTenantId(String tenantId);

    List<DocumentSummary> searchByContent(String tenantId, String searchTerm);
}
