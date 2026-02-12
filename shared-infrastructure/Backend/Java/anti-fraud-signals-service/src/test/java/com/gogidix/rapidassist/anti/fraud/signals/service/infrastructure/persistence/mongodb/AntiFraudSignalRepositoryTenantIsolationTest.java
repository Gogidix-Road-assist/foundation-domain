package com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL SECURITY TESTS: Tenant Isolation for AntiFraudSignalRepository
 *
 * These tests verify that tenant isolation is enforced at the repository layer.
 * Failure of any test indicates a DATA LEAKAGE VULNERABILITY where one tenant
 * could access another tenant's fraud signals.
 */
@DataMongoTest
class AntiFraudSignalRepositoryTenantIsolationTest {

    @Autowired
    private AntiFraudSignalRepository repository;

    private final String tenant1Id = "tenant-1";
    private final String tenant2Id = "tenant-2";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("CRITICAL: Tenant Isolation - Cross-Tenant Data Access Prevention")
    class CrossTenantDataAccessTests {

        @Test
        @DisplayName("CRITICAL: Should not return signals from other tenants when querying by tenantId")
        void testTenantIsolationInFindByTenantId() {
            // Create signal for tenant-1
            AntiFraudSignalDocument signal1 = new AntiFraudSignalDocument(
                    null,
                    tenant1Id,
                    "txn-1",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    "High value transaction",
                    Map.of("amount", 15000),
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal1);

            // Create signal for tenant-2
            AntiFraudSignalDocument signal2 = new AntiFraudSignalDocument(
                    null,
                    tenant2Id,
                    "txn-2",
                    "suspicious_pattern",
                    AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0,
                    "Suspicious pattern detected",
                    Map.of("pattern", "velocity"),
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal2);

            // Query for tenant-1 - should ONLY return tenant-1 signals
            var tenant1Signals = repository.findByTenantId(tenant1Id,
                    org.springframework.data.domain.PageRequest.of(0, 10));
            assertThat(tenant1Signals.getContent())
                    .as("Tenant 1 should only see their own signals")
                    .hasSize(1);
            assertThat(tenant1Signals.getContent().get(0).tenantId())
                    .as("Returned signal must belong to tenant-1")
                    .isEqualTo(tenant1Id);

            // Query for tenant-2 - should ONLY return tenant-2 signals
            var tenant2Signals = repository.findByTenantId(tenant2Id,
                    org.springframework.data.domain.PageRequest.of(0, 10));
            assertThat(tenant2Signals.getContent())
                    .as("Tenant 2 should only see their own signals")
                    .hasSize(1);
            assertThat(tenant2Signals.getContent().get(0).tenantId())
                    .as("Returned signal must belong to tenant-2")
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findByTenantIdAndTransactionId should enforce tenant filtering")
        void testTenantIsolationInFindByTransaction() {
            // Create signals for same transaction but different tenants
            AntiFraudSignalDocument signal1 = new AntiFraudSignalDocument(
                    null,
                    tenant1Id,
                    "txn-shared",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal1);

            AntiFraudSignalDocument signal2 = new AntiFraudSignalDocument(
                    null,
                    tenant2Id,
                    "txn-shared",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal2);

            // Query for transaction in tenant-1 context
            List<AntiFraudSignalDocument> tenant1Signals =
                    repository.findByTenantIdAndTransactionId(tenant1Id, "txn-shared");
            assertThat(tenant1Signals)
                    .as("Tenant 1 should only see their signal for the transaction")
                    .hasSize(1);
            assertThat(tenant1Signals.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            // Query for transaction in tenant-2 context
            List<AntiFraudSignalDocument> tenant2Signals =
                    repository.findByTenantIdAndTransactionId(tenant2Id, "txn-shared");
            assertThat(tenant2Signals)
                    .as("Tenant 2 should only see their signal for the transaction")
                    .hasSize(1);
            assertThat(tenant2Signals.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findByIdAndTenantId should only return signal if it belongs to tenant")
        void testFindByIdAndTenantId() {
            // Create signal for tenant-1
            AntiFraudSignalDocument signal1 = new AntiFraudSignalDocument(
                    "signal-id-1",
                    tenant1Id,
                    "txn-1",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal1);

            // Tenant-1 should be able to find their signal
            Optional<AntiFraudSignalDocument> foundForT1 =
                    repository.findByIdAndTenantId("signal-id-1", tenant1Id);
            assertThat(foundForT1)
                    .as("Tenant 1 should find their own signal")
                    .isPresent();
            assertThat(foundForT1.get().tenantId())
                    .isEqualTo(tenant1Id);

            // Tenant-2 should NOT be able to find tenant-1's signal
            Optional<AntiFraudSignalDocument> foundForT2 =
                    repository.findByIdAndTenantId("signal-id-1", tenant2Id);
            assertThat(foundForT2)
                    .as("Tenant 2 should NOT find tenant 1's signal")
                    .isEmpty();
        }

        @Test
        @DisplayName("CRITICAL: existsByIdAndTenantId should only return true for signals belonging to tenant")
        void testExistsByIdAndTenantId() {
            // Create signal for tenant-1
            AntiFraudSignalDocument signal1 = new AntiFraudSignalDocument(
                    "signal-id-1",
                    tenant1Id,
                    "txn-1",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal1);

            // Tenant-1 should see their signal as existing
            boolean existsForT1 = repository.existsByIdAndTenantId("signal-id-1", tenant1Id);
            assertThat(existsForT1)
                    .as("Tenant 1 should see their signal as existing")
                    .isTrue();

            // Tenant-2 should NOT see tenant-1's signal as existing
            boolean existsForT2 = repository.existsByIdAndTenantId("signal-id-1", tenant2Id);
            assertThat(existsForT2)
                    .as("Tenant 2 should NOT see tenant 1's signal as existing")
                    .isFalse();
        }

        @Test
        @DisplayName("CRITICAL: findByTenantIdAndResolvedFalse should enforce tenant filtering")
        void testTenantFilteredUnresolved() {
            // Create unresolved signals for both tenants
            AntiFraudSignalDocument signal1 = new AntiFraudSignalDocument(
                    null,
                    tenant1Id,
                    "txn-1",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal1);

            AntiFraudSignalDocument signal2 = new AntiFraudSignalDocument(
                    null,
                    tenant2Id,
                    "txn-2",
                    "suspicious",
                    AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal2);

            // Verify tenant isolation for unresolved signals
            List<AntiFraudSignalDocument> tenant1Unresolved =
                    repository.findByTenantIdAndResolvedFalse(tenant1Id);
            assertThat(tenant1Unresolved)
                    .as("Tenant 1 should only see their unresolved signals")
                    .hasSize(1);
            assertThat(tenant1Unresolved.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            List<AntiFraudSignalDocument> tenant2Unresolved =
                    repository.findByTenantIdAndResolvedFalse(tenant2Id);
            assertThat(tenant2Unresolved)
                    .as("Tenant 2 should only see their unresolved signals")
                    .hasSize(1);
            assertThat(tenant2Unresolved.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findByTenantIdAndSeverityOrderByCreatedAtDesc should enforce tenant filtering")
        void testTenantFilteredBySeverity() {
            // Create HIGH severity signals for both tenants
            AntiFraudSignalDocument signal1 = new AntiFraudSignalDocument(
                    null,
                    tenant1Id,
                    "txn-1",
                    "high_value",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    85.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal1);

            AntiFraudSignalDocument signal2 = new AntiFraudSignalDocument(
                    null,
                    tenant2Id,
                    "txn-2",
                    "suspicious",
                    AntiFraudSignal.SignalSeverity.HIGH,
                    75.0,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    Instant.now(),
                    "system"
            );
            repository.save(signal2);

            // Verify tenant isolation by severity
            List<AntiFraudSignalDocument> tenant1High =
                    repository.findByTenantIdAndSeverityOrderByCreatedAtDesc(tenant1Id,
                            AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant1High)
                    .as("Tenant 1 should only see their HIGH severity signals")
                    .hasSize(1);
            assertThat(tenant1High.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            List<AntiFraudSignalDocument> tenant2High =
                    repository.findByTenantIdAndSeverityOrderByCreatedAtDesc(tenant2Id,
                            AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant2High)
                    .as("Tenant 2 should only see their HIGH severity signals")
                    .hasSize(1);
            assertThat(tenant2High.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: countByTenantIdAndResolvedFalse should only count for specific tenant")
        void testCountByTenantIdAndResolvedFalse() {
            // Create unresolved signals for both tenants
            repository.save(new AntiFraudSignalDocument(
                    null, tenant1Id, "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, false, null, null, null, Instant.now(), "system"
            ));
            repository.save(new AntiFraudSignalDocument(
                    null, tenant2Id, "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0, null, null, false, null, null, null, Instant.now(), "system"
            ));

            // Verify tenant isolation in count
            long tenant1Count = repository.countByTenantIdAndResolvedFalse(tenant1Id);
            assertThat(tenant1Count)
                    .as("Tenant 1 should only count their unresolved signals")
                    .isEqualTo(1);

            long tenant2Count = repository.countByTenantIdAndResolvedFalse(tenant2Id);
            assertThat(tenant2Count)
                    .as("Tenant 2 should only count their unresolved signals")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("CRITICAL: countByTenantIdAndSeverity should only count for specific tenant")
        void testCountByTenantIdAndSeverity() {
            // Create HIGH severity signals for both tenants
            repository.save(new AntiFraudSignalDocument(
                    null, tenant1Id, "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, false, null, null, null, Instant.now(), "system"
            ));
            repository.save(new AntiFraudSignalDocument(
                    null, tenant2Id, "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.HIGH,
                    75.0, null, null, false, null, null, null, Instant.now(), "system"
            ));

            // Verify tenant isolation in count by severity
            long tenant1Count = repository.countByTenantIdAndSeverity(tenant1Id,
                    AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant1Count)
                    .as("Tenant 1 should only count their HIGH severity signals")
                    .isEqualTo(1);

            long tenant2Count = repository.countByTenantIdAndSeverity(tenant2Id,
                    AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant2Count)
                    .as("Tenant 2 should only count their HIGH severity signals")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("CRITICAL: deleteByTenantId should only delete signals for specific tenant")
        void testDeleteByTenantId() {
            // Create signals for both tenants
            repository.save(new AntiFraudSignalDocument(
                    null, tenant1Id, "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, false, null, null, null, Instant.now(), "system"
            ));
            repository.save(new AntiFraudSignalDocument(
                    null, tenant2Id, "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0, null, null, false, null, null, null, Instant.now(), "system"
            ));

            // Delete only tenant-1's signals
            repository.deleteByTenantId(tenant1Id);

            // Verify only tenant-1's signals were deleted
            var remainingTenant1 = repository.findByTenantId(tenant1Id,
                    org.springframework.data.domain.PageRequest.of(0, 10));
            assertThat(remainingTenant1.getContent())
                    .as("Tenant 1's signals should be deleted")
                    .isEmpty();

            var remainingTenant2 = repository.findByTenantId(tenant2Id,
                    org.springframework.data.domain.PageRequest.of(0, 10));
            assertThat(remainingTenant2.getContent())
                    .as("Tenant 2's signals should NOT be deleted")
                    .hasSize(1);
        }

        @Test
        @DisplayName("CRITICAL: Multiple signals per tenant should be isolated")
        void testMultipleSignalsPerTenantIsolation() {
            // Create multiple signals for each tenant
            for (int i = 1; i <= 5; i++) {
                repository.save(new AntiFraudSignalDocument(
                        null,
                        tenant1Id,
                        "txn-t1-" + i,
                        "signal-" + i,
                        i % 2 == 0 ? AntiFraudSignal.SignalSeverity.HIGH : AntiFraudSignal.SignalSeverity.MEDIUM,
                        50.0 + i * 10,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        Instant.now(),
                        "system"
                ));

                repository.save(new AntiFraudSignalDocument(
                        null,
                        tenant2Id,
                        "txn-t2-" + i,
                        "signal-" + i,
                        i % 2 == 0 ? AntiFraudSignal.SignalSeverity.HIGH : AntiFraudSignal.SignalSeverity.MEDIUM,
                        50.0 + i * 10,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        Instant.now(),
                        "system"
                ));
            }

            // Verify each tenant sees only their signals
            var tenant1Signals = repository.findByTenantId(tenant1Id,
                    org.springframework.data.domain.PageRequest.of(0, 10));
            assertThat(tenant1Signals.getContent())
                    .as("Tenant 1 should see only their 5 signals")
                    .hasSize(5);
            assertThat(tenant1Signals.getContent())
                    .allMatch(signal -> signal.tenantId().equals(tenant1Id),
                            "All signals must belong to tenant-1");

            var tenant2Signals = repository.findByTenantId(tenant2Id,
                    org.springframework.data.domain.PageRequest.of(0, 10));
            assertThat(tenant2Signals.getContent())
                    .as("Tenant 2 should see only their 5 signals")
                    .hasSize(5);
            assertThat(tenant2Signals.getContent())
                    .allMatch(signal -> signal.tenantId().equals(tenant2Id),
                            "All signals must belong to tenant-2");
        }
    }
}
