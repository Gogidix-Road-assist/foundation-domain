package com.gogidix.rapidassist.payments.adapter.service.application.service;

import com.gogidix.rapidassist.payments.adapter.service.domain.model.Payment;
import com.gogidix.rapidassist.payments.adapter.service.infrastructure.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL tenant isolation tests for PaymentService.
 * <p>
 * These tests verify that service layer maintains tenant isolation.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PaymentService - CRITICAL Tenant Isolation Tests")
class PaymentServiceTest {

    private static final String TENANT_1 = "tenant-1";
    private static final String TENANT_2 = "tenant-2";
    private static final String PROVIDER = "stripe";

    @Autowired
    private PaymentRepository paymentRepository;

    private TestEntityManager entityManager;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        entityManager = new TestEntityManager(entityManager.getEntityManager());
        paymentService = new PaymentService(paymentRepository);
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("createPayment - Should create payment with correct tenant ID")
    void createPayment_ShouldSetTenantId() {
        // When
        Payment payment = paymentService.createPayment(
                TENANT_1,
                PROVIDER,
                "ref-123",
                new BigDecimal("100.00"),
                "USD"
        );

        // Then
        assertThat(payment.getTenantId()).isEqualTo(TENANT_1);
        assertThat(payment.getId()).isNotNull();

        // Verify in database
        Optional<Payment> saved = paymentRepository.findByIdAndTenantId(payment.getId(), TENANT_1);
        assertThat(saved).isPresent();
        assertThat(saved.get().getTenantId()).isEqualTo(TENANT_1);
    }

    @Test
    @DisplayName("getPaymentById - Should only return payment for matching tenant")
    void getPaymentById_ShouldEnforceTenantIsolation() {
        // Given
        Payment tenant1Payment = paymentService.createPayment(
                TENANT_1,
                PROVIDER,
                "ref-1",
                new BigDecimal("100.00"),
                "USD"
        );

        // When
        Optional<Payment> foundForTenant1 = paymentService.getPaymentById(tenant1Payment.getId(), TENANT_1);
        Optional<Payment> foundForTenant2 = paymentService.getPaymentById(tenant1Payment.getId(), TENANT_2);

        // Then
        assertThat(foundForTenant1).isPresent();
        assertThat(foundForTenant1.get().getId()).isEqualTo(tenant1Payment.getId());

        // CRITICAL: Tenant 2 cannot access tenant 1's payment
        assertThat(foundForTenant2).isEmpty();
    }

    @Test
    @DisplayName("getPaymentsByTenant - Should only return payments for specified tenant")
    void getPaymentsByTenant_ShouldIsolateByTenant() {
        // Given
        paymentService.createPayment(TENANT_1, PROVIDER, "ref-1-1", new BigDecimal("100.00"), "USD");
        paymentService.createPayment(TENANT_1, PROVIDER, "ref-1-2", new BigDecimal("200.00"), "USD");
        paymentService.createPayment(TENANT_2, PROVIDER, "ref-2-1", new BigDecimal("300.00"), "USD");

        // When
        List<Payment> tenant1Payments = paymentService.getPaymentsByTenant(TENANT_1);
        List<Payment> tenant2Payments = paymentService.getPaymentsByTenant(TENANT_2);

        // Then
        assertThat(tenant1Payments).hasSize(2);
        assertThat(tenant1Payments)
                .allMatch(p -> p.getTenantId().equals(TENANT_1));

        assertThat(tenant2Payments).hasSize(1);
        assertThat(tenant2Payments)
                .allMatch(p -> p.getTenantId().equals(TENANT_2));
    }

    @Test
    @DisplayName("getPaymentsByTenantAndStatus - Should filter by both tenant and status")
    void getPaymentsByTenantAndStatus_ShouldFilterByTenantAndStatus() {
        // Given
        Payment p1 = paymentService.createPayment(TENANT_1, PROVIDER, "ref-1-1", new BigDecimal("100.00"), "USD");
        Payment p2 = paymentService.createPayment(TENANT_1, PROVIDER, "ref-1-2", new BigDecimal("200.00"), "USD");
        Payment p3 = paymentService.createPayment(TENANT_2, PROVIDER, "ref-2-1", new BigDecimal("300.00"), "USD");

        paymentService.markPaymentAsCompleted(p2.getId(), TENANT_1, "ext-123");

        // When
        List<Payment> tenant1Pending = paymentService.getPaymentsByTenantAndStatus(
                TENANT_1, Payment.PaymentStatus.PENDING
        );
        List<Payment> tenant2Pending = paymentService.getPaymentsByTenantAndStatus(
                TENANT_2, Payment.PaymentStatus.PENDING
        );

        // Then
        assertThat(tenant1Pending).hasSize(1);
        assertThat(tenant1Pending.get(0).getId()).isEqualTo(p1.getId());

        assertThat(tenant2Pending).hasSize(1);
        assertThat(tenant2Pending.get(0).getId()).isEqualTo(p3.getId());
    }

    @Test
    @DisplayName("getPaymentByProviderReference - Should only find payment for matching tenant")
    void getPaymentByProviderReference_ShouldEnforceTenantIsolation() {
        // Given
        paymentService.createPayment(TENANT_1, PROVIDER, "ref-123", new BigDecimal("100.00"), "USD");

        // When
        Optional<Payment> foundForTenant1 = paymentService.getPaymentByProviderReference(TENANT_1, "ref-123");
        Optional<Payment> foundForTenant2 = paymentService.getPaymentByProviderReference(TENANT_2, "ref-123");

        // Then
        assertThat(foundForTenant1).isPresent();
        assertThat(foundForTenant1.get().getTenantId()).isEqualTo(TENANT_1);

        // CRITICAL: Tenant 2 cannot access tenant 1's payment
        assertThat(foundForTenant2).isEmpty();
    }

    @Test
    @DisplayName("markPaymentAsCompleted - Should only update payment for matching tenant")
    void markPaymentAsCompleted_ShouldEnforceTenantIsolation() {
        // Given
        Payment tenant1Payment = paymentService.createPayment(
                TENANT_1, PROVIDER, "ref-1", new BigDecimal("100.00"), "USD"
        );
        Payment tenant2Payment = paymentService.createPayment(
                TENANT_2, PROVIDER, "ref-2", new BigDecimal("200.00"), "USD"
        );

        // When
        Optional<Payment> updatedForTenant1 = paymentService.markPaymentAsCompleted(
                tenant1Payment.getId(), TENANT_1, "ext-123"
        );
        Optional<Payment> notUpdatedForTenant2 = paymentService.markPaymentAsCompleted(
                tenant1Payment.getId(), TENANT_2, "ext-456"
        );

        // Then
        assertThat(updatedForTenant1).isPresent();
        assertThat(updatedForTenant1.get().getStatus()).isEqualTo(Payment.PaymentStatus.COMPLETED);

        // CRITICAL: Tenant 2 cannot update tenant 1's payment
        assertThat(notUpdatedForTenant2).isEmpty();

        // Verify database state
        Optional<Payment> stored = paymentRepository.findById(tenant1Payment.getId());
        assertThat(stored).isPresent();
        assertThat(stored.get().getStatus()).isEqualTo(Payment.PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("markPaymentAsFailed - Should only update payment for matching tenant")
    void markPaymentAsFailed_ShouldEnforceTenantIsolation() {
        // Given
        Payment tenant1Payment = paymentService.createPayment(
                TENANT_1, PROVIDER, "ref-1", new BigDecimal("100.00"), "USD"
        );

        // When
        Optional<Payment> updatedForTenant1 = paymentService.markPaymentAsFailed(
                tenant1Payment.getId(), TENANT_1
        );
        Optional<Payment> notUpdatedForTenant2 = paymentService.markPaymentAsFailed(
                tenant1Payment.getId(), TENANT_2
        );

        // Then
        assertThat(updatedForTenant1).isPresent();
        assertThat(updatedForTenant1.get().getStatus()).isEqualTo(Payment.PaymentStatus.FAILED);

        // CRITICAL: Tenant 2 cannot update tenant 1's payment
        assertThat(notUpdatedForTenant2).isEmpty();
    }

    @Test
    @DisplayName("getPaymentStatistics - Should only calculate for specified tenant")
    void getPaymentStatistics_ShouldCalculateForTenantOnly() {
        // Given
        Payment p1 = paymentService.createPayment(TENANT_1, PROVIDER, "ref-1-1", new BigDecimal("100.00"), "USD");
        Payment p2 = paymentService.createPayment(TENANT_1, PROVIDER, "ref-1-2", new BigDecimal("200.00"), "USD");
        Payment p3 = paymentService.createPayment(TENANT_2, PROVIDER, "ref-2-1", new BigDecimal("300.00"), "USD");

        paymentService.markPaymentAsCompleted(p2.getId(), TENANT_1, "ext-123");
        paymentService.markPaymentAsCompleted(p3.getId(), TENANT_2, "ext-456");

        // When
        PaymentService.PaymentStatistics tenant1Stats = paymentService.getPaymentStatistics(TENANT_1);
        PaymentService.PaymentStatistics tenant2Stats = paymentService.getPaymentStatistics(TENANT_2);

        // Then
        assertThat(tenant1Stats.pendingCount()).isEqualTo(1);
        assertThat(tenant1Stats.completedCount()).isEqualTo(1);
        assertThat(tenant1Stats.completedTotalAmount()).isEqualTo(new BigDecimal("200.00"));

        assertThat(tenant2Stats.pendingCount()).isEqualTo(0);
        assertThat(tenant2Stats.completedCount()).isEqualTo(1);
        assertThat(tenant2Stats.completedTotalAmount()).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("deletePayment - Should only delete payment for matching tenant")
    void deletePayment_ShouldEnforceTenantIsolation() {
        // Given
        Payment tenant1Payment = paymentService.createPayment(
                TENANT_1, PROVIDER, "ref-1", new BigDecimal("100.00"), "USD"
        );

        // When
        boolean deletedByTenant1 = paymentService.deletePayment(tenant1Payment.getId(), TENANT_1);
        boolean deletedByTenant2 = paymentService.deletePayment(tenant1Payment.getId(), TENANT_2);

        // Then
        assertThat(deletedByTenant1).isTrue();
        assertThat(deletedByTenant2).isFalse();

        // Verify database state
        Optional<Payment> stored = paymentRepository.findById(tenant1Payment.getId());
        assertThat(stored).isEmpty();
    }

    @Test
    @DisplayName("CRITICAL: Verify complete tenant isolation across all operations")
    void critical_CompleteTenantIsolationVerification() {
        // Given
        Payment p1 = paymentService.createPayment(TENANT_1, PROVIDER, "ref-1", new BigDecimal("100.00"), "USD");
        Payment p2 = paymentService.createPayment(TENANT_2, PROVIDER, "ref-2", new BigDecimal("200.00"), "USD");

        // Then - Verify all operations maintain isolation

        // Get by ID
        assertThat(paymentService.getPaymentById(p1.getId(), TENANT_1)).isPresent();
        assertThat(paymentService.getPaymentById(p1.getId(), TENANT_2)).isEmpty();

        // Get by provider reference
        assertThat(paymentService.getPaymentByProviderReference(TENANT_1, "ref-1")).isPresent();
        assertThat(paymentService.getPaymentByProviderReference(TENANT_2, "ref-1")).isEmpty();

        // Get all for tenant
        List<Payment> tenant1Payments = paymentService.getPaymentsByTenant(TENANT_1);
        List<Payment> tenant2Payments = paymentService.getPaymentsByTenant(TENANT_2);
        assertThat(tenant1Payments).hasSize(1);
        assertThat(tenant2Payments).hasSize(1);
        assertThat(tenant1Payments).noneMatch(p -> p.getId().equals(p2.getId()));
        assertThat(tenant2Payments).noneMatch(p -> p.getId().equals(p1.getId()));

        // Update operations
        assertThat(paymentService.markPaymentAsCompleted(p1.getId(), TENANT_1, "ext-123")).isPresent();
        assertThat(paymentService.markPaymentAsCompleted(p1.getId(), TENANT_2, "ext-456")).isEmpty();

        // Delete operations
        assertThat(paymentService.deletePayment(p2.getId(), TENANT_2)).isTrue();
        assertThat(paymentService.deletePayment(p2.getId(), TENANT_1)).isFalse();
    }
}
