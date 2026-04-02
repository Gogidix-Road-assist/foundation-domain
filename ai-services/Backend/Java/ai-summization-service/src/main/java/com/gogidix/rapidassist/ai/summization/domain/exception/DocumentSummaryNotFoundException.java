package com.gogidix.rapidassist.ai.summization.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a document summary is not found.
 */
public class DocumentSummaryNotFoundException extends RuntimeException {

    public DocumentSummaryNotFoundException(UUID summaryId, String tenantId) {
        super(String.format("Document summary not found: %s for tenant: %s", summaryId, tenantId));
    }

    public DocumentSummaryNotFoundException(String message) {
        super(message);
    }
}
