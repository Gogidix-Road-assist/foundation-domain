package com.gogidix.rapidassist.ai.imagerecognition.application.service;

import com.gogidix.rapidassist.ai.imagerecognition.application.command.CreateImageRecognitionCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.command.ProcessImageRecognitionCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.command.UpdateRecognitionStatusCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.dto.ImageRecognitionDto;
import com.gogidix.rapidassist.ai.imagerecognition.application.mapper.ImageRecognitionMapper;
import com.gogidix.rapidassist.ai.imagerecognition.application.port.in.ImageRecognitionUseCase;
import com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate.ImageRecognition;
import com.gogidix.rapidassist.ai.imagerecognition.domain.exception.ImageRecognitionNotFoundException;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.*;
import com.gogidix.rapidassist.ai.imagerecognition.domain.repository.ImageRecognitionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application service for Image Recognition operations.
 * Implements the use case port and orchestrates business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageRecognitionApplicationService implements ImageRecognitionUseCase {

    private final ImageRecognitionRepositoryPort repository;
    private final ImageRecognitionMapper mapper;

    @Override
    @Transactional
    public ImageRecognitionDto createRecognition(CreateImageRecognitionCommand command) {
        log.info("Creating image recognition for tenant: {}, user: {}, type: {}",
                 command.getTenantId(), command.getUserId(), command.getRecognitionType());

        // Initialize recognition
        ImageRecognition recognition = ImageRecognition.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getImageUrl(),
                command.getRecognitionType()
        );

        // Set additional fields
        if (command.getImageStoragePath() != null) {
            recognition.setImageStoragePath(command.getImageStoragePath());
        }

        if (command.getMetadata() != null) {
            recognition.setMetadata(command.getMetadata());
        }

        // Save
        ImageRecognition saved = repository.save(command.getTenantId(), recognition);

        log.info("Created image recognition: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageRecognitionDto getRecognition(String tenantId, UUID id) {
        log.info("Getting image recognition: {} for tenant: {}", id, tenantId);

        return repository.findById(tenantId, id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ImageRecognitionNotFoundException(tenantId, id));
    }

    @Override
    @Transactional(readOnly = true)
    public ImageRecognitionDto getRecognitionByRequestId(String tenantId, String requestId) {
        log.info("Getting image recognition by request ID: {} for tenant: {}", requestId, tenantId);

        return repository.findByRequestId(tenantId, requestId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ImageRecognitionNotFoundException(tenantId, requestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageRecognitionDto> getTenantRecognitions(String tenantId) {
        log.info("Getting all image recognitions for tenant: {}", tenantId);

        return repository.findByTenantId(tenantId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageRecognitionDto> getUserRecognitions(String tenantId, String userId) {
        log.info("Getting image recognitions for user: {} in tenant: {}", userId, tenantId);

        return repository.findByUserId(tenantId, userId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageRecognitionDto> getRecognitionsByStatus(String tenantId, String status) {
        log.info("Getting image recognitions by status: {} for tenant: {}", status, tenantId);

        return repository.findByStatus(tenantId, status).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ImageRecognitionDto processRecognition(ProcessImageRecognitionCommand command) {
        log.info("Processing image recognition: {} for tenant: {}", command.getRecognitionId(), command.getTenantId());

        // Get recognition
        ImageRecognition recognition = repository.findById(command.getTenantId(), command.getRecognitionId())
                .orElseThrow(() -> new ImageRecognitionNotFoundException(command.getTenantId(), command.getRecognitionId()));

        // Start processing
        long startTime = System.currentTimeMillis();
        recognition.startProcessing();

        // Set AI model
        if (command.getAiModelUsed() != null) {
            recognition.setAiModelUsed(command.getAiModelUsed());
        }

        // Process results (simulated - in real implementation, this would call AI service)
        if (command.getResults() != null) {
            processResults(recognition, command.getResults());
        }

        // Complete processing
        recognition.completeProcessing();
        recognition.setProcessingTime(startTime);

        // Save
        ImageRecognition saved = repository.save(command.getTenantId(), recognition);

        log.info("Processed image recognition: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ImageRecognitionDto updateStatus(UpdateRecognitionStatusCommand command) {
        log.info("Updating status of image recognition: {} to {} for tenant: {}",
                 command.getRecognitionId(), command.getStatus(), command.getTenantId());

        // Get recognition
        ImageRecognition recognition = repository.findById(command.getTenantId(), command.getRecognitionId())
                .orElseThrow(() -> new ImageRecognitionNotFoundException(command.getTenantId(), command.getRecognitionId()));

        // Update status based on command
        switch (command.getStatus()) {
            case PROCESSING -> {
                if (recognition.getStatus() == RecognitionStatus.PENDING) {
                    recognition.startProcessing();
                }
            }
            case COMPLETED -> {
                if (recognition.getStatus() == RecognitionStatus.PROCESSING) {
                    recognition.completeProcessing();
                }
            }
            case FAILED -> {
                if (recognition.getStatus() == RecognitionStatus.PROCESSING) {
                    recognition.failProcessing(command.getErrorMessage());
                }
            }
            case CANCELLED -> {
                recognition.cancel();
            }
            default -> {
                log.warn("Invalid status transition to: {}", command.getStatus());
            }
        }

        // Save
        ImageRecognition saved = repository.save(command.getTenantId(), recognition);

        log.info("Updated status of image recognition: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteRecognition(String tenantId, UUID id) {
        log.info("Deleting image recognition: {} for tenant: {}", id, tenantId);

        if (!repository.exists(tenantId, id)) {
            throw new ImageRecognitionNotFoundException(tenantId, id);
        }

        repository.delete(tenantId, id);

        log.info("Deleted image recognition: {}", id);
    }

    @Override
    @Transactional
    public ImageRecognitionDto cancelRecognition(String tenantId, UUID id) {
        log.info("Cancelling image recognition: {} for tenant: {}", id, tenantId);

        // Get recognition
        ImageRecognition recognition = repository.findById(tenantId, id)
                .orElseThrow(() -> new ImageRecognitionNotFoundException(tenantId, id));

        // Cancel
        recognition.cancel();

        // Save
        ImageRecognition saved = repository.save(tenantId, recognition);

        log.info("Cancelled image recognition: {}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Process recognition results from AI service.
     * This is a simplified implementation.
     */
    private void processResults(ImageRecognition recognition, java.util.Map<String, Object> results) {
        // Extract recognized objects
        java.util.List<java.util.Map<String, Object>> objects =
                (java.util.List<java.util.Map<String, Object>>) results.get("objects");
        if (objects != null) {
            for (java.util.Map<String, Object> objData : objects) {
                RecognizedObject obj = RecognizedObject.builder()
                        .label((String) objData.get("label"))
                        .objectType((String) objData.get("objectType"))
                        .confidence(((Number) objData.getOrDefault("confidence", 0.0)).doubleValue())
                        .boundingBox((BoundingBox) objData.get("boundingBox"))
                        .attributes((java.util.Map<String, Object>) objData.getOrDefault("attributes", new java.util.HashMap<>()))
                        .build();
                recognition.addRecognizedObject(obj);
            }
        }

        // Extract scene labels
        java.util.List<java.util.Map<String, Object>> labels =
                (java.util.List<java.util.Map<String, Object>>) results.get("sceneLabels");
        if (labels != null) {
            for (java.util.Map<String, Object> labelData : labels) {
                SceneLabel label = SceneLabel.builder()
                        .label((String) labelData.get("label"))
                        .category((String) labelData.get("category"))
                        .confidence(((Number) labelData.getOrDefault("confidence", 0.0)).doubleValue())
                        .tags((java.util.List<String>) labelData.getOrDefault("tags", new java.util.ArrayList<>()))
                        .description((String) labelData.get("description"))
                        .build();
                recognition.addSceneLabel(label);
            }
        }

        // Extract brand detections
        java.util.List<java.util.Map<String, Object>> brands =
                (java.util.List<java.util.Map<String, Object>>) results.get("brandDetections");
        if (brands != null) {
            for (java.util.Map<String, Object> brandData : brands) {
                BrandDetection brand = BrandDetection.builder()
                        .brandName((String) brandData.get("brandName"))
                        .logoVariant((String) brandData.get("logoVariant"))
                        .confidence(((Number) brandData.getOrDefault("confidence", 0.0)).doubleValue())
                        .boundingBox((BoundingBox) brandData.get("boundingBox"))
                        .attributes((java.util.Map<String, Object>) brandData.getOrDefault("attributes", new java.util.HashMap<>()))
                        .build();
                recognition.addBrandDetection(brand);
            }
        }

        // Extract image features
        java.util.List<java.util.Map<String, Object>> features =
                (java.util.List<java.util.Map<String, Object>>) results.get("imageFeatures");
        if (features != null) {
            for (java.util.Map<String, Object> featureData : features) {
                ImageFeature feature = ImageFeature.builder()
                        .featureType((String) featureData.get("featureType"))
                        .featureName((String) featureData.get("featureName"))
                        .featureValue(((Number) featureData.getOrDefault("featureValue", 0.0)).doubleValue())
                        .featureVector((java.util.List<Double>) featureData.get("featureVector"))
                        .metadata((java.util.Map<String, Object>) featureData.getOrDefault("metadata", new java.util.HashMap<>()))
                        .build();
                recognition.addImageFeature(feature);
            }
        }
    }
}
