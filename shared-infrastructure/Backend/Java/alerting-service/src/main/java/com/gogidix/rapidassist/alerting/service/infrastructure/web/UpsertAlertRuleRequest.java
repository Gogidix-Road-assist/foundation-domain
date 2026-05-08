package com.gogidix.rapidassist.alerting.service.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class UpsertAlertRuleRequest {

    @NotBlank
    private String ruleId;

    @NotBlank
    private String name;

    @NotBlank
    private String severity;

    private boolean enabled = true;

    private Map<String, String> conditions;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, String> conditions) {
        this.conditions = conditions;
    }
}
