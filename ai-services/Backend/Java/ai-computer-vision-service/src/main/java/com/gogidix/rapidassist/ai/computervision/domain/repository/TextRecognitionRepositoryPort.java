package com.gogidix.rapidassist.ai.computervision.domain.repository;

import com.gogidix.rapidassist.ai.computervision.domain.model.TextRecognition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for TextRecognition
 * This is a domain interface - implemented in infrastructure layer
 */
public interface TextRecognitionRepositoryPort {

    /**
     * Save text recognition
     */
    TextRecognition save(TextRecognition recognition);

    /**
     * Find text recognition by ID
     */
    Optional<TextRecognition> findById(UUID id);

    /**
     * Find text recognition by image analysis ID
     */
    List<TextRecognition> findByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Find text recognition by tenant ID
     */
    List<TextRecognition> findByTenantId(String tenantId);

    /**
     * Find text recognition by language
     */
    List<TextRecognition> findByLanguage(String language);

    /**
     * Delete text recognition by ID
     */
    void deleteById(UUID id);

    /**
     * Delete all recognition for an image analysis
     */
    void deleteByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Count recognition by image analysis ID
     */
    long countByImageAnalysisId(UUID imageAnalysisId);
}
