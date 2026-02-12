package com.gogidix.rapidassist.anti.fraud.signals.service.domain.port.in;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing anti-fraud signals with tenant isolation.
 * CRITICAL: All operations MUST be scoped to a specific tenant to prevent data leakage.
 */
public interface AntiFraudSignalService {

    /**
     * Create a new fraud signal for a specific tenant.
     * CRITICAL: tenantId is mandatory and will be enforced.
     */
    AntiFraudSignal createSignal(String tenantId, CreateSignalRequest request);

    /**
     * Resolve a fraud signal for a specific tenant.
     * CRITICAL: Will verify the signal belongs to the specified tenant before resolving.
     */
    AntiFraudSignal resolveSignal(String tenantId, String signalId, ResolveSignalRequest request);

    /**
     * Find a signal by ID for a specific tenant.
     * CRITICAL: Will only return the signal if it belongs to the specified tenant.
     */
    Optional<AntiFraudSignal> findById(String tenantId, String signalId);

    /**
     * Find all signals for a specific tenant with pagination.
     * CRITICAL: Only returns signals belonging to the specified tenant.
     */
    Page<AntiFraudSignal> findByTenant(String tenantId, org.springframework.data.domain.Pageable pageable);

    /**
     * Find all signals for a specific transaction within a tenant.
     * CRITICAL: Only returns signals for the specified transaction and tenant.
     */
    List<AntiFraudSignal> findByTransaction(String tenantId, String transactionId);

    /**
     * Find unresolved signals for a specific tenant.
     * CRITICAL: Only returns unresolved signals belonging to the specified tenant.
     */
    List<AntiFraudSignal> findUnresolvedByTenant(String tenantId);

    /**
     * Find signals by severity for a specific tenant.
     * CRITICAL: Only returns signals of the specified severity belonging to the tenant.
     */
    List<AntiFraudSignal> findByTenantAndSeverity(String tenantId, AntiFraudSignal.SignalSeverity severity);

    /**
     * Find high-risk signals for a specific tenant.
     * CRITICAL: Only returns high-risk signals belonging to the specified tenant.
     */
    List<AntiFraudSignal> findHighRiskSignals(String tenantId, double minRiskScore);

    /**
     * Find signals by signal type for a specific tenant.
     * CRITICAL: Only returns signals of the specified type belonging to the tenant.
     */
    List<AntiFraudSignal> findBySignalType(String tenantId, String signalType);

    /**
     * Find signals created within a date range for a specific tenant.
     * CRITICAL: Only returns signals within the date range belonging to the tenant.
     */
    List<AntiFraudSignal> findByDateRange(String tenantId, Instant startDate, Instant endDate);

    /**
     * Count unresolved signals for a specific tenant.
     */
    long countUnresolvedByTenant(String tenantId);

    /**
     * Count signals by severity for a specific tenant.
     */
    long countBySeverity(String tenantId, AntiFraudSignal.SignalSeverity severity);

    /**
     * Request DTO for creating a signal.
     */
    record CreateSignalRequest(
            String transactionId,
            String signalType,
            AntiFraudSignal.SignalSeverity severity,
            double riskScore,
            String description,
            java.util.Map<String, Object> metadata,
            String createdBy
    ) {}

    /**
     * Request DTO for resolving a signal.
     */
    record ResolveSignalRequest(
            boolean resolved,
            String resolvedBy,
            String resolutionNotes
    ) {}
}
