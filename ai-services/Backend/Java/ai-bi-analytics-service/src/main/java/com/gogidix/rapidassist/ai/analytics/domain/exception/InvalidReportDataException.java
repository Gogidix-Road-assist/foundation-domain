package com.gogidix.rapidassist.ai.analytics.domain.exception;

/**
 * Exception thrown when report data is invalid
 */
public class InvalidReportDataException extends AnalyticsException {

    public InvalidReportDataException(String message) {
        super(message);
    }

    public InvalidReportDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
