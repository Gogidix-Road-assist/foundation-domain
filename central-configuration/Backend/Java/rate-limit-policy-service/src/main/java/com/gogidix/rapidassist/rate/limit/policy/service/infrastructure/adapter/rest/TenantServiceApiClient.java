package com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * REST API client for communicating with the Tenant Service.
 *
 * <p>This adapter allows the rate limit policy service to validate tenant IDs
 * and fetch tenant configuration from the central tenant service.
 */
@Component
public class TenantServiceApiClient {

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceApiClient.class);

    private final RestTemplate restTemplate;
    private final String tenantServiceBaseUrl;
    private final String apiKey;

    public TenantServiceApiClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${app.tenant-service.base-url:http://tenant-service:8080}") String baseUrl,
            @Value("${app.tenant-service.api-key:}") String apiKey) {
        this.tenantServiceBaseUrl = baseUrl;
        this.apiKey = apiKey;
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(10))
            .build();
    }

    /**
     * Validates if a tenant exists and is active.
     *
     * @param tenantId the tenant ID to validate
     * @return true if the tenant is active, false otherwise
     */
    public boolean isTenantActive(String tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/status";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("X-API-Key", apiKey);
            }

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            Map<String, Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Map.class
            ).getBody();

            if (response != null) {
                String status = (String) response.get("status");
                return "ACTIVE".equalsIgnoreCase(status);
            }

            return false;
        } catch (Exception e) {
            logger.warn("Failed to validate tenant status for {}: {}", tenantId, e.getMessage());
            return false;
        }
    }

    /**
     * Fetches tenant configuration.
     *
     * @param tenantId the tenant ID
     * @return the tenant configuration if found
     */
    public Optional<TenantConfiguration> getTenantConfiguration(String tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("X-API-Key", apiKey);
            }

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            Map<String, Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Map.class
            ).getBody();

            if (response != null) {
                return Optional.of(new TenantConfiguration(
                    (String) response.get("id"),
                    (String) response.get("name"),
                    (String) response.get("status"),
                    (Boolean) response.getOrDefault("active", true),
                    (Map<String, Object>) response.get("settings")
                ));
            }

            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to fetch tenant configuration for {}: {}", tenantId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if a tenant has a specific feature enabled.
     *
     * @param tenantId  the tenant ID
     * @param feature   the feature name
     * @return true if the feature is enabled
     */
    public boolean hasFeature(String tenantId, String feature) {
        return getTenantConfiguration(tenantId)
            .map(config -> config.features().contains(feature))
            .orElse(false);
    }

    /**
     * Records a policy access for auditing.
     *
     * @param tenantId    the tenant ID
     * @param policyKey   the policy key accessed
     * @param accessedBy  the user who accessed it
     */
    public void recordAccess(String tenantId, String policyKey, String accessedBy) {
        try {
            String url = tenantServiceBaseUrl + "/api/v1/tenants/" + tenantId + "/audit";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("X-API-Key", apiKey);
            }

            Map<String, String> auditEntry = Map.of(
                "action", "POLICY_ACCESS",
                "resource", "policy:" + policyKey,
                "userId", accessedBy
            );

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(auditEntry, headers);

            restTemplate.postForEntity(url, requestEntity, String.class);
        } catch (Exception e) {
            // Don't fail the request if audit fails
            logger.debug("Failed to record audit for tenant {}: {}", tenantId, e.getMessage());
        }
    }

    /**
     * Tenant configuration record.
     *
     * @param id       the tenant ID
     * @param name     the tenant name
     * @param status   the tenant status
     * @param active   whether the tenant is active
     * @param settings additional settings
     */
    public record TenantConfiguration(
        String id,
        String name,
        String status,
        boolean active,
        Map<String, Object> settings
    ) {
        @SuppressWarnings("unchecked")
        public java.util.Set<String> features() {
            if (settings == null) {
                return java.util.Set.of();
            }
            Object features = settings.get("features");
            if (features instanceof java.util.Collection<?> c) {
                return (java.util.Set<String>) c.stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toSet());
            }
            return java.util.Set.of();
        }
    }
}
