package com.gogidix.rapidassist.payments.adapter.service.infrastructure.repository;

import com.gogidix.rapidassist.payments.adapter.service.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity with CRITICAL tenant isolation.
 * <p>
 * ALL query methods MUST filter by tenantId to prevent cross-tenant data access.
 * Direct access to findAll() or other methods without tenant filtering is BLOCKED.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * Find all payments for a specific tenant.
     * This is the ONLY way to list payments - tenant filtering is MANDATORY.
     *
     * @param tenantId The tenant ID
     * @return List of payments for the tenant
     */
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId ORDER BY p.createdAt DESC")
    List<Payment> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * Find payment by ID and tenant ID.
     * Both ID and tenantId must match for security.
     *
     * @param id       The payment ID
     * @param tenantId The tenant ID
     * @return Optional payment if found and belongs to tenant
     */
    @Query("SELECT p FROM Payment p WHERE p.id = :id AND p.tenantId = :tenantId")
    Optional<Payment> findByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

    /**
     * Find payments by tenant ID and status.
     *
     * @param tenantId The tenant ID
     * @param status   The payment status
     * @return List of payments with the given status
     */
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") Payment.PaymentStatus status);

    /**
     * Find payments by tenant ID and provider.
     *
     * @param tenantId The tenant ID
     * @param provider The payment provider
     * @return List of payments from the provider
     */
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.provider = :provider ORDER BY p.createdAt DESC")
    List<Payment> findByTenantIdAndProvider(@Param("tenantId") String tenantId, @Param("provider") String provider);

    /**
     * Find payment by tenant ID and provider reference.
     * Provider references are unique per tenant.
     *
     * @param tenantId          The tenant ID
     * @param providerReference The provider's reference number
     * @return Optional payment if found
     */
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.providerReference = :providerReference")
    Optional<Payment> findByTenantIdAndProviderReference(
            @Param("tenantId") String tenantId,
            @Param("providerReference") String providerReference
    );

    /**
     * Find payments by tenant ID within a date range.
     *
     * @param tenantId The tenant ID
     * @param startDate Start of date range
     * @param endDate   End of date range
     * @return List of payments in the date range
     */
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findByTenantIdAndCreatedAtBetween(
            @Param("tenantId") String tenantId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    /**
     * Find payments by tenant ID and external payment ID.
     *
     * @param tenantId          The tenant ID
     * @param externalPaymentId The external payment ID
     * @return Optional payment if found
     */
    @Query("SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.externalPaymentId = :externalPaymentId")
    Optional<Payment> findByTenantIdAndExternalPaymentId(
            @Param("tenantId") String tenantId,
            @Param("externalPaymentId") String externalPaymentId
    );

    /**
     * Count payments by tenant ID and status.
     *
     * @param tenantId The tenant ID
     * @param status   The payment status
     * @return Count of payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.tenantId = :tenantId AND p.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") Payment.PaymentStatus status);

    /**
     * Calculate total amount of payments by tenant ID and status.
     *
     * @param tenantId The tenant ID
     * @param status   The payment status
     * @return Total amount
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.tenantId = :tenantId AND p.status = :status")
    Optional<java.math.BigDecimal> sumAmountByTenantIdAndStatus(
            @Param("tenantId") String tenantId,
            @Param("status") Payment.PaymentStatus status
    );

    /**
     * CRITICAL: BLOCK direct findAll() to prevent cross-tenant data access.
     * Use findByTenantId() instead.
     */
    @Override
    default List<Payment> findAll() {
        throw new UnsupportedOperationException(
                "CRITICAL: Direct access to all payments is blocked for security. " +
                        "Use findByTenantId() to access payments for a specific tenant."
        );
    }

    /**
     * CRITICAL: BLOCK findAllById() to prevent cross-tenant data access.
     * Use findByIdAndTenantId() for each ID instead.
     */
    @Override
    default List<Payment> findAllById(Iterable<String> ids) {
        throw new UnsupportedOperationException(
                "CRITICAL: Batch access to payments is blocked for security. " +
                        "Use findByIdAndTenantId() for each payment individually."
        );
    }

    /**
     * CRITICAL: BLOCK deleteAll() to prevent mass deletion.
     */
    @Override
    default void deleteAll() {
        throw new UnsupportedOperationException(
                "CRITICAL: Mass deletion is blocked for security. " +
                        "Delete payments by tenant ID only."
        );
    }

    /**
     * CRITICAL: BLOCK deleteAllById() to prevent mass deletion.
     */
    @Override
    default void deleteAllById(Iterable<? extends String> ids) {
        throw new UnsupportedOperationException(
                "CRITICAL: Batch deletion is blocked for security. " +
                        "Use deleteByIdAndTenantId() for each payment individually."
        );
    }

    /**
     * Delete all payments for a specific tenant.
     * This is a privileged operation and should only be used in specific scenarios.
     *
     * @param tenantId The tenant ID
     */
    @Query("DELETE FROM Payment p WHERE p.tenantId = :tenantId")
    void deleteAllByTenantId(@Param("tenantId") String tenantId);

    /**
     * Delete a payment by ID and tenant ID.
     *
     * @param id       The payment ID
     * @param tenantId The tenant ID
     */
    @Query("DELETE FROM Payment p WHERE p.id = :id AND p.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

    /**
     * Check if a payment exists by ID and tenant ID.
     *
     * @param id       The payment ID
     * @param tenantId The tenant ID
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p WHERE p.id = :id AND p.tenantId = :tenantId")
    boolean existsByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}
