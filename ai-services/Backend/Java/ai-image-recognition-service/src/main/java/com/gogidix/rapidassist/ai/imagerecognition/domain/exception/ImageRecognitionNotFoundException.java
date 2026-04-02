package com.gogidix.rapidassist.ai.imagerecognition.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an image recognition is not found.
 */
public class ImageRecognitionNotFoundException extends RuntimeException {

    public ImageRecognitionNotFoundException(String message) {
        super(message);
    }

    public ImageRecognitionNotFoundException(String tenantId, UUID id) {
        super(String.format("Image recognition not found: tenantId=%s, id=%s", tenantId, id));
    }

    public ImageRecognitionNotFoundException(String tenantId, String requestId) {
        super(String.format("Image recognition not found: tenantId=%s, requestId=%s", tenantId, requestId));
    }
}
