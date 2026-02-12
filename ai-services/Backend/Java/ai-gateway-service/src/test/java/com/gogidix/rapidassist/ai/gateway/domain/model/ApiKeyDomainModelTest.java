package com.gogidix.rapidassist.ai.gateway.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiKey domain model.
 * Tests business logic, builders, validation, and domain behaviors.
 */
@DisplayName("ApiKey Domain Model Tests")
class ApiKeyDomainModelTest {

    @Test
    @DisplayName("Should create ApiKey with all fields using builder")
    void shouldCreateApiKeyWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String keyName = "Test API Key";
        String apiKeyValue = "test-key-12345";
        String keyHash = "hash-123";
        ApiKeyStatus status = ApiKeyStatus.ACTIVE;
        String[] allowedServices = {"service1", "service2"};
        String[] allowedPaths = {"/api/v1/*", "/api/v2/*"};
        Integer rateLimit = 100;
        Integer rateLimitWindowSeconds = 60;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        String createdBy = "admin";
        String description = "Test API key";
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("environment", "test");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastUsedAt = LocalDateTime.now();
        Long usageCount = 50L;

        // When
        ApiKey apiKey = ApiKey.builder()
                .id(id)
                .tenantId(tenantId)
                .keyName(keyName)
                .apiKey(apiKeyValue)
                .keyHash(keyHash)
                .status(status)
                .allowedServices(allowedServices)
                .allowedPaths(allowedPaths)
                .rateLimit(rateLimit)
                .rateLimitWindowSeconds(rateLimitWindowSeconds)
                .expiresAt(expiresAt)
                .createdBy(createdBy)
                .description(description)
                .metadata(metadata)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastUsedAt(lastUsedAt)
                .usageCount(usageCount)
                .build();

        // Then
        assertNotNull(apiKey);
        assertEquals(id, apiKey.getId());
        assertEquals(tenantId, apiKey.getTenantId());
        assertEquals(keyName, apiKey.getKeyName());
        assertEquals(apiKeyValue, apiKey.getApiKey());
        assertEquals(keyHash, apiKey.getKeyHash());
        assertEquals(status, apiKey.getStatus());
        assertArrayEquals(allowedServices, apiKey.getAllowedServices());
        assertArrayEquals(allowedPaths, apiKey.getAllowedPaths());
        assertEquals(rateLimit, apiKey.getRateLimit());
        assertEquals(rateLimitWindowSeconds, apiKey.getRateLimitWindowSeconds());
        assertEquals(expiresAt, apiKey.getExpiresAt());
        assertEquals(createdBy, apiKey.getCreatedBy());
        assertEquals(description, apiKey.getDescription());
        assertEquals(metadata, apiKey.getMetadata());
        assertEquals(createdAt, apiKey.getCreatedAt());
        assertEquals(updatedAt, apiKey.getUpdatedAt());
        assertEquals(lastUsedAt, apiKey.getLastUsedAt());
        assertEquals(usageCount, apiKey.getUsageCount());
    }

    @Test
    @DisplayName("Should create ApiKey with required fields only")
    void shouldCreateApiKeyWithRequiredFieldsOnly() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";

        // When
        ApiKey apiKey = new ApiKey();
        apiKey.setId(id);
        apiKey.setTenantId(tenantId);

        // Then
        assertNotNull(apiKey);
        assertEquals(id, apiKey.getId());
        assertEquals(tenantId, apiKey.getTenantId());
    }

    @Test
    @DisplayName("Should create ApiKey using no-args constructor")
    void shouldCreateApiKeyWithNoArgsConstructor() {
        // When
        ApiKey apiKey = new ApiKey();

        // Then
        assertNotNull(apiKey);
        assertNull(apiKey.getId());
        assertNull(apiKey.getTenantId());
        assertNull(apiKey.getKeyName());
        assertNull(apiKey.getStatus());
        assertNull(apiKey.getUsageCount());
    }

    @Test
    @DisplayName("Should create ApiKey using all-args constructor")
    void shouldCreateApiKeyWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String keyName = "Test Key";
        String apiKey = "key-123";
        String keyHash = "hash-123";
        ApiKeyStatus status = ApiKeyStatus.ACTIVE;
        String[] allowedServices = {"service1"};
        String[] allowedPaths = {"/api/*"};
        Integer rateLimit = 100;
        Integer rateLimitWindowSeconds = 60;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        String createdBy = "admin";
        String description = "Test";
        Map<String, Object> metadata = new HashMap<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastUsedAt = LocalDateTime.now();
        Long usageCount = 10L;

        // When
        ApiKey key = new ApiKey(
                id, tenantId, keyName, apiKey, keyHash, status,
                allowedServices, allowedPaths, rateLimit, rateLimitWindowSeconds,
                expiresAt, createdBy, description, metadata,
                createdAt, updatedAt, lastUsedAt, usageCount
        );

        // Then
        assertNotNull(key);
        assertEquals(id, key.getId());
        assertEquals(tenantId, key.getTenantId());
        assertEquals(status, key.getStatus());
    }

    @Test
    @DisplayName("Should handle null values in optional fields")
    void shouldHandleNullValuesInOptionalFields() {
        // Given
        ApiKey apiKey = new ApiKey();
        apiKey.setId(UUID.randomUUID());
        apiKey.setTenantId("tenant-123");
        apiKey.setKeyName("Test Key");
        apiKey.setStatus(ApiKeyStatus.ACTIVE);

        // When & Then
        assertNotNull(apiKey);
        assertNull(apiKey.getApiKey());
        assertNull(apiKey.getKeyHash());
        assertNull(apiKey.getAllowedServices());
        assertNull(apiKey.getAllowedPaths());
        assertNull(apiKey.getRateLimit());
        assertNull(apiKey.getExpiresAt());
        assertNull(apiKey.getCreatedBy());
        assertNull(apiKey.getDescription());
        assertNull(apiKey.getMetadata());
        assertNull(apiKey.getCreatedAt());
        assertNull(apiKey.getUpdatedAt());
        assertNull(apiKey.getLastUsedAt());
        assertNull(apiKey.getUsageCount());
    }

    @Test
    @DisplayName("Should handle empty arrays")
    void shouldHandleEmptyArrays() {
        // Given
        ApiKey apiKey = new ApiKey();
        String[] emptyServices = {};
        String[] emptyPaths = {};

        // When
        apiKey.setAllowedServices(emptyServices);
        apiKey.setAllowedPaths(emptyPaths);

        // Then
        assertArrayEquals(emptyServices, apiKey.getAllowedServices());
        assertArrayEquals(emptyPaths, apiKey.getAllowedPaths());
        assertEquals(0, apiKey.getAllowedServices().length);
        assertEquals(0, apiKey.getAllowedPaths().length);
    }

    @Test
    @DisplayName("Should handle metadata map correctly")
    void shouldHandleMetadataMap() {
        // Given
        ApiKey apiKey = new ApiKey();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);
        metadata.put("key3", true);

        // When
        apiKey.setMetadata(metadata);

        // Then
        assertEquals(metadata, apiKey.getMetadata());
        assertEquals(3, apiKey.getMetadata().size());
        assertEquals("value1", apiKey.getMetadata().get("key1"));
        assertEquals(123, apiKey.getMetadata().get("key2"));
        assertEquals(true, apiKey.getMetadata().get("key3"));
    }

    @Test
    @DisplayName("Should handle null metadata")
    void shouldHandleNullMetadata() {
        // Given
        ApiKey apiKey = new ApiKey();

        // When
        apiKey.setMetadata(null);

        // Then
        assertNull(apiKey.getMetadata());
    }

    @Test
    @DisplayName("Should update all fields using setters")
    void shouldUpdateAllFieldsUsingSetters() {
        // Given
        ApiKey apiKey = new ApiKey();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("test", "value");

        // When
        apiKey.setId(id);
        apiKey.setTenantId("tenant-123");
        apiKey.setKeyName("Updated Key");
        apiKey.setApiKey("updated-key");
        apiKey.setKeyHash("updated-hash");
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        apiKey.setAllowedServices(new String[]{"service1"});
        apiKey.setAllowedPaths(new String[]{"/api/*"});
        apiKey.setRateLimit(200);
        apiKey.setRateLimitWindowSeconds(120);
        apiKey.setExpiresAt(now.plusDays(60));
        apiKey.setCreatedBy("user");
        apiKey.setDescription("Updated description");
        apiKey.setMetadata(metadata);
        apiKey.setCreatedAt(now);
        apiKey.setUpdatedAt(now);
        apiKey.setLastUsedAt(now);
        apiKey.setUsageCount(100L);

        // Then
        assertEquals(id, apiKey.getId());
        assertEquals("tenant-123", apiKey.getTenantId());
        assertEquals("Updated Key", apiKey.getKeyName());
        assertEquals(ApiKeyStatus.REVOKED, apiKey.getStatus());
        assertEquals(200, apiKey.getRateLimit());
        assertEquals(100L, apiKey.getUsageCount());
    }

    @Test
    @DisplayName("Should verify Lombok @Data annotations work correctly")
    void shouldVerifyLombokDataAnnotations() {
        // Given
        ApiKey key1 = new ApiKey();
        key1.setId(UUID.randomUUID());
        key1.setTenantId("tenant-123");
        key1.setKeyName("Test");
        key1.setStatus(ApiKeyStatus.ACTIVE);

        ApiKey key2 = new ApiKey();
        key2.setId(key1.getId());
        key2.setTenantId(key1.getTenantId());
        key2.setKeyName(key1.getKeyName());
        key2.setStatus(key1.getStatus());

        // When & Then - equals
        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());

        // When & Then - toString
        String toString = key1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ApiKey"));
        assertTrue(toString.contains("tenant-123") || toString.contains("tenantId"));
    }

    @Test
    @DisplayName("Should handle different ApiKeyStatus values")
    void shouldHandleDifferentApiKeyStatusValues() {
        // Given
        ApiKey activeKey = new ApiKey();
        activeKey.setStatus(ApiKeyStatus.ACTIVE);

        ApiKey revokedKey = new ApiKey();
        revokedKey.setStatus(ApiKeyStatus.REVOKED);

        ApiKey expiredKey = new ApiKey();
        expiredKey.setStatus(ApiKeyStatus.EXPIRED);

        // Then
        assertEquals(ApiKeyStatus.ACTIVE, activeKey.getStatus());
        assertEquals(ApiKeyStatus.REVOKED, revokedKey.getStatus());
        assertEquals(ApiKeyStatus.EXPIRED, expiredKey.getStatus());

        // Verify enum values (adjust based on actual enum definition)
        assertTrue(ApiKeyStatus.values().length >= 3);
    }

    @Test
    @DisplayName("Should handle usage count increment logic")
    void shouldHandleUsageCountIncrement() {
        // Given
        ApiKey apiKey = new ApiKey();
        apiKey.setUsageCount(0L);

        // When
        apiKey.setUsageCount(apiKey.getUsageCount() + 1);

        // Then
        assertEquals(1L, apiKey.getUsageCount());

        // When
        apiKey.setUsageCount(apiKey.getUsageCount() + 10);

        // Then
        assertEquals(11L, apiKey.getUsageCount());
    }

    @Test
    @DisplayName("Should handle timestamp fields correctly")
    void shouldHandleTimestampFieldsCorrectly() {
        // Given
        ApiKey apiKey = new ApiKey();
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 11, 0, 0);
        LocalDateTime lastUsedAt = LocalDateTime.of(2024, 1, 3, 12, 0, 0);

        // When
        apiKey.setCreatedAt(createdAt);
        apiKey.setUpdatedAt(updatedAt);
        apiKey.setLastUsedAt(lastUsedAt);

        // Then
        assertEquals(createdAt, apiKey.getCreatedAt());
        assertEquals(updatedAt, apiKey.getUpdatedAt());
        assertEquals(lastUsedAt, apiKey.getLastUsedAt());
        assertTrue(apiKey.getUpdatedAt().isAfter(apiKey.getCreatedAt()));
        assertTrue(apiKey.getLastUsedAt().isAfter(apiKey.getUpdatedAt()));
    }

    @Test
    @DisplayName("Should handle API key expiration logic")
    void shouldHandleApiKeyExpirationLogic() {
        // Given
        ApiKey apiKey = new ApiKey();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureExpiry = now.plusDays(30);
        LocalDateTime pastExpiry = now.minusDays(10);

        // When - future expiry
        apiKey.setExpiresAt(futureExpiry);

        // Then
        assertTrue(apiKey.getExpiresAt().isAfter(now));

        // When - past expiry
        apiKey.setExpiresAt(pastExpiry);

        // Then
        assertTrue(apiKey.getExpiresAt().isBefore(now));

        // When - no expiry (null)
        apiKey.setExpiresAt(null);

        // Then
        assertNull(apiKey.getExpiresAt());
    }

    @Test
    @DisplayName("Should handle rate limiting configuration")
    void shouldHandleRateLimitingConfiguration() {
        // Given
        ApiKey apiKey = new ApiKey();

        // When - set rate limit
        apiKey.setRateLimit(1000);
        apiKey.setRateLimitWindowSeconds(3600);

        // Then
        assertEquals(1000, apiKey.getRateLimit());
        assertEquals(3600, apiKey.getRateLimitWindowSeconds());

        // Calculate requests per second
        double requestsPerSecond = (double) apiKey.getRateLimit() / apiKey.getRateLimitWindowSeconds();
        assertEquals(1000.0 / 3600.0, requestsPerSecond, 0.001);
    }

    @Test
    @DisplayName("Should handle allowed services and paths arrays")
    void shouldHandleAllowedServicesAndPaths() {
        // Given
        ApiKey apiKey = new ApiKey();
        String[] services = {"ai-inference", "ai-nlp", "ai-vision"};
        String[] paths = {"/api/v1/inference/*", "/api/v1/nlp/*", "/api/v1/vision/*"};

        // When
        apiKey.setAllowedServices(services);
        apiKey.setAllowedPaths(paths);

        // Then
        assertEquals(3, apiKey.getAllowedServices().length);
        assertEquals(3, apiKey.getAllowedPaths().length);
        assertEquals("ai-inference", apiKey.getAllowedServices()[0]);
        assertEquals("/api/v1/inference/*", apiKey.getAllowedPaths()[0]);
    }

    @Test
    @DisplayName("Should verify equality with different instances")
    void shouldVerifyEqualityWithDifferentInstances() {
        // Given
        UUID id = UUID.randomUUID();
        ApiKey key1 = ApiKey.builder().id(id).tenantId("tenant-1").build();
        ApiKey key2 = ApiKey.builder().id(id).tenantId("tenant-1").build();
        ApiKey key3 = ApiKey.builder().id(UUID.randomUUID()).tenantId("tenant-2").build();

        // Then
        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
        assertNotEquals(key1, key3);
        assertNotEquals(key1.hashCode(), key3.hashCode());
        assertNotEquals(key1, null);
        assertNotEquals(key1, new Object());
    }
}
