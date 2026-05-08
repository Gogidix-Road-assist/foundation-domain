package com.gogidix.rapidassist.service.health.monitor.service.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class ReportServiceHealthRequest {

    @NotBlank
    private String serviceName;

    private String instanceId;

    @NotBlank
    private String status;

    private Map<String, String> details;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}
