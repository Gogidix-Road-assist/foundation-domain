package com.gogidix.rapidassist.ai.computervision.domain.repository;

import com.gogidix.rapidassist.ai.computervision.domain.model.ImageClassification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ImageClassification
 * This is a domain interface - implemented in infrastructure layer
 */
public interface ImageClassificationRepositoryPort {

    /**
     * Save image classification
     */
    ImageClassification save(ImageClassification classification);

    /**
     * Find image classification by ID
     */
    Optional<ImageClassification> findById(UUID id);

    /**
     * Find image classifications by image analysis ID
     */
    List<ImageClassification> findByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Find image classifications by tenant ID
     */
    List<ImageClassification> findByTenantId(String tenantId);

    /**
     * Find image classifications by primary class
     */
    List<ImageClassification> findByPrimaryClass(String primaryClass);

    /**
     * Delete image classification by ID
     */
    void deleteById(UUID id);

    /**
     * Delete all classifications for an image analysis
     */
    void deleteByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Count classifications by image analysis ID
     */
    long countByImageAnalysisId(UUID imageAnalysisId);
}
