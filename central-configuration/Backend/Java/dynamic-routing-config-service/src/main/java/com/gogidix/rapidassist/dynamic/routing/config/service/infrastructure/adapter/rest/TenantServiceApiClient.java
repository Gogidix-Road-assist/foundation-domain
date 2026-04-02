package com.gogidix.rapidassist.dynamic.routing.config.service.infrastructure.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * REST client for interacting with the Tenant Service.
 *
 * <p>This client is used to validate tenant IDs and retrieve tenant
 * configuration information.
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
        this.restTemplate = new RestTemplate();
        this.tenantServiceBaseUrl = tenantServiceBaseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Validates if a tenant exists and is active.
     *
     * @param tenantId the tenant ID to validate
     * @return true if tenant exists and is active
     */
    public boolean isTenantActive(String tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/status";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                String status = (String) response.get("status");
                return "active".equalsIgnoreCase(status);
            }
            return false;
        } catch (Exception e) {
            logger.warn("Failed to validate tenant status for: {}", tenantId, e);
            return false;
        }
    }

    /**
     * Retrieves tenant configuration.
     *
     * @param tenantId the tenant ID
     * @return optional map of tenant configuration
     */
    public Optional<Map<String, Object>> getTenantConfig(String tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/config";
            @SuppressWarnings("unchecked")
            Map<String, Object> config = restTemplate.getForObject(url, Map.class);
            return Optional.ofNullable(config);
        } catch (Exception e) {
            logger.warn("Failed to retrieve tenant config for: {}", tenantId, e);
            return Optional.empty();
        }
    }

    /**
     * Validates if a tenant has access to a specific environment.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment to check
     * @return true if tenant has access
     */
    public boolean hasAccessToEnvironment(String tenantId, String environment) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/environments/" + environment;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("hasAccess"));
        } catch (Exception e) {
            logger.warn("Failed to validate environment access for tenant: {}, env: {}",
                tenantId, environment, e);
            return false;
        }
    }
}
