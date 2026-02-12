package com.gogidix.rapidassist.ai.summarization.domain.exception;

import java.util.UUID;

public class SummarizationRequestNotFoundException extends RuntimeException {
    public SummarizationRequestNotFoundException(UUID id) {
        super("Summarization request not found: " + id);
    }

    public SummarizationRequestNotFoundException(String requestId, String tenantId) {
        super("Summarization request not found: " + requestId + " for tenant: " + tenantId);
    }
}
