package com.gogidix.rapidassist.shared.persistence.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BaseEntity class.
 */
@DisplayName("Base Entity Tests")
class BaseEntityTest {

    private TestEntity entity;

    @BeforeEach
    void setUp() {
        entity = TestEntity.builder()
                .tenantId(UUID.randomUUID())
                .name("Test Name")
                .description("Test Description")
                .build();
    }

    @Test
    @DisplayName("Should create entity with builder")
    void testCreateEntityWithBuilder() {
        assertNotNull(entity);
        assertNotNull(entity.getTenantId());
        assertEquals("Test Name", entity.getName());
        assertEquals("Test Description", entity.getDescription());
    }

    @Test
    @DisplayName("Should have default values for deleted and version")
    void testDefaultValues() {
        TestEntity newEntity = TestEntity.builder().tenantId(UUID.randomUUID()).build();

        assertFalse(newEntity.getDeleted());
        assertEquals(0L, newEntity.getVersion());
    }

    @Test
    @DisplayName("Should soft delete entity")
    void testSoftDelete() {
        entity.softDelete();

        assertTrue(entity.getDeleted());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should restore soft deleted entity")
    void testRestore() {
        entity.softDelete();
        entity.restore();

        assertFalse(entity.getDeleted());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get audit fields")
    void testAuditFields() {
        UUID userId = UUID.randomUUID();
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);

        assertEquals(userId, entity.getCreatedBy());
        assertEquals(userId, entity.getUpdatedBy());
    }

    @Test
    @DisplayName("Should set and get ID")
    void testId() {
        UUID id = UUID.randomUUID();
        entity.setId(id);

        assertEquals(id, entity.getId());
    }

    @Test
    @DisplayName("Should set and get timestamps")
    void testTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should increment version")
    void testVersion() {
        entity.setVersion(5L);

        assertEquals(5L, entity.getVersion());
    }

    @Test
    @DisplayName("Should use toBuilder correctly")
    void testToBuilder() {
        UUID originalId = entity.getId();
        String originalName = entity.getName();

        TestEntity modifiedEntity = entity.toBuilder()
                .name("Modified Name")
                .build();

        assertEquals(originalId, modifiedEntity.getId());
        assertEquals("Modified Name", modifiedEntity.getName());
        assertEquals(originalName, entity.getName());
    }
}
