package com.gogidix.rapidassist.shared.audit.library.infrastructure.database;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuditLogEntity.
 */
class AuditLogEntityTest {

    @Test
    void testEntityCreation() {
        Instant now = Instant.now();

        AuditLogEntity entity = new AuditLogEntity(
                "1.0",
                "event-123",
                "USER_CREATED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                "subtenant-001",
                "user-123",
                "USER",
                "John Doe",
                "Customer",
                "customer-456",
                "Acme Corp",
                null,
                "{\"name\": \"John Doe\"}"
        );

        assertNotNull(entity);
        assertEquals("1.0", entity.getSpecVersion());
        assertEquals("event-123", entity.getEventId());
        assertEquals("USER_CREATED", entity.getEventType());
        assertEquals(now, entity.getOccurredAt());
        assertEquals("corr-456", entity.getCorrelationId());
        assertEquals("US", entity.getCountry());
        assertEquals("tenant-001", entity.getTenantId());
        assertEquals("subtenant-001", entity.getSubTenantId());
        assertEquals("user-123", entity.getActorId());
        assertEquals("USER", entity.getActorType());
        assertEquals("John Doe", entity.getActorName());
        assertEquals("Customer", entity.getEntityType());
        assertEquals("customer-456", entity.getEntityId());
        assertEquals("Acme Corp", entity.getEntityName());
        assertEquals("{\"name\": \"John Doe\"}", entity.getPayload());
    }

    @Test
    void testEntityWithNullOptionalFields() {
        Instant now = Instant.now();

        AuditLogEntity entity = new AuditLogEntity(
                "1.0",
                "event-123",
                "USER_CREATED",
                now,
                "corr-456",
                "US",
                "tenant-001",
                null,  // subTenantId null
                "user-123",
                "USER",
                null,  // actorName null
                "Customer",
                "customer-456",
                null,  // entityName null
                null,  // attributes null
                "{\"name\": \"John Doe\"}"
        );

        assertNull(entity.getSubTenantId());
        assertNull(entity.getActorName());
        assertNull(entity.getEntityName());
        assertNull(entity.getAttributes());
        assertNotNull(entity.getPayload());
    }

    @Test
    void testSettersAndGetters() {
        AuditLogEntity entity = new AuditLogEntity();
        Instant now = Instant.now();

        entity.setId("id-123");
        entity.setSpecVersion("1.0");
        entity.setEventId("event-456");
        entity.setEventType("USER_DELETED");
        entity.setOccurredAt(now);
        entity.setCorrelationId("corr-789");
        entity.setCountry("CA");
        entity.setTenantId("tenant-002");
        entity.setSubTenantId("subtenant-002");
        entity.setActorId("user-456");
        entity.setActorType("ADMIN");
        entity.setActorName("Admin User");
        entity.setEntityType("Provider");
        entity.setEntityId("provider-123");
        entity.setEntityName("ABC Provider");
        entity.setAttributes("{\"key\": \"value\"}");
        entity.setPayload("{\"data\": \"test\"}");

        assertEquals("id-123", entity.getId());
        assertEquals("1.0", entity.getSpecVersion());
        assertEquals("event-456", entity.getEventId());
        assertEquals("USER_DELETED", entity.getEventType());
        assertEquals(now, entity.getOccurredAt());
        assertEquals("corr-789", entity.getCorrelationId());
        assertEquals("CA", entity.getCountry());
        assertEquals("tenant-002", entity.getTenantId());
        assertEquals("subtenant-002", entity.getSubTenantId());
        assertEquals("user-456", entity.getActorId());
        assertEquals("ADMIN", entity.getActorType());
        assertEquals("Admin User", entity.getActorName());
        assertEquals("Provider", entity.getEntityType());
        assertEquals("provider-123", entity.getEntityId());
        assertEquals("ABC Provider", entity.getEntityName());
        assertEquals("{\"key\": \"value\"}", entity.getAttributes());
        assertEquals("{\"data\": \"test\"}", entity.getPayload());
    }

    @Test
    void testDefaultConstructor() {
        AuditLogEntity entity = new AuditLogEntity();

        assertNotNull(entity);
        assertNotNull(entity.getId()); // ID is initialized with UUID
        assertNull(entity.getEventId());
        assertNull(entity.getEventType());
    }

    @Test
    void testCreatedAt() {
        AuditLogEntity entity = new AuditLogEntity();
        Instant createdAt = Instant.now();

        entity.setCreatedAt(createdAt);

        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void testEntityWithAllFieldsSet() {
        Instant now = Instant.now();
        Instant createdAt = Instant.now();

        AuditLogEntity entity = new AuditLogEntity();
        entity.setSpecVersion("1.0");
        entity.setEventId("event-123");
        entity.setEventType("TEST");
        entity.setOccurredAt(now);
        entity.setCorrelationId("corr-123");
        entity.setCountry("US");
        entity.setTenantId("tenant-001");
        entity.setActorId("user-123");
        entity.setActorType("USER");
        entity.setEntityType("TestEntity");
        entity.setEntityId("entity-123");
        entity.setPayload("{}");
        entity.setCreatedAt(createdAt);

        assertNotNull(entity.getCreatedAt());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}
