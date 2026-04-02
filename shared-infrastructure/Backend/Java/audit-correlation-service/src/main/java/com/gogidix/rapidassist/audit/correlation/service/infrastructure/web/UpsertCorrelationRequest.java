package com.gogidix.rapidassist.audit.correlation.service.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class UpsertCorrelationRequest {

    @NotBlank
    private String correlationId;

    private Map<String, String> tags;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
