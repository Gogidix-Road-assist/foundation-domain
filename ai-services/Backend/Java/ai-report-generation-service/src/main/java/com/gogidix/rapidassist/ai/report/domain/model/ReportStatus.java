package com.gogidix.rapidassist.ai.report.domain.model;

/**
 * Enum representing the status of a report generation request.
 */
public enum ReportStatus {
    PENDING("Report generation is queued"),
    IN_PROGRESS("Report is being generated"),
    COMPLETED("Report generated successfully"),
    FAILED("Report generation failed"),
    CANCELLED("Report generation was cancelled"),
    SCHEDULED("Report is scheduled for future generation"),
    DISTRIBUTING("Report is being distributed"),
    ARCHIVED("Report has been archived");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
