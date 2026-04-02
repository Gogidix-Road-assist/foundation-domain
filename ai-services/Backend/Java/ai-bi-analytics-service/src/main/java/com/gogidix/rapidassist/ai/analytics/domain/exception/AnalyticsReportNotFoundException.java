package com.gogidix.rapidassist.ai.analytics.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an analytics report is not found
 */
public class AnalyticsReportNotFoundException extends AnalyticsException {

    public AnalyticsReportNotFoundException(UUID reportId) {
        super("Analytics report not found with ID: " + reportId);
    }

    public AnalyticsReportNotFoundException(String message) {
        super(message);
    }
}
