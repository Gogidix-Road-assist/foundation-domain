package com.gogidix.rapidassist.ai.imagerecognition.domain.repository;

import com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate.ImageRecognition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ImageRecognition aggregate.
 */
public interface ImageRecognitionRepositoryPort {

    ImageRecognition save(String tenantId, ImageRecognition imageRecognition);

    Optional<ImageRecognition> findById(String tenantId, UUID id);

    Optional<ImageRecognition> findByRequestId(String tenantId, String requestId);

    List<ImageRecognition> findByTenantId(String tenantId);

    List<ImageRecognition> findByUserId(String tenantId, String userId);

    List<ImageRecognition> findByStatus(String tenantId, String status);

    void delete(String tenantId, UUID id);

    boolean exists(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
