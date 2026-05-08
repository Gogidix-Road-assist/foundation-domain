package com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MongoTenantContext}.
 */
@DisplayName("MongoTenantContext Tests")
class MongoTenantContextTest {

    @AfterEach
    void tearDown() {
        // Clear context after each test
        MongoTenantContext.clear();
    }

    @Test
    @DisplayName("Should set and get tenant ID")
    void shouldSetAndGetTenantId() {
        MongoTenantContext.setTenantId(123L);
        assertEquals(123L, MongoTenantContext.getTenantId());
    }

    @Test
    @DisplayName("Should return default tenant ID when none is set")
    void shouldReturnDefaultTenantIdWhenNoneIsSet() {
        // No tenant ID set
        assertEquals(MongoTenantContext.getDefaultTenant(), MongoTenantContext.getTenantId());
        assertEquals(1L, MongoTenantContext.getTenantId());
    }

    @Test
    @DisplayName("Should use default tenant ID when null is set")
    void shouldUseDefaultTenantWhenNullIsSet() {
        MongoTenantContext.setTenantId(null);
        assertEquals(1L, MongoTenantContext.getTenantId());
    }

    @Test
    @DisplayName("Should return empty optional when tenant is not explicitly set")
    void shouldReturnEmptyOptionalWhenTenantNotExplicitlySet() {
        Optional<Long> tenantId = MongoTenantContext.getTenantIdOptional();
        assertFalse(tenantId.isPresent());
    }

    @Test
    @DisplayName("Should return tenant ID in optional when explicitly set")
    void shouldReturnTenantIdInOptionalWhenExplicitlySet() {
        MongoTenantContext.setTenantId(456L);
        Optional<Long> tenantId = MongoTenantContext.getTenantIdOptional();
        assertTrue(tenantId.isPresent());
        assertEquals(456L, tenantId.get());
    }

    @Test
    @DisplayName("Should detect if tenant is set")
    void shouldDetectIfTenantIsSet() {
        assertFalse(MongoTenantContext.isTenantSet());
        MongoTenantContext.setTenantId(789L);
        assertTrue(MongoTenantContext.isTenantSet());
    }

    @Test
    @DisplayName("Should throw exception when tenant ID is requested but not set")
    void shouldThrowExceptionWhenTenantIdRequestedButNotSet() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            MongoTenantContext::getTenantIdOrThrow
        );
        assertTrue(exception.getMessage().contains("Tenant ID is required"));
    }

    @Test
    @DisplayName("Should return tenant ID when explicitly set via getTenantIdOrThrow")
    void shouldReturnTenantIdWhenExplicitlySetViaGetTenantIdOrThrow() {
        MongoTenantContext.setTenantId(999L);
        assertEquals(999L, MongoTenantContext.getTenantIdOrThrow());
    }

    @Test
    @DisplayName("Should execute action with specified tenant ID")
    void shouldExecuteActionWithSpecifiedTenantId() {
        MongoTenantContext.setTenantId(111L);
        assertEquals(111L, MongoTenantContext.getTenantId());

        Long result = MongoTenantContext.withTenantId(222L, () -> {
            assertEquals(222L, MongoTenantContext.getTenantId());
            return 222L;
        });

        assertEquals(222L, result);
        // Original tenant should be restored
        assertEquals(111L, MongoTenantContext.getTenantId());
    }

    @Test
    @DisplayName("Should execute runnable with specified tenant ID")
    void shouldExecuteRunnableWithSpecifiedTenantId() {
        MongoTenantContext.setTenantId(333L);

        MongoTenantContext.withTenantId(444L, () -> {
            assertEquals(444L, MongoTenantContext.getTenantId());
        });

        // Original tenant should be restored
        assertEquals(333L, MongoTenantContext.getTenantId());
    }

    @Test
    @DisplayName("Should clear tenant context after withTenantId even on exception")
    void shouldClearTenantContextAfterWithTenantIdEvenOnException() {
        MongoTenantContext.setTenantId(555L);

        try {
            MongoTenantContext.withTenantId(666L, () -> {
                throw new RuntimeException("Test exception");
            });
        } catch (RuntimeException e) {
            // Expected
        }

        // Original tenant should be restored
        assertEquals(555L, MongoTenantContext.getTenantId());
    }

    @Test
    @DisplayName("Should throw exception when withTenantId is called with null")
    void shouldThrowExceptionWhenWithTenantIdCalledWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            MongoTenantContext.withTenantId(null, () -> "result");
        });
    }

    @Test
    @DisplayName("Should clear tenant context")
    void shouldClearTenantContext() {
        MongoTenantContext.setTenantId(777L);
        assertTrue(MongoTenantContext.isTenantSet());

        MongoTenantContext.clear();
        assertFalse(MongoTenantContext.isTenantSet());
        assertEquals(1L, MongoTenantContext.getTenantId()); // Returns default
    }

    @Test
    @DisplayName("Should return default tenant constant")
    void shouldReturnDefaultTenantConstant() {
        assertEquals(1L, MongoTenantContext.getDefaultTenant());
    }

    @Test
    @DisplayName("Should handle thread-local isolation")
    void shouldHandleThreadLocalIsolation() throws Exception {
        MongoTenantContext.setTenantId(100L);

        Thread otherThread = new Thread(() -> {
            // Other thread should not see the tenant ID from main thread
            assertEquals(1L, MongoTenantContext.getTenantId());
            MongoTenantContext.setTenantId(200L);
            assertEquals(200L, MongoTenantContext.getTenantId());
        });

        otherThread.start();
        otherThread.join();

        // Main thread should still have its original tenant ID
        assertEquals(100L, MongoTenantContext.getTenantId());
    }
}
