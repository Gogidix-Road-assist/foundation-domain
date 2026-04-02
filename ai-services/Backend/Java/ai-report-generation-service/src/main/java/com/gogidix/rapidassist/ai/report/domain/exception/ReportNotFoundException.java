package com.gogidix.rapidassist.ai.report.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a report is not found.
 */
public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(UUID reportId, String tenantId) {
        super("Report not found: " + reportId + " for tenant: " + tenantId);
    }

    public ReportNotFoundException(String message) {
        super(message);
    }
}
