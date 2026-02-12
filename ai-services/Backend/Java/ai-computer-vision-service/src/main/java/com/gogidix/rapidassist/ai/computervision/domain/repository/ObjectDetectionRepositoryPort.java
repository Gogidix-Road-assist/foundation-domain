package com.gogidix.rapidassist.ai.computervision.domain.repository;

import com.gogidix.rapidassist.ai.computervision.domain.model.ObjectDetection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ObjectDetection
 * This is a domain interface - implemented in infrastructure layer
 */
public interface ObjectDetectionRepositoryPort {

    /**
     * Save object detection
     */
    ObjectDetection save(ObjectDetection detection);

    /**
     * Find object detection by ID
     */
    Optional<ObjectDetection> findById(UUID id);

    /**
     * Find object detections by image analysis ID
     */
    List<ObjectDetection> findByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Find object detections by tenant ID
     */
    List<ObjectDetection> findByTenantId(String tenantId);

    /**
     * Find object detections by type
     */
    List<ObjectDetection> findByType(String type);

    /**
     * Delete object detection by ID
     */
    void deleteById(UUID id);

    /**
     * Delete all detections for an image analysis
     */
    void deleteByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Count detections by image analysis ID
     */
    long countByImageAnalysisId(UUID imageAnalysisId);
}
