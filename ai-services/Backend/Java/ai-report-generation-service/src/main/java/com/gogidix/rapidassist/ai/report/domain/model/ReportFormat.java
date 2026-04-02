package com.gogidix.rapidassist.ai.report.domain.model;

/**
 * Enum representing supported report output formats.
 */
public enum ReportFormat {
    PDF("Portable Document Format"),
    EXCEL("Microsoft Excel (.xlsx)"),
    CSV("Comma Separated Values"),
    HTML("HyperText Markup Language");

    private final String description;

    ReportFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
