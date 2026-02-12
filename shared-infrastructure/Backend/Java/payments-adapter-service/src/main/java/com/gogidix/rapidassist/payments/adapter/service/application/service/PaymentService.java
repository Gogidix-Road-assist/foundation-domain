package com.gogidix.rapidassist.payments.adapter.service.application.service;

import com.gogidix.rapidassist.payments.adapter.service.domain.model.Payment;
import com.gogidix.rapidassist.payments.adapter.service.infrastructure.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for Payment operations with CRITICAL tenant isolation.
 * <p>
 * ALL operations are tenant-isolated to prevent cross-tenant data access.
 * Financial data isolation is MANDATORY for compliance.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Create a new payment for a tenant.
     *
     * @param tenantId          The tenant ID (MUST be validated)
     * @param provider          The payment provider
     * @param providerReference The provider's reference number
     * @param amount            The payment amount
     * @param currency          The currency code (ISO 4217)
     * @return The created payment
     */
    public Payment createPayment(
            String tenantId,
            String provider,
            String providerReference,
            BigDecimal amount,
            String currency
    ) {
        log.info("Creating payment for tenant: {}, provider: {}, amount: {} {}", tenantId, provider, amount, currency);

        Payment payment = Payment.builder()
                .tenantId(tenantId)
                .provider(provider)
                .providerReference(providerReference)
                .amount(amount)
                .currency(currency)
                .status(Payment.PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Created payment with ID: {} for tenant: {}", saved.getId(), tenantId);
        return saved;
    }

    /**
     * Get a payment by ID and tenant ID.
     * <p>
     * CRITICAL: This method enforces tenant isolation by requiring both ID and tenantId.
     *
     * @param id       The payment ID
     * @param tenantId The tenant ID
     * @return Optional payment if found and belongs to tenant
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(String id, String tenantId) {
        log.debug("Fetching payment: {} for tenant: {}", id, tenantId);
        return paymentRepository.findByIdAndTenantId(id, tenantId);
    }

    /**
     * Get all payments for a tenant.
     * <p>
     * CRITICAL: Only returns payments for the specified tenant.
     *
     * @param tenantId The tenant ID
     * @return List of payments for the tenant
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByTenant(String tenantId) {
        log.debug("Fetching all payments for tenant: {}", tenantId);
        return paymentRepository.findByTenantId(tenantId);
    }

    /**
     * Get payments by tenant ID and status.
     *
     * @param tenantId The tenant ID
     * @param status   The payment status
     * @return List of payments with the given status
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByTenantAndStatus(String tenantId, Payment.PaymentStatus status) {
        log.debug("Fetching payments for tenant: {} with status: {}", tenantId, status);
        return paymentRepository.findByTenantIdAndStatus(tenantId, status);
    }

    /**
     * Get payments by tenant ID and provider.
     *
     * @param tenantId The tenant ID
     * @param provider The payment provider
     * @return List of payments from the provider
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByTenantAndProvider(String tenantId, String provider) {
        log.debug("Fetching payments for tenant: {} from provider: {}", tenantId, provider);
        return paymentRepository.findByTenantIdAndProvider(tenantId, provider);
    }

    /**
     * Get payment by tenant ID and provider reference.
     *
     * @param tenantId          The tenant ID
     * @param providerReference The provider's reference number
     * @return Optional payment if found
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByProviderReference(String tenantId, String providerReference) {
        log.debug("Fetching payment for tenant: {} with provider reference: {}", tenantId, providerReference);
        return paymentRepository.findByTenantIdAndProviderReference(tenantId, providerReference);
    }

    /**
     * Get payments by tenant ID within a date range.
     *
     * @param tenantId  The tenant ID
     * @param startDate Start of date range
     * @param endDate   End of date range
     * @return List of payments in the date range
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByTenantAndDateRange(String tenantId, Instant startDate, Instant endDate) {
        log.debug("Fetching payments for tenant: {} between {} and {}", tenantId, startDate, endDate);
        return paymentRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate);
    }

    /**
     * Update payment status to completed.
     *
     * @param id                The payment ID
     * @param tenantId          The tenant ID
     * @param externalPaymentId The external payment ID from provider
     * @return Optional updated payment if found
     */
    public Optional<Payment> markPaymentAsCompleted(String id, String tenantId, String externalPaymentId) {
        log.info("Marking payment: {} as completed for tenant: {}", id, tenantId);
        return paymentRepository.findByIdAndTenantId(id, tenantId)
                .map(payment -> {
                    payment.markAsCompleted(externalPaymentId);
                    return paymentRepository.save(payment);
                });
    }

    /**
     * Update payment status to failed.
     *
     * @param id       The payment ID
     * @param tenantId The tenant ID
     * @return Optional updated payment if found
     */
    public Optional<Payment> markPaymentAsFailed(String id, String tenantId) {
        log.warn("Marking payment: {} as failed for tenant: {}", id, tenantId);
        return paymentRepository.findByIdAndTenantId(id, tenantId)
                .map(payment -> {
                    payment.markAsFailed();
                    return paymentRepository.save(payment);
                });
    }

    /**
     * Get payment statistics for a tenant.
     *
     * @param tenantId The tenant ID
     * @return Payment statistics
     */
    @Transactional(readOnly = true)
    public PaymentStatistics getPaymentStatistics(String tenantId) {
        log.debug("Calculating payment statistics for tenant: {}", tenantId);

        long pendingCount = paymentRepository.countByTenantIdAndStatus(tenantId, Payment.PaymentStatus.PENDING);
        long processingCount = paymentRepository.countByTenantIdAndStatus(tenantId, Payment.PaymentStatus.PROCESSING);
        long completedCount = paymentRepository.countByTenantIdAndStatus(tenantId, Payment.PaymentStatus.COMPLETED);
        long failedCount = paymentRepository.countByTenantIdAndStatus(tenantId, Payment.PaymentStatus.FAILED);

        BigDecimal completedTotal = paymentRepository.sumAmountByTenantIdAndStatus(tenantId, Payment.PaymentStatus.COMPLETED)
                .orElse(BigDecimal.ZERO);

        return new PaymentStatistics(pendingCount, processingCount, completedCount, failedCount, completedTotal);
    }

    /**
     * Delete a payment by ID and tenant ID.
     * <p>
     * WARNING: This is a destructive operation. Use with caution.
     *
     * @param id       The payment ID
     * @param tenantId The tenant ID
     * @return true if deleted, false if not found
     */
    public boolean deletePayment(String id, String tenantId) {
        log.warn("Deleting payment: {} for tenant: {}", id, tenantId);
        if (paymentRepository.existsByIdAndTenantId(id, tenantId)) {
            paymentRepository.deleteByIdAndTenantId(id, tenantId);
            return true;
        }
        return false;
    }

    /**
     * Delete all payments for a tenant.
     * <p>
     * WARNING: This is a destructive operation. Use with extreme caution.
     *
     * @param tenantId The tenant ID
     */
    public void deleteAllPaymentsForTenant(String tenantId) {
        log.error("DELETING ALL PAYMENTS for tenant: {} - DANGEROUS OPERATION", tenantId);
        paymentRepository.deleteAllByTenantId(tenantId);
    }

    /**
     * Payment statistics record.
     */
    public record PaymentStatistics(
            long pendingCount,
            long processingCount,
            long completedCount,
            long failedCount,
            BigDecimal completedTotalAmount
    ) {}
}
