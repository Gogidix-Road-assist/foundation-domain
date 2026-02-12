package com.gogidix.rapidassist.ai.anomaly.domain.model;

/**
 * Enum representing types of alerts.
 */
public enum AlertType {

    EMAIL("Email", "Email notification"),
    SMS("SMS", "SMS notification"),
    WEBHOOK("Webhook", "Webhook notification"),
    IN_APP("In-App", "In-app notification"),
    SLACK("Slack", "Slack notification"),
    TEAMS("Teams", "Microsoft Teams notification"),
    PAGER_DUTY("PagerDuty", "PagerDuty notification"),
    CUSTOM("Custom", "Custom notification type");

    private final String code;
    private final String description;

    AlertType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
