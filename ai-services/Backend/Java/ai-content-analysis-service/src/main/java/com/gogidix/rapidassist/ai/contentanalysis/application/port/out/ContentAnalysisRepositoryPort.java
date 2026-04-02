package com.gogidix.rapidassist.ai.contentanalysis.application.port.out;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentAnalysis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Content Analysis aggregate.
 * Defines the contract for persisting and retrieving content analysis data.
 */
public interface ContentAnalysisRepositoryPort {

    /**
     * Save content analysis (create or update)
     */
    ContentAnalysis save(ContentAnalysis analysis);

    /**
     * Find content analysis by ID
     */
    Optional<ContentAnalysis> findById(UUID id);

    /**
     * Find content analysis by ID and tenant ID
     */
    Optional<ContentAnalysis> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all content analyses for a tenant
     */
    List<ContentAnalysis> findByTenantId(String tenantId);

    /**
     * Find content analysis by content ID
     */
    Optional<ContentAnalysis> findByContentId(String contentId);

    /**
     * Find content analysis by content ID and tenant ID
     */
    Optional<ContentAnalysis> findByContentIdAndTenantId(String contentId, String tenantId);

    /**
     * Find content analyses by status
     */
    List<ContentAnalysis> findByStatus(ContentAnalysis.AnalysisStatus status);

    /**
     * Find content analyses by status and tenant ID
     */
    List<ContentAnalysis> findByStatusAndTenantId(ContentAnalysis.AnalysisStatus status, String tenantId);

    /**
     * Delete content analysis by ID
     */
    void deleteById(UUID id);

    /**
     * Delete content analysis by ID and tenant ID
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Check if content analysis exists by ID and tenant ID
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
