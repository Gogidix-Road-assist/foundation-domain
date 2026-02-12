package com.gogidix.rapidassist.ai.anomaly.integration;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyDetection;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalySeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyStatus;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyDetectionRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.tenant.RequestContext;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.tenant.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation.
 * MUST pass for ALL services before production.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.auto-index-creation=true",
    "spring.data.mongodb.port=0"
})
public class TenantIsolationTest {

    @Autowired
    private AnomalyDetectionRepositoryPort repository;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Tenant A and Tenant B both have detections
        RequestContext tenantA = RequestContext.builder()
            .tenantId("tenant-a")
            .userId("user-a")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContext tenantB = RequestContext.builder()
            .tenantId("tenant-b")
            .userId("user-b")
            .correlationId(UUID.randomUUID().toString())
            .build();

        // When: Create detections for both tenants
        RequestContextHolder.set(tenantA);

        AnomalyDetection detectionA = AnomalyDetection.builder()
            .id(UUID.randomUUID())
            .tenantId("tenant-a")
            .dataSource("test-source")
            .dataPoint("test-point-a")
            .severity(AnomalySeverity.HIGH)
            .status(AnomalyStatus.PENDING)
            .anomalyScore(0.85)
            .confidence(0.92)
            .detectionMethod("TEST")
            .detectedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        repository.save("tenant-a", detectionA);

        RequestContextHolder.set(tenantB);

        AnomalyDetection detectionB = AnomalyDetection.builder()
            .id(UUID.randomUUID())
            .tenantId("tenant-b")
            .dataSource("test-source")
            .dataPoint("test-point-b")
            .severity(AnomalySeverity.MEDIUM)
            .status(AnomalyStatus.PENDING)
            .anomalyScore(0.65)
            .confidence(0.78)
            .detectionMethod("TEST")
            .detectedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        repository.save("tenant-b", detectionB);

        // Then: Tenant A should NOT see Tenant B's data
        RequestContextHolder.set(tenantA);
        List<AnomalyDetection> tenantAResults = repository.findByTenantId("tenant-a");

        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults.get(0).getDataPoint()).isEqualTo("test-point-a");

        // And: Tenant B should NOT see Tenant A's data
        RequestContextHolder.set(tenantB);
        List<AnomalyDetection> tenantBResults = repository.findByTenantId("tenant-b");

        assertThat(tenantBResults).hasSize(1);
        assertThat(tenantBResults.get(0).getTenantId()).isEqualTo("tenant-b");
        assertThat(tenantBResults.get(0).getDataPoint()).isEqualTo("test-point-b");
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Detection exists for Tenant A
        RequestContext tenantA = RequestContext.builder()
            .tenantId("tenant-a")
            .userId("user-a")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(tenantA);

        AnomalyDetection detection = AnomalyDetection.builder()
            .id(UUID.randomUUID())
            .tenantId("tenant-a")
            .dataSource("test-source")
            .dataPoint("test-point-a")
            .severity(AnomalySeverity.CRITICAL)
            .status(AnomalyStatus.PENDING)
            .anomalyScore(0.95)
            .confidence(0.98)
            .detectionMethod("TEST")
            .detectedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        AnomalyDetection saved = repository.save("tenant-a", detection);
        UUID detectionId = saved.getId();

        // When: Tenant B tries to access same detection
        RequestContext tenantB = RequestContext.builder()
            .tenantId("tenant-b")
            .userId("user-b")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(tenantB);

        // Then: Result should be empty (not found for this tenant)
        var result = repository.findById("tenant-b", detectionId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("countByTenantId is isolated per tenant")
    void countByTenantId_whenMultipleTenants_thenReturnCorrectCounts() {
        // Given: Multiple tenants with different number of detections
        RequestContext tenantA = RequestContext.builder()
            .tenantId("tenant-a")
            .userId("user-a")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(tenantA);

        for (int i = 0; i < 5; i++) {
            AnomalyDetection detection = AnomalyDetection.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-a")
                .dataSource("test-source")
                .dataPoint("test-point-a-" + i)
                .severity(AnomalySeverity.HIGH)
                .status(AnomalyStatus.PENDING)
                .anomalyScore(0.85)
                .confidence(0.92)
                .detectionMethod("TEST")
                .detectedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

            repository.save("tenant-a", detection);
        }

        RequestContext tenantB = RequestContext.builder()
            .tenantId("tenant-b")
            .userId("user-b")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(tenantB);

        for (int i = 0; i < 3; i++) {
            AnomalyDetection detection = AnomalyDetection.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-b")
                .dataSource("test-source")
                .dataPoint("test-point-b-" + i)
                .severity(AnomalySeverity.MEDIUM)
                .status(AnomalyStatus.PENDING)
                .anomalyScore(0.65)
                .confidence(0.78)
                .detectionMethod("TEST")
                .detectedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

            repository.save("tenant-b", detection);
        }

        // When & Then: Counts should be isolated
        long countA = repository.countByTenantId("tenant-a");
        long countB = repository.countByTenantId("tenant-b");

        assertThat(countA).isEqualTo(5);
        assertThat(countB).isEqualTo(3);
    }
}
