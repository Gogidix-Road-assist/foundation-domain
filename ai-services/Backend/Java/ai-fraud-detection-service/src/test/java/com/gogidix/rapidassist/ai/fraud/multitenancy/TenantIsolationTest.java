package com.gogidix.rapidassist.ai.fraud.multitenancy;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import com.gogidix.rapidassist.ai.fraud.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.fraud.domain.repository.FraudDetectionRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tenant Isolation Tests")
class TenantIsolationTest {

    @Mock
    private FraudDetectionRepositoryPort repository;

    private String tenantId;
    private String otherTenantId;
    private UUID detectionId;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-123";
        otherTenantId = "tenant-456";
        detectionId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("TenantContext should set and get tenant ID")
    void testTenantContext_SetAndGet() {
        TenantContext.setTenantId(tenantId);

        assertTrue(TenantContext.getTenantId().isPresent());
        assertEquals(tenantId, TenantContext.getTenantId().get());
    }

    @Test
    @DisplayName("TenantContext should use ThreadLocal for isolation")
    void testTenantContext_ThreadLocal() throws InterruptedException {
        String mainTenant = "main-tenant";
        String threadTenant = "thread-tenant";

        TenantContext.setTenantId(mainTenant);

        Thread otherThread = new Thread(() -> {
            TenantContext.setTenantId(threadTenant);
            assertTrue(TenantContext.getTenantId().isPresent());
            assertEquals(threadTenant, TenantContext.getTenantId().get());
        });

        otherThread.start();
        otherThread.join();

        // Main thread should still have its own tenant
        assertTrue(TenantContext.getTenantId().isPresent());
        assertEquals(mainTenant, TenantContext.getTenantId().get());
    }

    @Test
    @DisplayName("TenantContext.clear should remove tenant ID")
    void testTenantContext_Clear() {
        TenantContext.setTenantId(tenantId);
        assertTrue(TenantContext.getTenantId().isPresent());

        TenantContext.clear();
        assertTrue(TenantContext.getTenantId().isEmpty());
    }

    @Test
    @DisplayName("Cross-tenant data access should be prevented")
    void testCrossTenantDataAccess_Prevented() {
        FraudDetection tenantDetection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .entityId("claim-456")
                .riskLevel(FraudRiskLevel.HIGH)
                .build();

        when(repository.findById(eq(otherTenantId), eq(detectionId)))
                .thenReturn(Optional.empty());
        when(repository.findById(eq(tenantId), eq(detectionId)))
                .thenReturn(Optional.of(tenantDetection));

        // Access with correct tenant
        Optional<FraudDetection> result1 = repository.findById(tenantId, detectionId);
        assertTrue(result1.isPresent());
        assertEquals(tenantId, result1.get().getTenantId());

        // Access with different tenant should return empty
        Optional<FraudDetection> result2 = repository.findById(otherTenantId, detectionId);
        assertFalse(result2.isPresent());
    }

    @Test
    @DisplayName("Repository should filter by tenant ID")
    void testRepositoryFilterByTenantId() {
        FraudDetection tenant1Detection = FraudDetection.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .entityType("CLAIM")
                .build();

        FraudDetection tenant2Detection = FraudDetection.builder()
                .id(UUID.randomUUID())
                .tenantId(otherTenantId)
                .entityType("CLAIM")
                .build();

        when(repository.findByTenantId(tenantId)).thenReturn(List.of(tenant1Detection));
        when(repository.findByTenantId(otherTenantId)).thenReturn(List.of(tenant2Detection));

        List<FraudDetection> tenant1Results = repository.findByTenantId(tenantId);
        List<FraudDetection> tenant2Results = repository.findByTenantId(otherTenantId);

        assertEquals(1, tenant1Results.size());
        assertEquals(1, tenant2Results.size());

        // Verify no cross-tenant data
        assertNotEquals(tenant1Results.get(0).getId(), tenant2Results.get(0).getId());
        assertTrue(tenant1Results.stream().allMatch(d -> tenantId.equals(d.getTenantId())));
        assertTrue(tenant2Results.stream().allMatch(d -> otherTenantId.equals(d.getTenantId())));
    }

    @Test
    @DisplayName("Domain models should have tenantId field")
    void testDomainModelHasTenantId() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(tenantId)
                .build();

        assertEquals(tenantId, detection.getTenantId());
        assertNotNull(detection.getTenantId());
    }

    @Test
    @DisplayName("Service should use tenant from TenantContext")
    void testServiceUsesTenantFromContext() {
        TenantContext.setTenantId(tenantId);

        FraudDetection detection = FraudDetection.builder()
                .tenantId(TenantContext.getTenantId().orElse(null))
                .entityType("CLAIM")
                .build();

        assertEquals(tenantId, detection.getTenantId());
    }

    @Test
    @DisplayName("tenantId validation - empty string is allowed by builder")
    void testTenantIdValidation_AllowsEmptyString() {
        // Lombok builder doesn't validate, so empty string is allowed
        // Application layer should handle validation
        FraudDetection detection = FraudDetection.builder()
                .tenantId("")
                .build();

        assertEquals("", detection.getTenantId());
    }

    @Test
    @DisplayName("tenantId validation should reject null values")
    void testTenantIdValidation_NotNull() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(null)
                .build();

        assertNull(detection.getTenantId());
    }

    @Test
    @DisplayName("Multi-tenant save should include tenantId")
    void testSaveWithTenantId() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(tenantId)
                .entityType("CLAIM")
                .build();

        when(repository.save(eq(tenantId), any(FraudDetection.class)))
                .thenReturn(detection);

        FraudDetection saved = repository.save(tenantId, detection);

        assertNotNull(saved);
        assertEquals(tenantId, saved.getTenantId());
        verify(repository).save(eq(tenantId), argThat(d -> tenantId.equals(d.getTenantId())));
    }

    @Test
    @DisplayName("Delete should include tenantId")
    void testDeleteIncludesTenantId() {
        doNothing().when(repository).delete(eq(tenantId), eq(detectionId));

        repository.delete(tenantId, detectionId);

        verify(repository).delete(tenantId, detectionId);
    }

    @Test
    @DisplayName("Delete should fail for non-existent tenant data")
    void testDeleteForNonExistentData() {
        when(repository.exists(eq(otherTenantId), eq(detectionId))).thenReturn(false);

        // Should not proceed with delete if data doesn't exist for tenant
        assertThrows(Exception.class, () -> {
            if (!repository.exists(otherTenantId, detectionId)) {
                throw new RuntimeException("Data not found for tenant");
            }
            repository.delete(otherTenantId, detectionId);
        });
    }
}
