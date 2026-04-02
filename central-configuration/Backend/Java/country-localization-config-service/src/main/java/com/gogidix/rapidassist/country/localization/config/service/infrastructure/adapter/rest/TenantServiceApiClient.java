package com.gogidix.rapidassist.country.localization.config.service.infrastructure.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * REST client for communicating with the Tenant Service.
 *
 * <p>This adapter is used to validate tenant IDs and retrieve tenant information.
 */
@Component
public class TenantServiceApiClient {

    private static final Logger log = LoggerFactory.getLogger(TenantServiceApiClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public TenantServiceApiClient(
            RestTemplate restTemplate,
            @Value("${app.tenant-service.base-url:http://localhost:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Validates if a tenant exists.
     *
     * @param tenantId the tenant ID to validate
     * @return true if the tenant exists, false otherwise
     */
    public boolean validateTenant(String tenantId) {
        try {
            String url = baseUrl + "/api/v1/tenants/" + tenantId + "/exists";
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("Failed to validate tenant {}: {}", tenantId, e.getMessage());
            return false;
        }
    }

    /**
     * Gets tenant information.
     *
     * @param tenantId the tenant ID
     * @return tenant information if found, null otherwise
     */
    public TenantInfo getTenantInfo(String tenantId) {
        try {
            String url = baseUrl + "/api/v1/tenants/" + tenantId;
            return restTemplate.getForObject(url, TenantInfo.class);
        } catch (Exception e) {
            log.warn("Failed to get tenant info for {}: {}", tenantId, e.getMessage());
            return null;
        }
    }

    /**
     * Tenant information record.
     */
    public record TenantInfo(
        String id,
        String name,
        String status,
        String plan
    ) {}
}
