package com.gogidix.rapidassist.event.schemas.event;

/**
 * System-level events for configuration and monitoring.
 */
public class SystemEvents {

    /**
     * Event fired when system configuration is changed
     */
    public static class ConfigurationChanged extends DomainEvent {
        private String configKey;
        private String oldValue;
        private String newValue;
        private String configScope; // GLOBAL, TENANT, ORGANIZATION, SERVICE

        public ConfigurationChanged() {
            super();
            this.eventType = "ConfigurationChanged";
            this.aggregateType = "Configuration";
        }

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }

        public String getConfigScope() {
            return configScope;
        }

        public void setConfigScope(String configScope) {
            this.configScope = configScope;
        }
    }

    /**
     * Event fired when a feature flag is toggled
     */
    public static class FeatureFlagToggled extends DomainEvent {
        private String flagName;
        private Boolean enabled;
        private String description;
        private String targetScope; // GLOBAL, TENANT, USER_SEGMENT

        public FeatureFlagToggled() {
            super();
            this.eventType = "FeatureFlagToggled";
            this.aggregateType = "FeatureFlag";
        }

        public String getFlagName() {
            return flagName;
        }

        public void setFlagName(String flagName) {
            this.flagName = flagName;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTargetScope() {
            return targetScope;
        }

        public void setTargetScope(String targetScope) {
            this.targetScope = targetScope;
        }
    }

    /**
     * Event fired when a policy is updated
     */
    public static class PolicyUpdated extends DomainEvent {
        private String policyName;
        private String policyType;
        private String version;

        public PolicyUpdated() {
            super();
            this.eventType = "PolicyUpdated";
            this.aggregateType = "Policy";
        }

        public String getPolicyName() {
            return policyName;
        }

        public void setPolicyName(String policyName) {
            this.policyName = policyName;
        }

        public String getPolicyType() {
            return policyType;
        }

        public void setPolicyType(String policyType) {
            this.policyType = policyType;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    /**
     * Event fired when an alert is triggered
     */
    public static class AlertTriggered extends DomainEvent {
        private String alertType;
        private String severity; // INFO, WARNING, ERROR, CRITICAL
        private String alertMessage;
        private String sourceService;
        private String alertCategory; // PERFORMANCE, SECURITY, BUSINESS, SYSTEM

        public AlertTriggered() {
            super();
            this.eventType = "AlertTriggered";
            this.aggregateType = "Alert";
        }

        public String getAlertType() {
            return alertType;
        }

        public void setAlertType(String alertType) {
            this.alertType = alertType;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getAlertMessage() {
            return alertMessage;
        }

        public void setAlertMessage(String alertMessage) {
            this.alertMessage = alertMessage;
        }

        public String getSourceService() {
            return sourceService;
        }

        public void setSourceService(String sourceService) {
            this.sourceService = sourceService;
        }

        public String getAlertCategory() {
            return alertCategory;
        }

        public void setAlertCategory(String alertCategory) {
            this.alertCategory = alertCategory;
        }
    }

    /**
     * Event fired when an alert is resolved
     */
    public static class AlertResolved extends DomainEvent {
        private String alertId;
        private String alertType;
        private String resolutionNotes;
        private String resolvedBy;

        public AlertResolved() {
            super();
            this.eventType = "AlertResolved";
            this.aggregateType = "Alert";
        }

        public String getAlertId() {
            return alertId;
        }

        public void setAlertId(String alertId) {
            this.alertId = alertId;
        }

        public String getAlertType() {
            return alertType;
        }

        public void setAlertType(String alertType) {
            this.alertType = alertType;
        }

        public String getResolutionNotes() {
            return resolutionNotes;
        }

        public void setResolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
        }

        public String getResolvedBy() {
            return resolvedBy;
        }

        public void setResolvedBy(String resolvedBy) {
            this.resolvedBy = resolvedBy;
        }
    }

    /**
     * Event fired when system health changes
     */
    public static class SystemHealthChanged extends DomainEvent {
        private String serviceName;
        private String oldStatus;
        private String newStatus;
        private String healthCheckType;

        public SystemHealthChanged() {
            super();
            this.eventType = "SystemHealthChanged";
            this.aggregateType = "SystemHealth";
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getOldStatus() {
            return oldStatus;
        }

        public void setOldStatus(String oldStatus) {
            this.oldStatus = oldStatus;
        }

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public String getHealthCheckType() {
            return healthCheckType;
        }

        public void setHealthCheckType(String healthCheckType) {
            this.healthCheckType = healthCheckType;
        }
    }

    /**
     * Event fired for scheduled tasks
     */
    public static class ScheduledTaskExecuted extends DomainEvent {
        private String taskName;
        private String taskType;
        private Boolean success;
        private String errorMessage;
        private Integer executionDurationMs;

        public ScheduledTaskExecuted() {
            super();
            this.eventType = "ScheduledTaskExecuted";
            this.aggregateType = "ScheduledTask";
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Integer getExecutionDurationMs() {
            return executionDurationMs;
        }

        public void setExecutionDurationMs(Integer executionDurationMs) {
            this.executionDurationMs = executionDurationMs;
        }
    }
}
