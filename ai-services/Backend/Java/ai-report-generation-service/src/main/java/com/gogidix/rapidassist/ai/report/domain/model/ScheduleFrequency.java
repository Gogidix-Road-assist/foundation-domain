package com.gogidix.rapidassist.ai.report.domain.model;

/**
 * Enum representing report schedule frequencies.
 */
public enum ScheduleFrequency {
    ONCE("Run once at specified time"),
    HOURLY("Run every hour"),
    DAILY("Run every day"),
    WEEKLY("Run every week"),
    BIWEEKLY("Run every two weeks"),
    MONTHLY("Run every month"),
    QUARTERLY("Run every quarter"),
    YEARLY("Run every year"),
    CUSTOM("Custom cron expression");

    private final String description;

    ScheduleFrequency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
