package com.gogidix.rapidassist.anti.fraud.signals.service.application.service;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import com.gogidix.rapidassist.anti.fraud.signals.service.domain.port.in.AntiFraudSignalService;
import com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb.AntiFraudSignalDocument;
import com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb.AntiFraudSignalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AntiFraudSignalService with CRITICAL tenant isolation enforcement.
 *
 * SECURITY: All operations validate tenantId to prevent cross-tenant data access.
 * - tenantId is validated for null/blank on all operations
 * - All repository queries filter by tenantId
 * - Cross-tenant access attempts throw IllegalStateException
 */
@Service
public class AntiFraudSignalServiceImpl implements AntiFraudSignalService {

    private static final Logger log = LoggerFactory.getLogger(AntiFraudSignalServiceImpl.class);

    private final AntiFraudSignalRepository repository;

    public AntiFraudSignalServiceImpl(AntiFraudSignalRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AntiFraudSignal createSignal(String tenantId, CreateSignalRequest request) {
        validateTenantId(tenantId);
        log.debug("Creating fraud signal for tenant: {}, transaction: {}", tenantId, request.transactionId());

        AntiFraudSignal signal = AntiFraudSignal.builder()
                .tenantId(tenantId)
                .transactionId(request.transactionId())
                .signalType(request.signalType())
                .severity(request.severity())
                .riskScore(request.riskScore())
                .description(request.description())
                .metadata(request.metadata())
                .resolved(false)
                .createdBy(request.createdBy())
                .build();

        AntiFraudSignalDocument document = AntiFraudSignalDocument.fromDomain(signal);
        AntiFraudSignalDocument saved = repository.save(document);

        log.info("Created fraud signal {} for tenant: {}", saved.id(), tenantId);
        return saved.toDomain();
    }

    @Override
    @Transactional
    public AntiFraudSignal resolveSignal(String tenantId, String signalId, ResolveSignalRequest request) {
        validateTenantId(tenantId);
        if (signalId == null || signalId.isBlank()) {
            throw new IllegalArgumentException("signalId cannot be null or blank");
        }

        log.debug("Resolving signal {} for tenant: {}", signalId, tenantId);

        // CRITICAL: Verify signal belongs to tenant before resolving
        AntiFraudSignalDocument existing = repository.findByIdAndTenantId(signalId, tenantId)
                .orElseThrow(() -> new IllegalStateException(
                        "Signal not found or does not belong to tenant: " + tenantId));

        AntiFraudSignal resolved = AntiFraudSignal.builder()
                .id(signalId)
                .tenantId(tenantId)
                .transactionId(existing.transactionId())
                .signalType(existing.signalType())
                .severity(existing.severity())
                .riskScore(existing.riskScore())
                .description(existing.description())
                .metadata(existing.metadata())
                .resolved(request.resolved())
                .resolvedBy(request.resolvedBy())
                .resolvedAt(Instant.now())
                .resolutionNotes(request.resolutionNotes())
                .createdAt(existing.createdAt())
                .createdBy(existing.createdBy())
                .build();

        AntiFraudSignalDocument document = AntiFraudSignalDocument.updateFromDomain(existing, resolved);
        AntiFraudSignalDocument saved = repository.save(document);

        log.info("Resolved signal {} for tenant: {}", saved.id(), tenantId);
        return saved.toDomain();
    }

    @Override
    public Optional<AntiFraudSignal> findById(String tenantId, String signalId) {
        validateTenantId(tenantId);
        if (signalId == null || signalId.isBlank()) {
            throw new IllegalArgumentException("signalId cannot be null or blank");
        }

        log.debug("Finding signal {} for tenant: {}", signalId, tenantId);

        // CRITICAL: Only return if signal belongs to tenant
        return repository.findByIdAndTenantId(signalId, tenantId)
                .map(AntiFraudSignalDocument::toDomain);
    }

    @Override
    public Page<AntiFraudSignal> findByTenant(String tenantId, Pageable pageable) {
        validateTenantId(tenantId);
        log.debug("Finding signals for tenant: {}, page: {}", tenantId, pageable.getPageNumber());

        // CRITICAL: Filter by tenantId
        return repository.findByTenantId(tenantId, pageable)
                .map(AntiFraudSignalDocument::toDomain);
    }

    @Override
    public List<AntiFraudSignal> findByTransaction(String tenantId, String transactionId) {
        validateTenantId(tenantId);
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("transactionId cannot be null or blank");
        }

        log.debug("Finding signals for transaction {} in tenant: {}", transactionId, tenantId);

        // CRITICAL: Filter by tenantId and transactionId
        return repository.findByTenantIdAndTransactionId(tenantId, transactionId).stream()
                .map(AntiFraudSignalDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudSignal> findUnresolvedByTenant(String tenantId) {
        validateTenantId(tenantId);
        log.debug("Finding unresolved signals for tenant: {}", tenantId);

        // CRITICAL: Filter by tenantId and resolved status
        return repository.findByTenantIdAndResolvedFalse(tenantId).stream()
                .map(AntiFraudSignalDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudSignal> findByTenantAndSeverity(String tenantId, AntiFraudSignal.SignalSeverity severity) {
        validateTenantId(tenantId);
        log.debug("Finding signals with severity {} for tenant: {}", severity, tenantId);

        // CRITICAL: Filter by tenantId and severity
        return repository.findByTenantIdAndSeverityOrderByCreatedAtDesc(tenantId, severity).stream()
                .map(AntiFraudSignalDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudSignal> findHighRiskSignals(String tenantId, double minRiskScore) {
        validateTenantId(tenantId);
        log.debug("Finding high-risk signals (score >= {}) for tenant: {}", minRiskScore, tenantId);

        // CRITICAL: Filter by tenantId and risk score
        return repository.findHighRiskUnresolvedByTenant(tenantId, minRiskScore).stream()
                .map(AntiFraudSignalDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudSignal> findBySignalType(String tenantId, String signalType) {
        validateTenantId(tenantId);
        if (signalType == null || signalType.isBlank()) {
            throw new IllegalArgumentException("signalType cannot be null or blank");
        }

        log.debug("Finding signals by type {} for tenant: {}", signalType, tenantId);

        // CRITICAL: Filter by tenantId and signal type
        return repository.findByTenantIdAndSignalTypeOrderByCreatedAtDesc(tenantId, signalType).stream()
                .map(AntiFraudSignalDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudSignal> findByDateRange(String tenantId, Instant startDate, Instant endDate) {
        validateTenantId(tenantId);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate cannot be null");
        }

        log.debug("Finding signals between {} and {} for tenant: {}", startDate, endDate, tenantId);

        // CRITICAL: Filter by tenantId and date range
        return repository.findByTenantIdAndCreatedAtBetweenOrderByCreatedAtDesc(tenantId, startDate, endDate).stream()
                .map(AntiFraudSignalDocument::toDomain)
                .toList();
    }

    @Override
    public long countUnresolvedByTenant(String tenantId) {
        validateTenantId(tenantId);
        return repository.countByTenantIdAndResolvedFalse(tenantId);
    }

    @Override
    public long countBySeverity(String tenantId, AntiFraudSignal.SignalSeverity severity) {
        validateTenantId(tenantId);
        return repository.countByTenantIdAndSeverity(tenantId, severity);
    }

    /**
     * Validate tenantId is not null or blank.
     */
    private void validateTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId cannot be null or blank");
        }
    }
}
