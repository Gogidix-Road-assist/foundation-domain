package com.gogidix.rapidassist.ai.summarization.domain.exception;

import java.util.UUID;

public class SummaryNotFoundException extends RuntimeException {
    public SummaryNotFoundException(UUID id) {
        super("Summary not found: " + id);
    }

    public SummaryNotFoundException(UUID id, String tenantId) {
        super("Summary not found: " + id + " for tenant: " + tenantId);
    }
}
