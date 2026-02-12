package com.gogidix.rapidassist.payments.adapter.service.infrastructure.repository;

import com.gogidix.rapidassist.payments.adapter.service.domain.model.Payment;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CRITICAL tenant isolation tests for PaymentRepository.
 * <p>
 * These tests verify that NO cross-tenant data access is possible.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PaymentRepository - CRITICAL Tenant Isolation Tests")
class PaymentRepositoryTest {

    private static final String TENANT_1 = "tenant-1";
    private static final String TENANT_2 = "tenant-2";
    private static final String PROVIDER = "stripe";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Payment tenant1Payment1;
    private Payment tenant1Payment2;
    private Payment tenant2Payment1;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();

        // Create payments for tenant 1
        tenant1Payment1 = Payment.builder()
                .tenantId(TENANT_1)
                .provider(PROVIDER)
                .providerReference("ref-1-1")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .status(Payment.PaymentStatus.PENDING)
                .build();

        tenant1Payment2 = Payment.builder()
                .tenantId(TENANT_1)
                .provider(PROVIDER)
                .providerReference("ref-1-2")
                .amount(new BigDecimal("200.00"))
                .currency("USD")
                .status(Payment.PaymentStatus.COMPLETED)
                .build();

        // Create payment for tenant 2
        tenant2Payment1 = Payment.builder()
                .tenantId(TENANT_2)
                .provider(PROVIDER)
                .providerReference("ref-2-1")
                .amount(new BigDecimal("300.00"))
                .currency("USD")
                .status(Payment.PaymentStatus.PENDING)
                .build();

        tenant1Payment1 = paymentRepository.save(tenant1Payment1);
        tenant1Payment2 = paymentRepository.save(tenant1Payment2);
        tenant2Payment1 = paymentRepository.save(tenant2Payment1);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findByTenantId - Should only return payments for specified tenant")
    void findByTenantId_ShouldOnlyReturnPaymentsForTenant() {
        // When
        List<Payment> tenant1Payments = paymentRepository.findByTenantId(TENANT_1);
        List<Payment> tenant2Payments = paymentRepository.findByTenantId(TENANT_2);

        // Then
        assertThat(tenant1Payments).hasSize(2);
        assertThat(tenant1Payments)
                .allMatch(p -> p.getTenantId().equals(TENANT_1));

        assertThat(tenant2Payments).hasSize(1);
        assertThat(tenant2Payments)
                .allMatch(p -> p.getTenantId().equals(TENANT_2));
    }

    @Test
    @DisplayName("findByIdAndTenantId - Should only return payment if both ID and tenant match")
    void findByIdAndTenantId_ShouldRequireBothIdAndTenantMatch() {
        // When
        Optional<Payment> foundForTenant1 = paymentRepository.findByIdAndTenantId(
                tenant1Payment1.getId(), TENANT_1
        );
        Optional<Payment> notFoundForTenant2 = paymentRepository.findByIdAndTenantId(
                tenant1Payment1.getId(), TENANT_2
        );

        // Then
        assertThat(foundForTenant1).isPresent();
        assertThat(foundForTenant1.get().getId()).isEqualTo(tenant1Payment1.getId());

        // CRITICAL: Tenant 2 cannot access tenant 1's payment
        assertThat(notFoundForTenant2).isEmpty();
    }

    @Test
    @DisplayName("findByTenantIdAndStatus - Should filter by both tenant and status")
    void findByTenantIdAndStatus_ShouldFilterByTenantAndStatus() {
        // When
        List<Payment> tenant1Pending = paymentRepository.findByTenantIdAndStatus(
                TENANT_1, Payment.PaymentStatus.PENDING
        );
        List<Payment> tenant2Pending = paymentRepository.findByTenantIdAndStatus(
                TENANT_2, Payment.PaymentStatus.PENDING
        );
        List<Payment> tenant1Completed = paymentRepository.findByTenantIdAndStatus(
                TENANT_1, Payment.PaymentStatus.COMPLETED
        );

        // Then
        assertThat(tenant1Pending).hasSize(1);
        assertThat(tenant1Pending.get(0).getId()).isEqualTo(tenant1Payment1.getId());

        assertThat(tenant2Pending).hasSize(1);
        assertThat(tenant2Pending.get(0).getId()).isEqualTo(tenant2Payment1.getId());

        assertThat(tenant1Completed).hasSize(1);
        assertThat(tenant1Completed.get(0).getId()).isEqualTo(tenant1Payment2.getId());
    }

    @Test
    @DisplayName("findByTenantIdAndProvider - Should filter by both tenant and provider")
    void findByTenantIdAndProvider_ShouldFilterByTenantAndProvider() {
        // When
        List<Payment> tenant1Stripe = paymentRepository.findByTenantIdAndProvider(TENANT_1, PROVIDER);
        List<Payment> tenant2Stripe = paymentRepository.findByTenantIdAndProvider(TENANT_2, PROVIDER);
        List<Payment> tenant1Paypal = paymentRepository.findByTenantIdAndProvider(TENANT_1, "paypal");

        // Then
        assertThat(tenant1Stripe).hasSize(2);
        assertThat(tenant2Stripe).hasSize(1);
        assertThat(tenant1Paypal).isEmpty();
    }

    @Test
    @DisplayName("findByTenantIdAndProviderReference - Should only find payment for matching tenant and reference")
    void findByTenantIdAndProviderReference_ShouldRequireTenantMatch() {
        // When
        Optional<Payment> foundForTenant1 = paymentRepository.findByTenantIdAndProviderReference(
                TENANT_1, "ref-1-1"
        );
        Optional<Payment> notFoundForTenant2 = paymentRepository.findByTenantIdAndProviderReference(
                TENANT_2, "ref-1-1"
        );

        // Then
        assertThat(foundForTenant1).isPresent();
        // CRITICAL: Tenant 2 cannot access tenant 1's payment even with the same reference
        assertThat(notFoundForTenant2).isEmpty();
    }

    @Test
    @DisplayName("findByTenantIdAndCreatedAtBetween - Should filter by tenant and date range")
    void findByTenantIdAndCreatedAtBetween_ShouldFilterByTenant() {
        // When
        List<Payment> tenant1Payments = paymentRepository.findByTenantIdAndCreatedAtBetween(
                TENANT_1,
                tenant1Payment1.getCreatedAt().minusSeconds(60),
                tenant1Payment1.getCreatedAt().plusSeconds(60)
        );

        // Then
        assertThat(tenant1Payments).hasSize(2);
        assertThat(tenant1Payments)
                .allMatch(p -> p.getTenantId().equals(TENANT_1));
    }

    @Test
    @DisplayName("countByTenantIdAndStatus - Should count only for specified tenant")
    void countByTenantIdAndStatus_ShouldCountOnlyForTenant() {
        // When
        long tenant1PendingCount = paymentRepository.countByTenantIdAndStatus(
                TENANT_1, Payment.PaymentStatus.PENDING
        );
        long tenant2PendingCount = paymentRepository.countByTenantIdAndStatus(
                TENANT_2, Payment.PaymentStatus.PENDING
        );

        // Then
        assertThat(tenant1PendingCount).isEqualTo(1);
        assertThat(tenant2PendingCount).isEqualTo(1);
    }

    @Test
    @DisplayName("sumAmountByTenantIdAndStatus - Should sum only for specified tenant")
    void sumAmountByTenantIdAndStatus_ShouldSumOnlyForTenant() {
        // When
        Optional<BigDecimal> tenant1CompletedTotal = paymentRepository.sumAmountByTenantIdAndStatus(
                TENANT_1, Payment.PaymentStatus.COMPLETED
        );
        Optional<BigDecimal> tenant2CompletedTotal = paymentRepository.sumAmountByTenantIdAndStatus(
                TENANT_2, Payment.PaymentStatus.COMPLETED
        );

        // Then
        assertThat(tenant1CompletedTotal).isPresent();
        assertThat(tenant1CompletedTotal.get()).isEqualTo(new BigDecimal("200.00"));

        assertThat(tenant2CompletedTotal).isPresent();
        assertThat(tenant2CompletedTotal.get()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("deleteByIdAndTenantId - Should only delete if both ID and tenant match")
    void deleteByIdAndTenantId_ShouldRequireTenantMatch() {
        // When
        paymentRepository.deleteByIdAndTenantId(tenant1Payment1.getId(), TENANT_1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Payment> deletedForTenant1 = paymentRepository.findByIdAndTenantId(
                tenant1Payment1.getId(), TENANT_1
        );
        assertThat(deletedForTenant1).isEmpty();

        // Verify tenant 2's payment still exists
        Optional<Payment> stillExistsForTenant2 = paymentRepository.findByIdAndTenantId(
                tenant2Payment1.getId(), TENANT_2
        );
        assertThat(stillExistsForTenant2).isPresent();
    }

    @Test
    @DisplayName("deleteAllByTenantId - Should only delete payments for specified tenant")
    void deleteAllByTenantId_ShouldOnlyDeleteForTenant() {
        // When
        paymentRepository.deleteAllByTenantId(TENANT_1);
        entityManager.flush();
        entityManager.clear();

        // Then
        List<Payment> tenant1Payments = paymentRepository.findByTenantId(TENANT_1);
        List<Payment> tenant2Payments = paymentRepository.findByTenantId(TENANT_2);

        assertThat(tenant1Payments).isEmpty();
        assertThat(tenant2Payments).hasSize(1);
    }

    @Test
    @DisplayName("existsByIdAndTenantId - Should check both ID and tenant")
    void existsByIdAndTenantId_ShouldCheckBothIdAndTenant() {
        // When
        boolean existsForTenant1 = paymentRepository.existsByIdAndTenantId(
                tenant1Payment1.getId(), TENANT_1
        );
        boolean notExistsForTenant2 = paymentRepository.existsByIdAndTenantId(
                tenant1Payment1.getId(), TENANT_2
        );

        // Then
        assertThat(existsForTenant1).isTrue();
        assertThat(notExistsForTenant2).isFalse();
    }

    @Test
    @DisplayName("findAll - Should be BLOCKED to prevent cross-tenant access")
    void findAll_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> paymentRepository.findAll())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL")
                .hasMessageContaining("blocked for security");
    }

    @Test
    @DisplayName("findAllById - Should be BLOCKED to prevent cross-tenant access")
    void findAllById_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> paymentRepository.findAllById(List.of(tenant1Payment1.getId())))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL")
                .hasMessageContaining("blocked for security");
    }

    @Test
    @DisplayName("deleteAll - Should be BLOCKED to prevent mass deletion")
    void deleteAll_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> paymentRepository.deleteAll())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL")
                .hasMessageContaining("blocked for security");
    }

    @Test
    @DisplayName("deleteAllById - Should be BLOCKED to prevent mass deletion")
    void deleteAllById_ShouldBeBlocked() {
        // When & Then
        assertThatThrownBy(() -> paymentRepository.deleteAllById(List.of(tenant1Payment1.getId())))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("CRITICAL")
                .hasMessageContaining("blocked for security");
    }

    @Test
    @DisplayName("CRITICAL: Tenant 2 cannot access any of Tenant 1's payments by any method")
    void critical_Tenant2CannotAccessTenant1Payments() {
        // Test all possible access methods
        assertThat(paymentRepository.findByTenantId(TENANT_2))
                .noneMatch(p -> p.getTenantId().equals(TENANT_1));

        assertThat(paymentRepository.findByIdAndTenantId(tenant1Payment1.getId(), TENANT_2))
                .isEmpty();

        assertThat(paymentRepository.findByTenantIdAndProviderReference(TENANT_2, "ref-1-1"))
                .isEmpty();

        assertThat(paymentRepository.existsByIdAndTenantId(tenant1Payment1.getId(), TENANT_2))
                .isFalse();

        // Verify counts are separate
        assertThat(paymentRepository.findByTenantId(TENANT_1)).hasSize(2);
        assertThat(paymentRepository.findByTenantId(TENANT_2)).hasSize(1);
    }
}
