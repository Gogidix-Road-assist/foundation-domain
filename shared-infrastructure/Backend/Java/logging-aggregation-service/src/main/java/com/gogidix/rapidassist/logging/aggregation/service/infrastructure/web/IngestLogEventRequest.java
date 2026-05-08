package com.gogidix.rapidassist.logging.aggregation.service.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class IngestLogEventRequest {

    @NotBlank
    private String level;

    @NotBlank
    private String message;

    private String logger;

    private String correlationId;

    private Map<String, String> attributes;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
