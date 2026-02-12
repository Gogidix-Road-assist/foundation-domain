package com.gogidix.rapidassist.orchestration.reporting.infrastructure.reportengine;

/**
 * Exception thrown when report generation fails
 */
public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
