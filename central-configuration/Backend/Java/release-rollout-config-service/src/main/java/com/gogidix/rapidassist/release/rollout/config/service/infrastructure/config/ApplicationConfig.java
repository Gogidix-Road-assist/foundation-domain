package com.gogidix.rapidassist.release.rollout.config.service.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Application-level configuration properties.
 *
 * <p>This configuration class captures custom application properties
 * that are not covered by Spring Boot's auto-configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "app.rollout")
public class ApplicationConfig {

    private String environment = "development";
    private TenantConfig tenant = new TenantConfig();
    private CacheConfig cache = new CacheConfig();
    private ValidationConfig validation = new ValidationConfig();
    private EventConfig events = new EventConfig();

    // Getters and setters
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public TenantConfig getTenant() {
        return tenant;
    }

    public void setTenant(TenantConfig tenant) {
        this.tenant = tenant;
    }

    public CacheConfig getCache() {
        return cache;
    }

    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }

    public ValidationConfig getValidation() {
        return validation;
    }

    public void setValidation(ValidationConfig validation) {
        this.validation = validation;
    }

    public EventConfig getEvents() {
        return events;
    }

    public void setEvents(EventConfig events) {
        this.events = events;
    }

    /**
     * Tenant-specific configuration.
     */
    public static class TenantConfig {
        private boolean enforceIsolation = true;
        private boolean requireTenantId = true;
        private List<String> allowedOrigins = List.of("http://localhost:3000");

        public boolean isEnforceIsolation() {
            return enforceIsolation;
        }

        public void setEnforceIsolation(boolean enforceIsolation) {
            this.enforceIsolation = enforceIsolation;
        }

        public boolean isRequireTenantId() {
            return requireTenantId;
        }

        public void setRequireTenantId(boolean requireTenantId) {
            this.requireTenantId = requireTenantId;
        }

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    /**
     * Cache configuration.
     */
    public static class CacheConfig {
        private boolean enabled = true;
        private long ttlSeconds = 300;
        private long maxSize = 1000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }

        public long getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(long maxSize) {
            this.maxSize = maxSize;
        }
    }

    /**
     * Validation configuration.
     */
    public static class ValidationConfig {
        private boolean requireApprovalForProduction = true;
        private boolean validateBatchConfiguration = true;
        private boolean enforceEnvironmentConstraints = true;

        public boolean isRequireApprovalForProduction() {
            return requireApprovalForProduction;
        }

        public void setRequireApprovalForProduction(boolean requireApprovalForProduction) {
            this.requireApprovalForProduction = requireApprovalForProduction;
        }

        public boolean isValidateBatchConfiguration() {
            return validateBatchConfiguration;
        }

        public void setValidateBatchConfiguration(boolean validateBatchConfiguration) {
            this.validateBatchConfiguration = validateBatchConfiguration;
        }

        public boolean isEnforceEnvironmentConstraints() {
            return enforceEnvironmentConstraints;
        }

        public void setEnforceEnvironmentConstraints(boolean enforceEnvironmentConstraints) {
            this.enforceEnvironmentConstraints = enforceEnvironmentConstraints;
        }
    }

    /**
     * Event publishing configuration.
     */
    public static class EventConfig {
        private boolean enabled = true;
        private boolean asyncPublishing = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isAsyncPublishing() {
            return asyncPublishing;
        }

        public void setAsyncPublishing(boolean asyncPublishing) {
            this.asyncPublishing = asyncPublishing;
        }
    }
}
