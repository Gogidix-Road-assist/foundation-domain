package com.gogidix.rapidassist.ai.computervision.domain.repository;

import com.gogidix.rapidassist.ai.computervision.domain.model.FaceDetection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FaceDetection
 * This is a domain interface - implemented in infrastructure layer
 */
public interface FaceDetectionRepositoryPort {

    /**
     * Save face detection
     */
    FaceDetection save(FaceDetection detection);

    /**
     * Find face detection by ID
     */
    Optional<FaceDetection> findById(UUID id);

    /**
     * Find face detections by image analysis ID
     */
    List<FaceDetection> findByImageAnalysisId(UUID imageAnalysisId);

    /**
     * Find face detections by tenant ID
     */
    List<FaceDetection> findByTenantId(String tenantId);

    /**
     * Find face detections by emotion
     */
    List<FaceDetection> findByEmotion(String emotion);

    /**
     * Delete face detection by ID
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
