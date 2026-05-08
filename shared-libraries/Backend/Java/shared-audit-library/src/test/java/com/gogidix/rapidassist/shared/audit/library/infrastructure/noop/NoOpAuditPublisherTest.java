package com.gogidix.rapidassist.shared.audit.library.infrastructure.noop;

import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditActor;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEntityRef;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoOpAuditPublisher.
 */
class NoOpAuditPublisherTest {

    @Test
    void testPublishDoesNothing() {
        // Arrange
        NoOpAuditPublisher publisher = new NoOpAuditPublisher();
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-123",
                "USER_CREATED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                "subtenant-001",
                actor,
                entity,
                null,
                Map.of("name", "John Doe")
        );

        // Act & Assert - should not throw any exception
        assertDoesNotThrow(() -> publisher.publish(event));
    }

    @Test
    void testPublishWithNullEvent() {
        // Arrange
        NoOpAuditPublisher publisher = new NoOpAuditPublisher();

        // Act & Assert - should handle null gracefully
        assertDoesNotThrow(() -> publisher.publish(null));
    }

    @Test
    void testMultiplePublishes() {
        // Arrange
        NoOpAuditPublisher publisher = new NoOpAuditPublisher();
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        AuditEventEnvelope event1 = new AuditEventEnvelope(
                "1.0", "event-1", "EVENT_1", now, "corr-1", "US", "tenant-001", null,
                actor, entity, null, Map.of("id", 1)
        );

        AuditEventEnvelope event2 = new AuditEventEnvelope(
                "1.0", "event-2", "EVENT_2", now, "corr-2", "US", "tenant-001", null,
                actor, entity, null, Map.of("id", 2)
        );

        // Act & Assert
        assertDoesNotThrow(() -> {
            publisher.publish(event1);
            publisher.publish(event2);
            publisher.publish(event1); // Duplicate event
        });
    }

    @Test
    void testPublishWithComplexPayload() {
        // Arrange
        NoOpAuditPublisher publisher = new NoOpAuditPublisher();
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("USER", "user-123", "John Doe");
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", "Acme Corp");

        // Create a complex payload
        Object complexPayload = new Object() {
            public final String field1 = "value1";
            public final int field2 = 123;
            public final boolean field3 = true;
        };

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-123",
                "COMPLEX_EVENT",
                now,
                "corr-456",
                "US",
                "tenant-001",
                null,
                actor,
                entity,
                Map.of("key1", "value1", "key2", 123, "key3", true),
                complexPayload
        );

        // Act & Assert - should not throw any exception
        assertDoesNotThrow(() -> publisher.publish(event));
    }
}
