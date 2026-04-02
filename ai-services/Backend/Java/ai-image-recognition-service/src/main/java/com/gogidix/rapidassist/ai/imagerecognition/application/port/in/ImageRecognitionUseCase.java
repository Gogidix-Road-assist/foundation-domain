package com.gogidix.rapidassist.ai.imagerecognition.application.port.in;

import com.gogidix.rapidassist.ai.imagerecognition.application.command.CreateImageRecognitionCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.command.ProcessImageRecognitionCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.command.UpdateRecognitionStatusCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.dto.ImageRecognitionDto;

import java.util.List;
import java.util.UUID;

/**
 * Use case interface for Image Recognition operations.
 */
public interface ImageRecognitionUseCase {

    /**
     * Create a new image recognition request.
     */
    ImageRecognitionDto createRecognition(CreateImageRecognitionCommand command);

    /**
     * Get image recognition by ID.
     */
    ImageRecognitionDto getRecognition(String tenantId, UUID id);

    /**
     * Get image recognition by request ID.
     */
    ImageRecognitionDto getRecognitionByRequestId(String tenantId, String requestId);

    /**
     * Get all image recognitions for a tenant.
     */
    List<ImageRecognitionDto> getTenantRecognitions(String tenantId);

    /**
     * Get image recognitions by user ID.
     */
    List<ImageRecognitionDto> getUserRecognitions(String tenantId, String userId);

    /**
     * Get image recognitions by status.
     */
    List<ImageRecognitionDto> getRecognitionsByStatus(String tenantId, String status);

    /**
     * Process image recognition.
     */
    ImageRecognitionDto processRecognition(ProcessImageRecognitionCommand command);

    /**
     * Update recognition status.
     */
    ImageRecognitionDto updateStatus(UpdateRecognitionStatusCommand command);

    /**
     * Delete image recognition.
     */
    void deleteRecognition(String tenantId, UUID id);

    /**
     * Cancel image recognition.
     */
    ImageRecognitionDto cancelRecognition(String tenantId, UUID id);
}
