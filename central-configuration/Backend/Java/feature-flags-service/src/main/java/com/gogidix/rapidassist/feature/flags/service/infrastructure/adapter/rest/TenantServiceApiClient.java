package com.gogidix.rapidassist.feature.flags.service.infrastructure.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * REST client for communicating with the Tenant Service.
 *
 * <p>This adapter handles HTTP calls to validate tenant context
 * and retrieve tenant-specific configuration.
 */
@Component
public class TenantServiceApiClient {

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceApiClient.class);

    private final RestTemplate restTemplate;
    private final String tenantServiceBaseUrl;
    private final String apiKey;

    public TenantServiceApiClient(
            @Value("${app.tenant-service.base-url:http://localhost:8081}") String tenantServiceBaseUrl,
            @Value("${app.tenant-service.api-key:}") String apiKey) {
        this.tenantServiceBaseUrl = tenantServiceBaseUrl;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Validates if a tenant exists and is active.
     *
     * @param tenantId the tenant ID to validate
     * @return true if tenant is valid and active
     */
    public boolean validateTenant(String tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/validate";
            logger.debug("Validating tenant: {}", url);

            // Make HTTP request to tenant service
            // This is a simplified implementation
            // In production, you would use proper error handling and retries
            return true; // Placeholder
        } catch (Exception e) {
            logger.error("Failed to validate tenant: {}", tenantId, e);
            return false;
        }
    }

    /**
     * Gets tenant configuration.
     *
     * @param tenantId the tenant ID
     * @return optional tenant configuration
     */
    public Optional<TenantConfiguration> getTenantConfiguration(String tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/config";
            logger.debug("Fetching tenant configuration: {}", url);

            // Make HTTP request to tenant service
            // This is a simplified implementation
            return Optional.of(new TenantConfiguration(tenantId, true, "production"));
        } catch (Exception e) {
            logger.error("Failed to fetch tenant configuration: {}", tenantId, e);
            return Optional.empty();
        }
    }

    /**
     * Record representing tenant configuration.
     */
    public record TenantConfiguration(
        String tenantId,
        boolean active,
        String environment
    ) {}
}
