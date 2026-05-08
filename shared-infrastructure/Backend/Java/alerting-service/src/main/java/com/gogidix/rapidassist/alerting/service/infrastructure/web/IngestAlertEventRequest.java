package com.gogidix.rapidassist.alerting.service.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class IngestAlertEventRequest {

    private String correlationId;

    private String ruleId;

    @NotBlank
    private String severity;

    @NotBlank
    private String message;

    private Map<String, String> attributes;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
