package com.gogidix.rapidassist.anti.fraud.signals.service.application.service;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import com.gogidix.rapidassist.anti.fraud.signals.service.domain.port.in.AntiFraudSignalService;
import com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb.AntiFraudSignalDocument;
import com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb.AntiFraudSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CRITICAL SECURITY TESTS: Tenant Isolation for AntiFraudSignalService
 *
 * These tests verify that tenant isolation is enforced at the service layer.
 * Failure of any test indicates a DATA LEAKAGE VULNERABILITY.
 */
@SpringBootTest
class AntiFraudSignalServiceTenantIsolationTest {

    @Autowired
    private AntiFraudSignalService signalService;

    @Autowired
    private AntiFraudSignalRepository repository;

    private final String tenant1Id = "tenant-1";
    private final String tenant2Id = "tenant-2";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("CRITICAL: Service Layer Tenant Isolation")
    class ServiceLayerTenantIsolation {

        @Test
        @DisplayName("CRITICAL: createSignal should set tenantId correctly")
        void testCreateSignalSetsTenantId() {
            AntiFraudSignalService.CreateSignalRequest request =
                    new AntiFraudSignalService.CreateSignalRequest(
                            "txn-12345",
                            "high_value",
                            AntiFraudSignal.SignalSeverity.HIGH,
                            85.0,
                            "High value transaction",
                            null,
                            "test-user"
                    );

            AntiFraudSignal signal = signalService.createSignal(tenant1Id, request);

            assertThat(signal.tenantId())
                    .as("Created signal must belong to the specified tenant")
                    .isEqualTo(tenant1Id);
        }

        @Test
        @DisplayName("CRITICAL: findByTenant should only return signals for specified tenant")
        void testFindByTenantIsolation() {
            // Create signals for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));

            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0, null, null, null));

            // Query for tenant-1
            var tenant1Signals = signalService.findByTenant(tenant1Id, PageRequest.of(0, 10));
            assertThat(tenant1Signals.getContent())
                    .as("Tenant 1 should only see their own signals")
                    .hasSize(1);
            assertThat(tenant1Signals.getContent().get(0).tenantId())
                    .isEqualTo(tenant1Id);

            // Query for tenant-2
            var tenant2Signals = signalService.findByTenant(tenant2Id, PageRequest.of(0, 10));
            assertThat(tenant2Signals.getContent())
                    .as("Tenant 2 should only see their own signals")
                    .hasSize(1);
            assertThat(tenant2Signals.getContent().get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findById should only return signal if it belongs to tenant")
        void testFindByIdTenantIsolation() {
            // Create signal for tenant-1
            AntiFraudSignal signal1 = signalService.createSignal(tenant1Id,
                    new AntiFraudSignalService.CreateSignalRequest(
                            "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                            85.0, null, null, null));

            // Tenant-1 should find their signal
            var foundForT1 = signalService.findById(tenant1Id, signal1.id());
            assertThat(foundForT1)
                    .as("Tenant 1 should find their signal")
                    .isPresent();
            assertThat(foundForT1.get().tenantId())
                    .isEqualTo(tenant1Id);

            // Tenant-2 should NOT find tenant-1's signal
            var foundForT2 = signalService.findById(tenant2Id, signal1.id());
            assertThat(foundForT2)
                    .as("Tenant 2 should NOT find tenant 1's signal")
                    .isEmpty();
        }

        @Test
        @DisplayName("CRITICAL: findByTransaction should only return signals for transaction in tenant")
        void testFindByTransactionTenantIsolation() {
            // Create signals for same transaction in both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-shared", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));

            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-shared", "suspicious", AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0, null, null, null));

            // Query for transaction in tenant-1
            var tenant1Signals = signalService.findByTransaction(tenant1Id, "txn-shared");
            assertThat(tenant1Signals)
                    .as("Tenant 1 should only see their signal for the transaction")
                    .hasSize(1);
            assertThat(tenant1Signals.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            // Query for transaction in tenant-2
            var tenant2Signals = signalService.findByTransaction(tenant2Id, "txn-shared");
            assertThat(tenant2Signals)
                    .as("Tenant 2 should only see their signal for the transaction")
                    .hasSize(1);
            assertThat(tenant2Signals.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: resolveSignal should only work for signals belonging to tenant")
        void testResolveSignalTenantIsolation() {
            // Create signal for tenant-1
            AntiFraudSignal signal1 = signalService.createSignal(tenant1Id,
                    new AntiFraudSignalService.CreateSignalRequest(
                            "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                            85.0, null, null, null));

            // Tenant-2 should NOT be able to resolve tenant-1's signal
            assertThatThrownBy(() ->
                    signalService.resolveSignal(tenant2Id, signal1.id(),
                            new AntiFraudSignalService.ResolveSignalRequest(
                                    true, "analyst@example.com", "Resolved")))
                    .as("Tenant 2 should not be able to resolve tenant 1's signal")
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("does not belong to tenant");

            // Verify signal was not resolved
            var notResolved = repository.findById(signal1.id()).get();
            assertThat(notResolved.resolved())
                    .as("Signal should not be resolved by tenant-2")
                    .isFalse();

            // Tenant-1 should be able to resolve their signal
            AntiFraudSignal resolved = signalService.resolveSignal(tenant1Id, signal1.id(),
                    new AntiFraudSignalService.ResolveSignalRequest(
                            true, "analyst@example.com", "Resolved"));
            assertThat(resolved.resolved())
                    .isTrue();
        }

        @Test
        @DisplayName("CRITICAL: findUnresolvedByTenant should only return unresolved signals for tenant")
        void testFindUnresolvedByTenantIsolation() {
            // Create unresolved signals for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));
            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0, null, null, null));

            // Query for unresolved signals
            var tenant1Unresolved = signalService.findUnresolvedByTenant(tenant1Id);
            assertThat(tenant1Unresolved)
                    .as("Tenant 1 should only see their unresolved signals")
                    .hasSize(1);
            assertThat(tenant1Unresolved.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            var tenant2Unresolved = signalService.findUnresolvedByTenant(tenant2Id);
            assertThat(tenant2Unresolved)
                    .as("Tenant 2 should only see their unresolved signals")
                    .hasSize(1);
            assertThat(tenant2Unresolved.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findByTenantAndSeverity should enforce tenant filtering")
        void testFindByTenantAndSeverityIsolation() {
            // Create HIGH severity signals for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));
            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.HIGH,
                    75.0, null, null, null));

            // Query for HIGH severity signals
            var tenant1High = signalService.findByTenantAndSeverity(tenant1Id,
                    AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant1High)
                    .as("Tenant 1 should only see their HIGH severity signals")
                    .hasSize(1);
            assertThat(tenant1High.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            var tenant2High = signalService.findByTenantAndSeverity(tenant2Id,
                    AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant2High)
                    .as("Tenant 2 should only see their HIGH severity signals")
                    .hasSize(1);
            assertThat(tenant2High.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findHighRiskSignals should enforce tenant filtering")
        void testFindHighRiskSignalsIsolation() {
            // Create high-risk signals for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.CRITICAL,
                    95.0, null, null, null));
            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.CRITICAL,
                    90.0, null, null, null));

            // Query for high-risk signals
            var tenant1HighRisk = signalService.findHighRiskSignals(tenant1Id, 75.0);
            assertThat(tenant1HighRisk)
                    .as("Tenant 1 should only see their high-risk signals")
                    .hasSize(1);
            assertThat(tenant1HighRisk.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            var tenant2HighRisk = signalService.findHighRiskSignals(tenant2Id, 75.0);
            assertThat(tenant2HighRisk)
                    .as("Tenant 2 should only see their high-risk signals")
                    .hasSize(1);
            assertThat(tenant2HighRisk.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findBySignalType should enforce tenant filtering")
        void testFindBySignalTypeIsolation() {
            // Create signals of same type for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));
            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));

            // Query for signals by type
            var tenant1ByType = signalService.findBySignalType(tenant1Id, "high_value");
            assertThat(tenant1ByType)
                    .as("Tenant 1 should only see their high_value signals")
                    .hasSize(1);
            assertThat(tenant1ByType.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            var tenant2ByType = signalService.findBySignalType(tenant2Id, "high_value");
            assertThat(tenant2ByType)
                    .as("Tenant 2 should only see their high_value signals")
                    .hasSize(1);
            assertThat(tenant2ByType.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: countUnresolvedByTenant should only count for specified tenant")
        void testCountUnresolvedByTenantIsolation() {
            // Create unresolved signals for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.MEDIUM,
                    65.0, null, null, null));
            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-3", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));

            long tenant1Count = signalService.countUnresolvedByTenant(tenant1Id);
            assertThat(tenant1Count)
                    .as("Tenant 1 should count only their unresolved signals")
                    .isEqualTo(2);

            long tenant2Count = signalService.countUnresolvedByTenant(tenant2Id);
            assertThat(tenant2Count)
                    .as("Tenant 2 should count only their unresolved signals")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("CRITICAL: countBySeverity should only count for specified tenant")
        void testCountBySeverityIsolation() {
            // Create HIGH severity signals for both tenants
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    85.0, null, null, null));
            signalService.createSignal(tenant1Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-2", "suspicious", AntiFraudSignal.SignalSeverity.HIGH,
                    75.0, null, null, null));
            signalService.createSignal(tenant2Id, new AntiFraudSignalService.CreateSignalRequest(
                    "txn-3", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                    95.0, null, null, null));

            long tenant1Count = signalService.countBySeverity(tenant1Id,
                    AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant1Count)
                    .as("Tenant 1 should count only their HIGH severity signals")
                    .isEqualTo(2);

            long tenant2Count = signalService.countBySeverity(tenant2Id,
                    AntiFraudSignal.SignalSeverity.HIGH);
            assertThat(tenant2Count)
                    .as("Tenant 2 should count only their HIGH severity signals")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("CRITICAL: Service should validate tenantId is not blank")
        void testTenantIdValidation() {
            assertThatThrownBy(() ->
                    signalService.createSignal("", new AntiFraudSignalService.CreateSignalRequest(
                            "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                            85.0, null, null, null)))
                    .as("Should reject blank tenantId")
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tenantId");

            assertThatThrownBy(() ->
                    signalService.createSignal(null, new AntiFraudSignalService.CreateSignalRequest(
                            "txn-1", "high_value", AntiFraudSignal.SignalSeverity.HIGH,
                            85.0, null, null, null)))
                    .as("Should reject null tenantId")
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tenantId");
        }
    }
}
