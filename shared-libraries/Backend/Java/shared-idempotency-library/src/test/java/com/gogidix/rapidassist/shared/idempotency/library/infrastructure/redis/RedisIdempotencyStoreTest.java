package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisIdempotencyStore.
 */
@ExtendWith(MockitoExtension.class)
class RedisIdempotencyStoreTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private ObjectMapper objectMapper;
    private RedisIdempotencyStore store;
    private Duration ttl = Duration.ofHours(24);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        store = new RedisIdempotencyStore(redisTemplate, objectMapper, ttl);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testFindWhenKeyExists() {
        // Arrange
        String key = "test-key-123";
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.COMPLETED,
                Instant.now(),
                Instant.now(),
                "request-hash-123",
                200,
                "response-hash-456"
        );

        when(valueOperations.get(anyString())).thenReturn(record);

        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find(key);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(key, result.get().key());
        assertEquals(IdempotencyStore.Status.COMPLETED, result.get().status());
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(anyString());
    }

    @Test
    void testFindWhenKeyDoesNotExist() {
        // Arrange
        String key = "non-existent-key";
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find(key);

        // Assert
        assertFalse(result.isPresent());
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(anyString());
    }

    @Test
    void testSaveNew() {
        // Arrange
        String key = "new-key-123";
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                Instant.now(),
                Instant.now(),
                "request-hash-789",
                null,
                null
        );

        // Act
        IdempotencyStore.IdempotencyRecord result = store.saveNew(record);

        // Assert
        assertNotNull(result);
        assertEquals(key, result.key());
        verify(valueOperations).set(eq("idempotency:" + key), eq(record), eq(ttl));
    }

    @Test
    void testUpdate() {
        // Arrange
        String key = "update-key-123";
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.COMPLETED,
                Instant.now(),
                Instant.now(),
                "request-hash-abc",
                201,
                "response-hash-def"
        );

        // Act
        IdempotencyStore.IdempotencyRecord result = store.update(record);

        // Assert
        assertNotNull(result);
        assertEquals(key, result.key());
        assertEquals(IdempotencyStore.Status.COMPLETED, result.status());
        verify(valueOperations).set(eq("idempotency:" + key), eq(record), eq(ttl));
    }

    @Test
    void testDeleteWhenKeyExists() {
        // Arrange
        String key = "delete-key-123";
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // Act
        boolean result = store.delete(key);

        // Assert
        assertTrue(result);
        verify(redisTemplate).delete(eq("idempotency:" + key));
    }

    @Test
    void testDeleteWhenKeyDoesNotExist() {
        // Arrange
        String key = "non-existent-delete-key";
        when(redisTemplate.delete(anyString())).thenReturn(false);

        // Act
        boolean result = store.delete(key);

        // Assert
        assertFalse(result);
        verify(redisTemplate).delete(eq("idempotency:" + key));
    }

    @Test
    void testExistsWhenKeyExists() {
        // Arrange
        String key = "exists-key-123";
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // Act
        boolean result = store.exists(key);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey(eq("idempotency:" + key));
    }

    @Test
    void testExistsWhenKeyDoesNotExist() {
        // Arrange
        String key = "non-existent-exists-key";
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // Act
        boolean result = store.exists(key);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey(eq("idempotency:" + key));
    }

    @Test
    void testSaveNewWithException() {
        // Arrange
        String key = "error-key";
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                Instant.now(),
                Instant.now(),
                "hash",
                null,
                null
        );

        doThrow(new RuntimeException("Redis connection error"))
                .when(valueOperations).set(anyString(), any(), any(Duration.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> store.saveNew(record));
    }

    @Test
    void testKeyPrefix() {
        // Arrange
        String key = "prefix-test";
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                Instant.now(),
                Instant.now(),
                "hash",
                null,
                null
        );

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        store.saveNew(record);

        // Assert
        verify(valueOperations).set(keyCaptor.capture(), any(), any(Duration.class));
        assertTrue(keyCaptor.getValue().startsWith("idempotency:"));
        assertTrue(keyCaptor.getValue().endsWith(key));
    }

    @Test
    void testTtlIsApplied() {
        // Arrange
        String key = "ttl-test";
        Duration customTtl = Duration.ofMinutes(30);
        RedisIdempotencyStore customStore = new RedisIdempotencyStore(
                redisTemplate, objectMapper, customTtl);

        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                Instant.now(),
                Instant.now(),
                "hash",
                null,
                null
        );

        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);

        // Act
        customStore.saveNew(record);

        // Assert
        verify(valueOperations).set(anyString(), any(), ttlCaptor.capture());
        assertEquals(customTtl, ttlCaptor.getValue());
    }
}
