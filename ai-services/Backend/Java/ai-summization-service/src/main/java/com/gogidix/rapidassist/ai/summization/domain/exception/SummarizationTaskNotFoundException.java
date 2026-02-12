package com.gogidix.rapidassist.ai.summization.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a summarization task is not found.
 */
public class SummarizationTaskNotFoundException extends RuntimeException {

    public SummarizationTaskNotFoundException(UUID taskId, String tenantId) {
        super(String.format("Summarization task not found: %s for tenant: %s", taskId, tenantId));
    }

    public SummarizationTaskNotFoundException(String message) {
        super(message);
    }
}
