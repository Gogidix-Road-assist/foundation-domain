package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKey;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.ApiKeyEntity;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper.ApiKeyPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApiKeyRepositoryImpl.
 * Tests persistence layer operations and CRUD functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKey Repository Implementation Tests")
class ApiKeyRepositoryImplTest {

    @Mock
    private SpringDataApiKeyRepository springDataRepository;

    @Mock
    private ApiKeyPersistenceMapper persistenceMapper;

    @InjectMocks
    private ApiKeyRepositoryImpl repository;

    private ApiKey apiKey;
    private ApiKeyEntity apiKeyEntity;

    private final UUID testId = UUID.randomUUID();
    private final String tenantId = "tenant-123";
    private final String apiKeyValue = "test-api-key-123";

    @BeforeEach
    void setUp() {
        // Initialize domain model
        apiKey = new ApiKey();
        apiKey.setId(testId);
        apiKey.setTenantId(tenantId);
        apiKey.setKeyName("Test API Key");
        apiKey.setApiKey(apiKeyValue);
        apiKey.setKeyHash("hash-123");
        apiKey.setStatus(ApiKeyStatus.ACTIVE);
        apiKey.setAllowedServices(new String[]{"service1", "service2"});
        apiKey.setAllowedPaths(new String[]{"/api/*"});
        apiKey.setRateLimit(100);
        apiKey.setRateLimitWindowSeconds(60);
        apiKey.setExpiresAt(LocalDateTime.now().plusDays(30));
        apiKey.setCreatedBy("admin");
        apiKey.setDescription("Test API key");
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setUpdatedAt(LocalDateTime.now());
        apiKey.setUsageCount(50L);

        // Initialize entity
        apiKeyEntity = new ApiKeyEntity();
        apiKeyEntity.setUuid(testId);
        apiKeyEntity.setTenantId(tenantId);
        apiKeyEntity.setKeyName("Test API Key");
        apiKeyEntity.setApiKey(apiKeyValue);
        apiKeyEntity.setKeyHash("hash-123");
        apiKeyEntity.setStatus(ApiKeyStatus.ACTIVE);
        apiKeyEntity.setAllowedServices(new String[]{"service1", "service2"});
        apiKeyEntity.setAllowedPaths(new String[]{"/api/*"});
        apiKeyEntity.setRateLimit(100);
        apiKeyEntity.setRateLimitWindowSeconds(60);
        apiKeyEntity.setExpiresAt(LocalDateTime.now().plusDays(30));
        apiKeyEntity.setCreatedBy("admin");
        apiKeyEntity.setDescription("Test API key");
        apiKeyEntity.setCreatedAt(LocalDateTime.now());
        apiKeyEntity.setUpdatedAt(LocalDateTime.now());
        apiKeyEntity.setUsageCount(50L);
    }

    @Test
    @DisplayName("Should save API key successfully")
    void shouldSaveApiKeySuccessfully() {
        // Given
        when(persistenceMapper.toEntity(apiKey)).thenReturn(apiKeyEntity);
        when(springDataRepository.save(apiKeyEntity)).thenReturn(apiKeyEntity);
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        ApiKey result = repository.save(apiKey);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(apiKeyValue, result.getApiKey());

        verify(persistenceMapper).toEntity(apiKey);
        verify(springDataRepository).save(apiKeyEntity);
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should find API key by ID")
    void shouldFindApiKeyById() {
        // Given
        when(springDataRepository.findAll()).thenReturn(Arrays.asList(apiKeyEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        Optional<ApiKey> result = repository.findById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());

        verify(springDataRepository).findAll();
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should return empty when API key not found by ID")
    void shouldReturnEmptyWhenApiKeyNotFoundById() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        ApiKeyEntity differentEntity = new ApiKeyEntity();
        differentEntity.setUuid(UUID.randomUUID());

        when(springDataRepository.findAll()).thenReturn(Arrays.asList(differentEntity));

        // When
        Optional<ApiKey> result = repository.findById(nonExistentId);

        // Then
        assertFalse(result.isPresent());
        verify(springDataRepository).findAll();
        verify(persistenceMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find API key by ID and tenant ID")
    void shouldFindApiKeyByIdAndTenantId() {
        // Given
        when(springDataRepository.findByUuidAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(apiKeyEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        Optional<ApiKey> result = repository.findByIdAndTenantId(testId, tenantId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals(tenantId, result.get().getTenantId());

        verify(springDataRepository).findByUuidAndTenantId(testId, tenantId);
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should return empty when API key not found by ID and tenant ID")
    void shouldReturnEmptyWhenApiKeyNotFoundByIdAndTenantId() {
        // Given
        when(springDataRepository.findByUuidAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        // When
        Optional<ApiKey> result = repository.findByIdAndTenantId(testId, tenantId);

        // Then
        assertFalse(result.isPresent());
        verify(springDataRepository).findByUuidAndTenantId(testId, tenantId);
        verify(persistenceMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find all API keys by tenant ID")
    void shouldFindAllApiKeysByTenantId() {
        // Given
        ApiKeyEntity entity2 = new ApiKeyEntity();
        entity2.setUuid(UUID.randomUUID());
        entity2.setTenantId(tenantId);

        ApiKey key2 = new ApiKey();
        key2.setId(entity2.getUuid());
        key2.setTenantId(tenantId);

        when(springDataRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList(apiKeyEntity, entity2));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);
        when(persistenceMapper.toDomain(entity2)).thenReturn(key2);

        // When
        List<ApiKey> result = repository.findByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tenantId, result.get(0).getTenantId());
        assertEquals(tenantId, result.get(1).getTenantId());

        verify(springDataRepository).findByTenantId(tenantId);
        verify(persistenceMapper, times(2)).toDomain(any(ApiKeyEntity.class));
    }

    @Test
    @DisplayName("Should find API keys by tenant ID and status")
    void shouldFindApiKeysByTenantIdAndStatus() {
        // Given
        ApiKeyStatus status = ApiKeyStatus.ACTIVE;
        when(springDataRepository.findByTenantIdAndStatus(tenantId, status))
                .thenReturn(Arrays.asList(apiKeyEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        List<ApiKey> result = repository.findByTenantIdAndStatus(tenantId, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());

        verify(springDataRepository).findByTenantIdAndStatus(tenantId, status);
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should find API key by API key value and tenant ID")
    void shouldFindApiKeyByApiKeyAndTenantId() {
        // Given
        when(springDataRepository.findByApiKeyAndTenantId(apiKeyValue, tenantId))
                .thenReturn(Optional.of(apiKeyEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        Optional<ApiKey> result = repository.findByApiKeyAndTenantId(apiKeyValue, tenantId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(apiKeyValue, result.get().getApiKey());
        assertEquals(tenantId, result.get().getTenantId());

        verify(springDataRepository).findByApiKeyAndTenantId(apiKeyValue, tenantId);
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should find API key by key name and tenant ID")
    void shouldFindApiKeyByKeyNameAndTenantId() {
        // Given
        String keyName = "Test API Key";
        when(springDataRepository.findByKeyNameAndTenantId(keyName, tenantId))
                .thenReturn(Optional.of(apiKeyEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        Optional<ApiKey> result = repository.findByKeyNameAndTenantId(keyName, tenantId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(keyName, result.get().getKeyName());

        verify(springDataRepository).findByKeyNameAndTenantId(keyName, tenantId);
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should find API keys by tenant ID and status in list")
    void shouldFindApiKeysByTenantIdAndStatusIn() {
        // Given
        List<ApiKeyStatus> statuses = Arrays.asList(ApiKeyStatus.ACTIVE, ApiKeyStatus.REVOKED);
        when(springDataRepository.findByTenantIdAndStatusIn(tenantId, statuses))
                .thenReturn(Arrays.asList(apiKeyEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        // When
        List<ApiKey> result = repository.findByTenantIdAndStatusIn(tenantId, statuses);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(statuses.contains(result.get(0).getStatus()));

        verify(springDataRepository).findByTenantIdAndStatusIn(tenantId, statuses);
        verify(persistenceMapper).toDomain(apiKeyEntity);
    }

    @Test
    @DisplayName("Should find expired keys")
    void shouldFindExpiredKeys() {
        // Given
        LocalDateTime currentDate = LocalDateTime.now();
        ApiKeyEntity expiredEntity = new ApiKeyEntity();
        expiredEntity.setUuid(UUID.randomUUID());
        expiredEntity.setTenantId(tenantId);
        expiredEntity.setExpiresAt(LocalDateTime.now().minusDays(10));

        when(springDataRepository.findAll()).thenReturn(Arrays.asList(apiKeyEntity, expiredEntity));
        when(persistenceMapper.toDomain(apiKeyEntity)).thenReturn(apiKey);

        ApiKey expiredKey = new ApiKey();
        expiredKey.setId(expiredEntity.getUuid());
        expiredKey.setTenantId(tenantId);
        expiredKey.setExpiresAt(expiredEntity.getExpiresAt());

        when(persistenceMapper.toDomain(expiredEntity)).thenReturn(expiredKey);

        // When
        List<ApiKey> result = repository.findExpiredKeys(currentDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getExpiresAt().isBefore(currentDate));

        verify(springDataRepository).findAll();
    }

    @Test
    @DisplayName("Should delete API key by ID")
    void shouldDeleteApiKeyById() {
        // Given
        when(springDataRepository.findAll()).thenReturn(Arrays.asList(apiKeyEntity));
        doNothing().when(springDataRepository).delete(apiKeyEntity);

        // When
        repository.deleteById(testId);

        // Then
        verify(springDataRepository).findAll();
        verify(springDataRepository).delete(apiKeyEntity);
    }

    @Test
    @DisplayName("Should delete API key by ID and tenant ID")
    void shouldDeleteApiKeyByIdAndTenantId() {
        // Given
        doNothing().when(springDataRepository).deleteByUuidAndTenantId(testId, tenantId);

        // When
        repository.deleteByIdAndTenantId(testId, tenantId);

        // Then
        verify(springDataRepository).deleteByUuidAndTenantId(testId, tenantId);
    }

    @Test
    @DisplayName("Should check existence by ID and tenant ID")
    void shouldCheckExistenceByIdAndTenantId() {
        // Given
        when(springDataRepository.existsByUuidAndTenantId(testId, tenantId))
                .thenReturn(true);

        // When
        boolean result = repository.existsByIdAndTenantId(testId, tenantId);

        // Then
        assertTrue(result);
        verify(springDataRepository).existsByUuidAndTenantId(testId, tenantId);
    }

    @Test
    @DisplayName("Should return false when checking existence for non-existent API key")
    void shouldReturnFalseWhenCheckingNonExistentApiKey() {
        // Given
        when(springDataRepository.existsByUuidAndTenantId(testId, tenantId))
                .thenReturn(false);

        // When
        boolean result = repository.existsByIdAndTenantId(testId, tenantId);

        // Then
        assertFalse(result);
        verify(springDataRepository).existsByUuidAndTenantId(testId, tenantId);
    }

    @Test
    @DisplayName("Should check existence by API key value and tenant ID")
    void shouldCheckExistenceByApiKeyAndTenantId() {
        // Given
        when(springDataRepository.existsByApiKeyAndTenantId(apiKeyValue, tenantId))
                .thenReturn(true);

        // When
        boolean result = repository.existsByApiKeyAndTenantId(apiKeyValue, tenantId);

        // Then
        assertTrue(result);
        verify(springDataRepository).existsByApiKeyAndTenantId(apiKeyValue, tenantId);
    }

    @Test
    @DisplayName("Should increment usage count")
    void shouldIncrementUsageCount() {
        // Given
        LocalDateTime lastUsedAt = LocalDateTime.now();
        when(springDataRepository.findByUuidAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(apiKeyEntity));
        when(springDataRepository.save(apiKeyEntity)).thenReturn(apiKeyEntity);

        // When
        repository.incrementUsageCount(testId, tenantId, lastUsedAt);

        // Then
        verify(springDataRepository).findByUuidAndTenantId(testId, tenantId);
        verify(springDataRepository).save(apiKeyEntity);
        assertEquals(51L, apiKeyEntity.getUsageCount());
        assertEquals(lastUsedAt, apiKeyEntity.getLastUsedAt());
    }

    @Test
    @DisplayName("Should handle increment usage count for non-existent API key")
    void shouldHandleIncrementUsageCountForNonExistentApiKey() {
        // Given
        LocalDateTime lastUsedAt = LocalDateTime.now();
        when(springDataRepository.findByUuidAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        // When
        repository.incrementUsageCount(testId, tenantId, lastUsedAt);

        // Then
        verify(springDataRepository).findByUuidAndTenantId(testId, tenantId);
        verify(springDataRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty list when no API keys found for tenant")
    void shouldReturnEmptyListWhenNoApiKeysFoundForTenant() {
        // Given
        when(springDataRepository.findByTenantId(tenantId))
                .thenReturn(Arrays.asList());

        // When
        List<ApiKey> result = repository.findByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(springDataRepository).findByTenantId(tenantId);
        verify(persistenceMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle multiple expired keys correctly")
    void shouldHandleMultipleExpiredKeysCorrectly() {
        // Given
        LocalDateTime currentDate = LocalDateTime.now();

        ApiKeyEntity expired1 = new ApiKeyEntity();
        expired1.setUuid(UUID.randomUUID());
        expired1.setExpiresAt(LocalDateTime.now().minusDays(5));

        ApiKeyEntity expired2 = new ApiKeyEntity();
        expired2.setUuid(UUID.randomUUID());
        expired2.setExpiresAt(LocalDateTime.now().minusDays(10));

        when(springDataRepository.findAll()).thenReturn(Arrays.asList(expired1, expired2));

        ApiKey key1 = new ApiKey();
        key1.setId(expired1.getUuid());
        key1.setExpiresAt(expired1.getExpiresAt());

        ApiKey key2 = new ApiKey();
        key2.setId(expired2.getUuid());
        key2.setExpiresAt(expired2.getExpiresAt());

        lenient().when(persistenceMapper.toDomain(expired1)).thenReturn(key1);
        lenient().when(persistenceMapper.toDomain(expired2)).thenReturn(key2);

        // When
        List<ApiKey> result = repository.findExpiredKeys(currentDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(springDataRepository).findAll();
    }

    @Test
    @DisplayName("Should handle null expires at when finding expired keys")
    void shouldHandleNullExpiresAtWhenFindingExpiredKeys() {
        // Given
        LocalDateTime currentDate = LocalDateTime.now();

        ApiKeyEntity entityWithNullExpiry = new ApiKeyEntity();
        entityWithNullExpiry.setUuid(UUID.randomUUID());
        entityWithNullExpiry.setExpiresAt(null);

        when(springDataRepository.findAll()).thenReturn(Arrays.asList(entityWithNullExpiry));

        // When
        List<ApiKey> result = repository.findExpiredKeys(currentDate);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(springDataRepository).findAll();
    }
}
