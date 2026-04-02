package com.gogidix.rapidassist.shared.persistence.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TenantAwareEntity class.
 */
@DisplayName("Tenant Aware Entity Tests")
class TenantAwareEntityTest {

    @Test
    @DisplayName("Should set tenant ID successfully")
    void testSetTenantId() {
        TestConcreteEntity entity = new TestConcreteEntity();
        UUID tenantId = UUID.randomUUID();

        entity.setTenantId(tenantId);

        assertEquals(tenantId, entity.getTenantId());
    }

    @Test
    @DisplayName("Should throw exception when setting null tenant ID")
    void testSetTenantIdNullThrowsException() {
        TestConcreteEntity entity = new TestConcreteEntity();

        assertThrows(IllegalArgumentException.class, () -> entity.setTenantId(null));
    }

    @Test
    @DisplayName("Should return true when entity belongs to tenant")
    void testBelongsToTenantReturnsTrue() {
        TestConcreteEntity entity = new TestConcreteEntity();
        UUID tenantId = UUID.randomUUID();
        entity.setTenantId(tenantId);

        assertTrue(entity.belongsToTenant(tenantId));
    }

    @Test
    @DisplayName("Should return false when entity does not belong to tenant")
    void testBelongsToTenantReturnsFalse() {
        TestConcreteEntity entity = new TestConcreteEntity();
        entity.setTenantId(UUID.randomUUID());

        assertFalse(entity.belongsToTenant(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should return false when tenant ID is null")
    void testBelongsToTenantWithNullTenantId() {
        TestConcreteEntity entity = new TestConcreteEntity();

        assertFalse(entity.belongsToTenant(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        UUID tenantId = UUID.randomUUID();
        TestConcreteEntity entity1 = new TestConcreteEntity();
        entity1.setTenantId(tenantId);

        TestConcreteEntity entity2 = new TestConcreteEntity();
        entity2.setTenantId(tenantId);

        assertEquals(entity1, entity2);
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void testHashCode() {
        UUID tenantId = UUID.randomUUID();
        TestConcreteEntity entity1 = new TestConcreteEntity();
        entity1.setTenantId(tenantId);

        TestConcreteEntity entity2 = new TestConcreteEntity();
        entity2.setTenantId(tenantId);

        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    @DisplayName("Should generate toString with tenant ID")
    void testToString() {
        TestConcreteEntity entity = new TestConcreteEntity();
        UUID tenantId = UUID.randomUUID();
        entity.setTenantId(tenantId);

        String result = entity.toString();

        assertTrue(result.contains("tenantId=" + tenantId));
    }

    @Test
    @DisplayName("Should have null tenant ID initially")
    void testInitialTenantIdIsNull() {
        TestConcreteEntity entity = new TestConcreteEntity();

        assertNull(entity.getTenantId());
    }

    @Test
    @DisplayName("Should update tenant ID when set")
    void testUpdateTenantId() {
        TestConcreteEntity entity = new TestConcreteEntity();
        UUID tenantId1 = UUID.randomUUID();
        UUID tenantId2 = UUID.randomUUID();

        entity.setTenantId(tenantId1);
        assertEquals(tenantId1, entity.getTenantId());

        entity.setTenantId(tenantId2);
        assertEquals(tenantId2, entity.getTenantId());
    }

    /**
     * Concrete implementation of TenantAwareEntity for testing.
     */
    static class TestConcreteEntity extends TenantAwareEntity {
    }
}
