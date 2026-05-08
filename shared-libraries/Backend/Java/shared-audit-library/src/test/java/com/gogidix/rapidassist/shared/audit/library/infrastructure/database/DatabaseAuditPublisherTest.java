package com.gogidix.rapidassist.shared.audit.library.infrastructure.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditActor;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEntityRef;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseAuditPublisher.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseAuditPublisherTest {

    @Mock
    private AuditLogRepository repository;

    private ObjectMapper objectMapper;
    private DatabaseAuditPublisher publisher;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        publisher = new DatabaseAuditPublisher(repository, objectMapper);
    }

    @Test
    void testPublishSuccess() {
        // Arrange
        Instant now = Instant.now();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("ip", "192.168.1.1");

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
                attributes,
                Map.of("name", "John Doe", "email", "john@example.com")
        );

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(repository, times(1)).save(captor.capture());

        AuditLogEntity savedEntity = captor.getValue();
        assertEquals("1.0", savedEntity.getSpecVersion());
        assertEquals("event-123", savedEntity.getEventId());
        assertEquals("USER_CREATED", savedEntity.getEventType());
        assertEquals(now, savedEntity.getOccurredAt());
        assertEquals("corr-456", savedEntity.getCorrelationId());
        assertEquals("US", savedEntity.getCountry());
        assertEquals("tenant-001", savedEntity.getTenantId());
        assertEquals("subtenant-001", savedEntity.getSubTenantId());
        assertEquals("user-123", savedEntity.getActorId());
        assertEquals("USER", savedEntity.getActorType());
        assertEquals("John Doe", savedEntity.getActorName());
        assertEquals("Customer", savedEntity.getEntityType());
        assertEquals("customer-456", savedEntity.getEntityId());
        assertEquals("Acme Corp", savedEntity.getEntityName());
    }

    @Test
    void testPublishWithNullOptionalFields() {
        // Arrange
        Instant now = Instant.now();
        AuditActor actor = new AuditActor("user-123", "USER", null);
        AuditEntityRef entity = new AuditEntityRef("Customer", "customer-456", null);

        AuditEventEnvelope event = new AuditEventEnvelope(
                "1.0",
                "event-123",
                "USER_DELETED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                null,
                actor,
                entity,
                null,
                Map.of("id", "customer-456")
        );

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(repository, times(1)).save(captor.capture());

        AuditLogEntity savedEntity = captor.getValue();
        assertNull(savedEntity.getSubTenantId());
        assertNull(savedEntity.getActorName());
        assertNull(savedEntity.getEntityName());
        assertNull(savedEntity.getAttributes());
        assertNotNull(savedEntity.getPayload());
    }

    @Test
    void testPublishWithRepositoryException() {
        // Arrange
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
                null,
                actor,
                entity,
                null,
                Map.of("name", "John Doe")
        );

        when(repository.save(any(AuditLogEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act - should not throw exception
        assertDoesNotThrow(() -> publisher.publish(event));

        // Assert - still attempted to save
        verify(repository, times(1)).save(any(AuditLogEntity.class));
    }

    @Test
    void testPayloadSerialization() throws Exception {
        // Arrange
        Instant now = Instant.now();
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "John Doe");
        payload.put("age", 30);
        payload.put("active", true);

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
                null,
                actor,
                entity,
                null,
                payload
        );

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(repository, times(1)).save(captor.capture());

        AuditLogEntity savedEntity = captor.getValue();
        String payloadJson = savedEntity.getPayload();

        // Verify JSON is valid
        assertNotNull(payloadJson);
        Map<?, ?> deserialized = objectMapper.readValue(payloadJson, Map.class);
        assertEquals("John Doe", deserialized.get("name"));
        assertEquals(30, deserialized.get("age"));
        assertEquals(true, deserialized.get("active"));
    }

    @Test
    void testAttributesSerialization() {
        // Arrange
        Instant now = Instant.now();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("ip", "192.168.1.1");
        attributes.put("userAgent", "Mozilla/5.0");

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
                null,
                actor,
                entity,
                attributes,
                Map.of()
        );

        // Act
        publisher.publish(event);

        // Assert
        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(repository, times(1)).save(captor.capture());

        AuditLogEntity savedEntity = captor.getValue();
        String attributesJson = savedEntity.getAttributes();

        assertNotNull(attributesJson);
        assertTrue(attributesJson.contains("192.168.1.1"));
        assertTrue(attributesJson.contains("Mozilla"));
    }
}
