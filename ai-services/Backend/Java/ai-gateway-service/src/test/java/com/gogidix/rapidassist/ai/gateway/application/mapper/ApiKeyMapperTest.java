package com.gogidix.rapidassist.ai.gateway.application.mapper;

import com.gogidix.rapidassist.ai.gateway.application.dto.ApiKeyDto;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKey;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiKeyMapper.
 * Tests mapping between domain model and DTO.
 */
@DisplayName("ApiKey Mapper Tests")
class ApiKeyMapperTest {

    private ApiKeyMapper apiKeyMapper;

    private ApiKey domain;
    private ApiKeyDto dto;

    private final UUID testId = UUID.randomUUID();
    private final String tenantId = "tenant-123";

    @BeforeEach
    void setUp() {
        // Initialize mapper
        apiKeyMapper = Mappers.getMapper(ApiKeyMapper.class);

        // Initialize domain object
        domain = new ApiKey();
        domain.setId(testId);
        domain.setTenantId(tenantId);
        domain.setKeyName("Test API Key");
        domain.setApiKey("test-key-123");
        domain.setKeyHash("hash-123");
        domain.setStatus(ApiKeyStatus.ACTIVE);
        domain.setAllowedServices(new String[]{"service1", "service2"});
        domain.setAllowedPaths(new String[]{"/api/*", "/api/v1/*"});
        domain.setRateLimit(100);
        domain.setRateLimitWindowSeconds(60);
        domain.setExpiresAt(LocalDateTime.now().plusDays(30));
        domain.setCreatedBy("admin");
        domain.setDescription("Test API key");
        domain.setCreatedAt(LocalDateTime.now());
        domain.setUpdatedAt(LocalDateTime.now());
        domain.setUsageCount(50L);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("environment", "test");
        metadata.put("version", "1.0");
        domain.setMetadata(metadata);

        // Initialize DTO
        dto = new ApiKeyDto();
        dto.setId(testId);
        dto.setTenantId(tenantId);
        dto.setKeyName("Test API Key");
        dto.setApiKey("test-key-123");
        dto.setKeyHash("hash-123");
        dto.setStatus("ACTIVE");
        dto.setAllowedServices(new String[]{"service1", "service2"});
        dto.setAllowedPaths(new String[]{"/api/*", "/api/v1/*"});
        dto.setRateLimit(100);
        dto.setRateLimitWindowSeconds(60);
        dto.setExpiresAt(LocalDateTime.now().plusDays(30));
        dto.setCreatedBy("admin");
        dto.setDescription("Test API key");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setUsageCount(50L);

        Map<String, Object> dtoMetadata = new HashMap<>();
        dtoMetadata.put("environment", "test");
        dtoMetadata.put("version", "1.0");
        dto.setMetadata(dtoMetadata);
    }

    @Test
    @DisplayName("Should map domain to DTO correctly")
    void shouldMapDomainToDtoCorrectly() {
        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getTenantId(), result.getTenantId());
        assertEquals(domain.getKeyName(), result.getKeyName());
        assertEquals(domain.getApiKey(), result.getApiKey());
        assertEquals(domain.getKeyHash(), result.getKeyHash());
        assertEquals("ACTIVE", result.getStatus());
        assertArrayEquals(domain.getAllowedServices(), result.getAllowedServices());
        assertArrayEquals(domain.getAllowedPaths(), result.getAllowedPaths());
        assertEquals(domain.getRateLimit(), result.getRateLimit());
        assertEquals(domain.getRateLimitWindowSeconds(), result.getRateLimitWindowSeconds());
        assertEquals(domain.getExpiresAt(), result.getExpiresAt());
        assertEquals(domain.getCreatedBy(), result.getCreatedBy());
        assertEquals(domain.getDescription(), result.getDescription());
        assertEquals(domain.getMetadata(), result.getMetadata());
        assertEquals(domain.getCreatedAt(), result.getCreatedAt());
        assertEquals(domain.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(domain.getUsageCount(), result.getUsageCount());
    }

    @Test
    @DisplayName("Should map DTO to domain correctly")
    void shouldMapDtoToDomainCorrectly() {
        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getTenantId(), result.getTenantId());
        assertEquals(dto.getKeyName(), result.getKeyName());
        assertEquals(dto.getApiKey(), result.getApiKey());
        assertEquals(dto.getKeyHash(), result.getKeyHash());
        assertEquals(ApiKeyStatus.ACTIVE, result.getStatus());
        assertArrayEquals(dto.getAllowedServices(), result.getAllowedServices());
        assertArrayEquals(dto.getAllowedPaths(), result.getAllowedPaths());
        assertEquals(dto.getRateLimit(), result.getRateLimit());
        assertEquals(dto.getRateLimitWindowSeconds(), result.getRateLimitWindowSeconds());
        assertEquals(dto.getExpiresAt(), result.getExpiresAt());
        assertEquals(dto.getCreatedBy(), result.getCreatedBy());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getMetadata(), result.getMetadata());
        assertEquals(dto.getCreatedAt(), result.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(dto.getUsageCount(), result.getUsageCount());
    }

    @Test
    @DisplayName("Should map domain with ACTIVE status to DTO")
    void shouldMapActiveStatusToDto() {
        // Given
        domain.setStatus(ApiKeyStatus.ACTIVE);

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    @DisplayName("Should map domain with REVOKED status to DTO")
    void shouldMapRevokedStatusToDto() {
        // Given
        domain.setStatus(ApiKeyStatus.REVOKED);

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertEquals("REVOKED", result.getStatus());
    }

    @Test
    @DisplayName("Should map domain with EXPIRED status to DTO")
    void shouldMapExpiredStatusToDto() {
        // Given
        domain.setStatus(ApiKeyStatus.EXPIRED);

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertEquals("EXPIRED", result.getStatus());
    }

    @Test
    @DisplayName("Should map DTO with ACTIVE status to domain")
    void shouldMapActiveStatusToDomain() {
        // Given
        dto.setStatus("ACTIVE");

        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertEquals(ApiKeyStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("Should map DTO with REVOKED status to domain")
    void shouldMapRevokedStatusToDomain() {
        // Given
        dto.setStatus("REVOKED");

        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertEquals(ApiKeyStatus.REVOKED, result.getStatus());
    }

    @Test
    @DisplayName("Should map DTO with EXPIRED status to domain")
    void shouldMapExpiredStatusToDomain() {
        // Given
        dto.setStatus("EXPIRED");

        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertEquals(ApiKeyStatus.EXPIRED, result.getStatus());
    }

    @Test
    @DisplayName("Should handle null status in DTO to domain mapping")
    void shouldHandleNullStatusInDtoToDomain() {
        // Given
        dto.setStatus(null);

        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertNull(result.getStatus());
    }

    @Test
    @DisplayName("Should handle null status in domain to DTO mapping")
    void shouldHandleNullStatusInDomainToDto() {
        // Given
        domain.setStatus(null);

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertNull(result.getStatus());
    }

    @Test
    @DisplayName("Should handle invalid status string in DTO")
    void shouldHandleInvalidStatusStringInDto() {
        // Given
        dto.setStatus("INVALID_STATUS");

        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertNull(result.getStatus());
    }

    @Test
    @DisplayName("Should handle case-insensitive status in DTO")
    void shouldHandleCaseInsensitiveStatusInDto() {
        // Given
        dto.setStatus("active");

        // When
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertEquals(ApiKeyStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("Should map domain with null fields to DTO")
    void shouldMapDomainWithNullFieldsToDto() {
        // Given
        ApiKey nullFieldDomain = new ApiKey();
        nullFieldDomain.setId(testId);
        nullFieldDomain.setTenantId(tenantId);
        // All other fields are null

        // When
        ApiKeyDto result = apiKeyMapper.toDto(nullFieldDomain);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertNull(result.getKeyName());
        assertNull(result.getApiKey());
        assertNull(result.getStatus());
        assertNull(result.getAllowedServices());
        assertNull(result.getAllowedPaths());
        assertNull(result.getRateLimit());
        assertNull(result.getExpiresAt());
        assertNull(result.getCreatedBy());
        assertNull(result.getDescription());
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Should map DTO with null fields to domain")
    void shouldMapDtoWithNullFieldsToDomain() {
        // Given
        ApiKeyDto nullFieldDto = new ApiKeyDto();
        nullFieldDto.setId(testId);
        nullFieldDto.setTenantId(tenantId);
        // All other fields are null

        // When
        ApiKey result = apiKeyMapper.toDomain(nullFieldDto);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertNull(result.getKeyName());
        assertNull(result.getApiKey());
        assertNull(result.getStatus());
        assertNull(result.getAllowedServices());
        assertNull(result.getAllowedPaths());
        assertNull(result.getRateLimit());
        assertNull(result.getExpiresAt());
        assertNull(result.getCreatedBy());
        assertNull(result.getDescription());
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Should handle empty arrays in mapping")
    void shouldHandleEmptyArraysInMapping() {
        // Given
        domain.setAllowedServices(new String[]{});
        domain.setAllowedPaths(new String[]{});

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertNotNull(result.getAllowedServices());
        assertNotNull(result.getAllowedPaths());
        assertEquals(0, result.getAllowedServices().length);
        assertEquals(0, result.getAllowedPaths().length);
    }

    @Test
    @DisplayName("Should preserve metadata in mapping")
    void shouldPreserveMetadataInMapping() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);
        metadata.put("key3", true);
        domain.setMetadata(metadata);

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertEquals(metadata, result.getMetadata());
        assertEquals(3, result.getMetadata().size());
        assertEquals("value1", result.getMetadata().get("key1"));
        assertEquals(123, result.getMetadata().get("key2"));
        assertEquals(true, result.getMetadata().get("key3"));
    }

    @Test
    @DisplayName("Should handle null metadata in mapping")
    void shouldHandleNullMetadataInMapping() {
        // Given
        domain.setMetadata(null);

        // When
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Should round-trip map domain to DTO and back")
    void shouldRoundTripMapDomainToDtoAndBack() {
        // When - domain to DTO
        ApiKeyDto dto = apiKeyMapper.toDto(domain);

        // And - DTO back to domain
        ApiKey result = apiKeyMapper.toDomain(dto);

        // Then
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getTenantId(), result.getTenantId());
        assertEquals(domain.getKeyName(), result.getKeyName());
        assertEquals(domain.getStatus(), result.getStatus());
        assertEquals(domain.getRateLimit(), result.getRateLimit());
        assertEquals(domain.getUsageCount(), result.getUsageCount());
        assertArrayEquals(domain.getAllowedServices(), result.getAllowedServices());
        assertArrayEquals(domain.getAllowedPaths(), result.getAllowedPaths());
    }

    @Test
    @DisplayName("Should round-trip map DTO to domain and back")
    void shouldRoundTripMapDtoToDomainAndBack() {
        // When - DTO to domain
        ApiKey domain = apiKeyMapper.toDomain(dto);

        // And - domain back to DTO
        ApiKeyDto result = apiKeyMapper.toDto(domain);

        // Then
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getTenantId(), result.getTenantId());
        assertEquals(dto.getKeyName(), result.getKeyName());
        assertEquals(dto.getStatus(), result.getStatus());
        assertEquals(dto.getRateLimit(), result.getRateLimit());
        assertEquals(dto.getUsageCount(), result.getUsageCount());
        assertArrayEquals(dto.getAllowedServices(), result.getAllowedServices());
        assertArrayEquals(dto.getAllowedPaths(), result.getAllowedPaths());
    }
}
