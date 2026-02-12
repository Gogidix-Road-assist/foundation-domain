package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseIdempotencyStore.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseIdempotencyStoreTest {

    @Mock
    private IdempotencyKeyRepository repository;

    private DatabaseIdempotencyStore store;
    private Duration ttl = Duration.ofHours(24);

    @BeforeEach
    void setUp() {
        store = new DatabaseIdempotencyStore(repository, ttl);
    }

    @Test
    void testFindWhenKeyExists() {
        // Arrange
        String key = "test-key-123";
        Instant now = Instant.now();
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setId("entity-id-123");
        entity.setIdempotencyKey(key);
        entity.setStatus(IdempotencyKeyEntity.Status.COMPLETED);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setRequestHash("request-hash-123");
        entity.setResponseStatus(200);
        entity.setResponseHash("response-hash-456");
        entity.setExpiresAt(now.plus(ttl));

        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.of(entity));

        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find(key);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(key, result.get().key());
        assertEquals(IdempotencyStore.Status.COMPLETED, result.get().status());
        assertEquals("request-hash-123", result.get().requestHash());
        assertEquals(200, result.get().responseStatus());
        assertEquals("response-hash-456", result.get().responseHash());
        verify(repository).findByIdempotencyKey(key);
    }

    @Test
    void testFindWhenKeyDoesNotExist() {
        // Arrange
        String key = "non-existent-key";
        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.empty());

        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find(key);

        // Assert
        assertFalse(result.isPresent());
        verify(repository).findByIdempotencyKey(key);
    }

    @Test
    void testSaveNew() {
        // Arrange
        String key = "new-key-123";
        Instant now = Instant.now();
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                now,
                now,
                "request-hash-789",
                null,
                null
        );

        IdempotencyKeyEntity savedEntity = new IdempotencyKeyEntity();
        savedEntity.setId("saved-entity-id");
        savedEntity.setIdempotencyKey(key);
        savedEntity.setStatus(IdempotencyKeyEntity.Status.IN_PROGRESS);

        when(repository.save(any(IdempotencyKeyEntity.class))).thenReturn(savedEntity);

        // Act
        IdempotencyStore.IdempotencyRecord result = store.saveNew(record);

        // Assert
        assertNotNull(result);
        assertEquals(key, result.key());
        assertEquals(IdempotencyStore.Status.IN_PROGRESS, result.status());

        ArgumentCaptor<IdempotencyKeyEntity> captor = ArgumentCaptor.forClass(IdempotencyKeyEntity.class);
        verify(repository).save(captor.capture());

        IdempotencyKeyEntity capturedEntity = captor.getValue();
        assertEquals(key, capturedEntity.getIdempotencyKey());
        assertEquals(IdempotencyKeyEntity.Status.IN_PROGRESS, capturedEntity.getStatus());
        assertEquals("request-hash-789", capturedEntity.getRequestHash());
        assertNotNull(capturedEntity.getExpiresAt());
    }

    @Test
    void testUpdateExistingRecord() {
        // Arrange
        String key = "update-key-123";
        Instant now = Instant.now();

        IdempotencyKeyEntity existingEntity = new IdempotencyKeyEntity();
        existingEntity.setId("existing-id");
        existingEntity.setIdempotencyKey(key);
        existingEntity.setStatus(IdempotencyKeyEntity.Status.IN_PROGRESS);
        existingEntity.setCreatedAt(now.minusSeconds(60));
        existingEntity.setUpdatedAt(now.minusSeconds(60));

        IdempotencyStore.IdempotencyRecord updatedRecord = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.COMPLETED,
                now.minusSeconds(60),
                now,
                "request-hash-abc",
                201,
                "response-hash-def"
        );

        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(IdempotencyKeyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IdempotencyStore.IdempotencyRecord result = store.update(updatedRecord);

        // Assert
        assertNotNull(result);
        assertEquals(key, result.key());
        assertEquals(IdempotencyStore.Status.COMPLETED, result.status());

        ArgumentCaptor<IdempotencyKeyEntity> captor = ArgumentCaptor.forClass(IdempotencyKeyEntity.class);
        verify(repository).save(captor.capture());

        IdempotencyKeyEntity capturedEntity = captor.getValue();
        assertEquals(IdempotencyKeyEntity.Status.COMPLETED, capturedEntity.getStatus());
        assertEquals("request-hash-abc", capturedEntity.getRequestHash());
        assertEquals(201, capturedEntity.getResponseStatus());
        assertEquals("response-hash-def", capturedEntity.getResponseHash());
    }

    @Test
    void testUpdateNonExistingRecordCreatesNew() {
        // Arrange
        String key = "new-update-key";
        Instant now = Instant.now();

        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.COMPLETED,
                now,
                now,
                "hash",
                200,
                "response-hash"
        );

        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(repository.save(any(IdempotencyKeyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IdempotencyStore.IdempotencyRecord result = store.update(record);

        // Assert
        assertNotNull(result);
        verify(repository).save(any(IdempotencyKeyEntity.class));
    }

    @Test
    void testDeleteWhenKeyExists() {
        // Arrange
        String key = "delete-key-123";
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setId("delete-entity-id");
        entity.setIdempotencyKey(key);

        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.of(entity));

        // Act
        boolean result = store.delete(key);

        // Assert
        assertTrue(result);
        verify(repository).delete(entity);
    }

    @Test
    void testDeleteWhenKeyDoesNotExist() {
        // Arrange
        String key = "non-existent-delete-key";
        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.empty());

        // Act
        boolean result = store.delete(key);

        // Assert
        assertFalse(result);
        verify(repository, never()).delete(any());
    }

    @Test
    void testExistsWhenKeyExists() {
        // Arrange
        String key = "exists-key-123";
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setId("exists-entity-id");
        entity.setIdempotencyKey(key);

        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.of(entity));

        // Act
        boolean result = store.exists(key);

        // Assert
        assertTrue(result);
        verify(repository).findByIdempotencyKey(key);
    }

    @Test
    void testExistsWhenKeyDoesNotExist() {
        // Arrange
        String key = "non-existent-exists-key";
        when(repository.findByIdempotencyKey(key)).thenReturn(Optional.empty());

        // Act
        boolean result = store.exists(key);

        // Assert
        assertFalse(result);
        verify(repository).findByIdempotencyKey(key);
    }

    @Test
    void testDeleteExpired() {
        // Arrange
        when(repository.deleteByExpiresAtBefore(any(Instant.class))).thenReturn(42L);

        // Act
        long result = store.deleteExpired();

        // Assert
        assertEquals(42, result);
        verify(repository).deleteByExpiresAtBefore(any(Instant.class));
    }

    @Test
    void testSaveNewWithException() {
        // Arrange
        String key = "error-key";
        Instant now = Instant.now();
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                now,
                now,
                "hash",
                null,
                null
        );

        when(repository.save(any(IdempotencyKeyEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> store.saveNew(record));
    }

    @Test
    void testTtlIsApplied() {
        // Arrange
        String key = "ttl-test";
        Duration customTtl = Duration.ofMinutes(30);
        DatabaseIdempotencyStore customStore = new DatabaseIdempotencyStore(repository, customTtl);

        Instant now = Instant.now();
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                now,
                now,
                "hash",
                null,
                null
        );

        when(repository.save(any(IdempotencyKeyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        customStore.saveNew(record);

        // Assert
        ArgumentCaptor<IdempotencyKeyEntity> captor = ArgumentCaptor.forClass(IdempotencyKeyEntity.class);
        verify(repository).save(captor.capture());

        IdempotencyKeyEntity capturedEntity = captor.getValue();
        Instant expectedExpiry = now.plus(customTtl);
        // Allow for small time differences
        assertTrue(Math.abs(capturedEntity.getExpiresAt().getEpochSecond() - expectedExpiry.getEpochSecond()) <= 1);
    }

    @Test
    void testAllStatusesAreMappedCorrectly() {
        // Arrange
        String key = "status-test";
        Instant now = Instant.now();

        for (IdempotencyStore.Status status : IdempotencyStore.Status.values()) {
            IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
            entity.setId("id-" + status.name());
            entity.setIdempotencyKey(key + "-" + status.name());
            entity.setStatus(IdempotencyKeyEntity.Status.valueOf(status.name()));
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            when(repository.findByIdempotencyKey(key + "-" + status.name()))
                    .thenReturn(Optional.of(entity));

            // Act
            Optional<IdempotencyStore.IdempotencyRecord> result = store.find(key + "-" + status.name());

            // Assert
            assertTrue(result.isPresent());
            assertEquals(status, result.get().status());
        }
    }
}
