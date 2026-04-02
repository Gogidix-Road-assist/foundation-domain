package com.gogidix.rapidassist.ai.computervision.domain.repository;

import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ImageAnalysis aggregate
 * This is a domain interface - implemented in infrastructure layer
 */
public interface ImageAnalysisRepositoryPort {

    /**
     * Save image analysis aggregate
     */
    ImageAnalysisAggregate save(ImageAnalysisAggregate aggregate);

    /**
     * Find image analysis by ID
     */
    Optional<ImageAnalysisAggregate> findById(UUID id);

    /**
     * Find image analysis by tenant ID and ID
     */
    Optional<ImageAnalysisAggregate> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all image analyses for a tenant
     */
    List<ImageAnalysisAggregate> findByTenantId(String tenantId);

    /**
     * Find image analyses by user ID
     */
    List<ImageAnalysisAggregate> findByUserId(String userId);

    /**
     * Find image analyses by status
     */
    List<ImageAnalysisAggregate> findByStatus(String status);

    /**
     * Find image analyses by tenant and status
     */
    List<ImageAnalysisAggregate> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Find image analyses by analysis type
     */
    List<ImageAnalysisAggregate> findByAnalysisType(String analysisType);

    /**
     * Delete image analysis by ID
     */
    void deleteById(UUID id);

    /**
     * Check if image analysis exists
     */
    boolean existsById(UUID id);

    /**
     * Count all image analyses for a tenant
     */
    long countByTenantId(String tenantId);
}
