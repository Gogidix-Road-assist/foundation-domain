package com.gogidix.rapidassist.ai.forecasting.domain.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TenantContext.
 */
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testSetAndGetTenantId() {
        // Given
        String tenantId = "tenant-123";

        // When
        TenantContext.setTenantId(tenantId);
        Optional<String> result = TenantContext.getTenantId();

        // Then
        assertTrue(result.isPresent());
        assertEquals(tenantId, result.get());
    }

    @Test
    void testGetRequiredTenantId_WhenSet() {
        // Given
        String tenantId = "tenant-456";
        TenantContext.setTenantId(tenantId);

        // When
        String result = TenantContext.getRequiredTenantId();

        // Then
        assertEquals(tenantId, result);
    }

    @Test
    void testGetRequiredTenantId_WhenNotSet_ThrowsException() {
        // Given - tenant ID not set

        // When/Then
        assertThrows(IllegalStateException.class, () -> {
            TenantContext.getRequiredTenantId();
        });
    }

    @Test
    void testGetTenantId_WhenNotSet_ReturnsEmpty() {
        // Given - tenant ID not set

        // When
        Optional<String> result = TenantContext.getTenantId();

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testClear() {
        // Given
        TenantContext.setTenantId("tenant-789");

        // When
        TenantContext.clear();
        Optional<String> result = TenantContext.getTenantId();

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testMultipleSetOperations_OverwritesPrevious() {
        // Given
        TenantContext.setTenantId("tenant-1");

        // When
        TenantContext.setTenantId("tenant-2");
        Optional<String> result = TenantContext.getTenantId();

        // Then
        assertTrue(result.isPresent());
        assertEquals("tenant-2", result.get());
    }
}
