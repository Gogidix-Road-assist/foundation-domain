package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.noop;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoOpIdempotencyStore.
 */
class NoOpIdempotencyStoreTest {

    private NoOpIdempotencyStore store;

    @BeforeEach
    void setUp() {
        store = new NoOpIdempotencyStore();
    }

    @Test
    void testFindReturnsEmpty() {
        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find("any-key");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindWithNullKey() {
        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindWithEmptyKey() {
        // Act
        Optional<IdempotencyStore.IdempotencyRecord> result = store.find("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveNewReturnsInputRecord() {
        // Arrange
        String key = "test-key";
        Instant now = Instant.now();
        IdempotencyStore.IdempotencyRecord input = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.IN_PROGRESS,
                now,
                now,
                "hash",
                null,
                null
        );

        // Act
        IdempotencyStore.IdempotencyRecord result = store.saveNew(input);

        // Assert
        assertNotNull(result);
        assertEquals(input.key(), result.key());
        assertEquals(input.status(), result.status());
        assertEquals(input.createdAt(), result.createdAt());
        assertEquals(input.updatedAt(), result.updatedAt());
        assertEquals(input.requestHash(), result.requestHash());
    }

    @Test
    void testSaveNewWithCompletedStatus() {
        // Arrange
        IdempotencyStore.IdempotencyRecord input = new IdempotencyStore.IdempotencyRecord(
                "key",
                IdempotencyStore.Status.COMPLETED,
                Instant.now(),
                Instant.now(),
                "hash",
                200,
                "response-hash"
        );

        // Act
        IdempotencyStore.IdempotencyRecord result = store.saveNew(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testUpdateReturnsInputRecord() {
        // Arrange
        String key = "update-key";
        Instant now = Instant.now();
        IdempotencyStore.IdempotencyRecord input = new IdempotencyStore.IdempotencyRecord(
                key,
                IdempotencyStore.Status.COMPLETED,
                now.minusSeconds(60),
                now,
                "hash",
                201,
                "response-hash"
        );

        // Act
        IdempotencyStore.IdempotencyRecord result = store.update(input);

        // Assert
        assertNotNull(result);
        assertEquals(input.key(), result.key());
        assertEquals(input.status(), result.status());
        assertEquals(input.responseStatus(), result.responseStatus());
        assertEquals(input.responseHash(), result.responseHash());
    }

    @Test
    void testUpdateWithFailedStatus() {
        // Arrange
        IdempotencyStore.IdempotencyRecord input = new IdempotencyStore.IdempotencyRecord(
                "key",
                IdempotencyStore.Status.FAILED,
                Instant.now(),
                Instant.now(),
                "hash",
                500,
                null
        );

        // Act
        IdempotencyStore.IdempotencyRecord result = store.update(input);

        // Assert
        assertEquals(input, result);
    }

    @Test
    void testSaveNewDoesNotThrowException() {
        // Arrange
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                null,  // Even null key should work
                IdempotencyStore.Status.IN_PROGRESS,
                null,
                null,
                null,
                null,
                null
        );

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> store.saveNew(record));
    }

    @Test
    void testUpdateDoesNotThrowException() {
        // Arrange
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                "any-key",
                IdempotencyStore.Status.COMPLETED,
                Instant.now(),
                Instant.now(),
                "hash",
                200,
                "response"
        );

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> store.update(record));
    }

    @Test
    void testFindAlwaysReturnsEmptyRegardlessOfKey() {
        // Act & Assert
        assertFalse(store.find("key1").isPresent());
        assertFalse(store.find("key2").isPresent());
        assertFalse(store.find("key3").isPresent());
        assertFalse(store.find("very-long-key-with-many-characters").isPresent());
    }

    @Test
    void testMultipleOperations() {
        // Arrange
        IdempotencyStore.IdempotencyRecord record1 = new IdempotencyStore.IdempotencyRecord(
                "key1",
                IdempotencyStore.Status.IN_PROGRESS,
                Instant.now(),
                Instant.now(),
                "hash1",
                null,
                null
        );

        IdempotencyStore.IdempotencyRecord record2 = new IdempotencyStore.IdempotencyRecord(
                "key2",
                IdempotencyStore.Status.COMPLETED,
                Instant.now(),
                Instant.now(),
                "hash2",
                200,
                "response2"
        );

        // Act
        store.saveNew(record1);
        store.saveNew(record2);
        store.update(record1);

        // Assert - NoOp store should not store anything
        assertFalse(store.find("key1").isPresent());
        assertFalse(store.find("key2").isPresent());
    }

    @Test
    void testIsNoOpImplementation() {
        // Verify that the store behaves as a no-op (no side effects)

        // Arrange
        IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                "test-key",
                IdempotencyStore.Status.IN_PROGRESS,
                Instant.now(),
                Instant.now(),
                "hash",
                null,
                null
        );

        // Act
        store.saveNew(record);
        store.update(record);

        // Assert - find should still return empty
        assertFalse(store.find("test-key").isPresent());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        // Test that multiple threads can use the store concurrently
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                IdempotencyStore.IdempotencyRecord record = new IdempotencyStore.IdempotencyRecord(
                        "key-" + index,
                        IdempotencyStore.Status.IN_PROGRESS,
                        Instant.now(),
                        Instant.now(),
                        "hash",
                        null,
                        null
                );
                store.saveNew(record);
                store.update(record);
                store.find("key-" + index);
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - all finds should return empty
        for (int i = 0; i < threads.length; i++) {
            assertFalse(store.find("key-" + i).isPresent());
        }
    }
}
