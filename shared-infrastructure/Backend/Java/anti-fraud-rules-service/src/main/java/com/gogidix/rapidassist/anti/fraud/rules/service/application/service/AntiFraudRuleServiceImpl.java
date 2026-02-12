package com.gogidix.rapidassist.anti.fraud.rules.service.application.service;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import com.gogidix.rapidassist.anti.fraud.rules.service.domain.port.in.AntiFraudRuleService;
import com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb.AntiFraudRuleDocument;
import com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb.AntiFraudRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of AntiFraudRuleService with CRITICAL tenant isolation enforcement.
 *
 * SECURITY: All operations validate tenantId to prevent cross-tenant data access.
 * - tenantId is validated for null/blank on all operations
 * - All repository queries filter by tenantId
 * - Cross-tenant access attempts throw IllegalStateException
 */
@Service
public class AntiFraudRuleServiceImpl implements AntiFraudRuleService {

    private static final Logger log = LoggerFactory.getLogger(AntiFraudRuleServiceImpl.class);

    private final AntiFraudRuleRepository repository;

    public AntiFraudRuleServiceImpl(AntiFraudRuleRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AntiFraudRule createRule(String tenantId, CreateRuleRequest request) {
        validateTenantId(tenantId);
        log.debug("Creating rule for tenant: {}, name: {}", tenantId, request.name());

        AntiFraudRule rule = AntiFraudRule.builder()
                .tenantId(tenantId)
                .name(request.name())
                .description(request.description())
                .ruleType(request.ruleType())
                .active(request.active())
                .priority(request.priority())
                .conditions(request.conditions())
                .actions(request.actions())
                .createdBy(request.createdBy())
                .build();

        AntiFraudRuleDocument document = AntiFraudRuleDocument.fromDomain(rule);
        AntiFraudRuleDocument saved = repository.save(document);

        log.info("Created rule {} for tenant: {}", saved.id(), tenantId);
        return saved.toDomain();
    }

    @Override
    @Transactional
    public AntiFraudRule updateRule(String tenantId, String ruleId, UpdateRuleRequest request) {
        validateTenantId(tenantId);
        if (ruleId == null || ruleId.isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be null or blank");
        }

        log.debug("Updating rule {} for tenant: {}", ruleId, tenantId);

        // CRITICAL: Verify rule belongs to tenant before updating
        AntiFraudRuleDocument existing = repository.findByIdAndTenantId(ruleId, tenantId)
                .orElseThrow(() -> new IllegalStateException(
                        "Rule not found or does not belong to tenant: " + tenantId));

        AntiFraudRule updated = AntiFraudRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name(request.name() != null ? request.name() : existing.name())
                .description(request.description() != null ? request.description() : existing.description())
                .ruleType(request.ruleType() != null ? request.ruleType() : existing.ruleType())
                .active(request.active())
                .priority(request.priority() >= 0 ? request.priority() : existing.priority())
                .conditions(request.conditions() != null ? request.conditions() : existing.conditions())
                .actions(request.actions() != null ? request.actions() : existing.actions())
                .createdAt(existing.createdAt())
                .createdBy(existing.createdBy())
                .updatedBy(request.updatedBy())
                .build();

        AntiFraudRuleDocument document = AntiFraudRuleDocument.updateFromDomain(existing, updated);
        AntiFraudRuleDocument saved = repository.save(document);

        log.info("Updated rule {} for tenant: {}", saved.id(), tenantId);
        return saved.toDomain();
    }

    @Override
    public Optional<AntiFraudRule> findById(String tenantId, String ruleId) {
        validateTenantId(tenantId);
        if (ruleId == null || ruleId.isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be null or blank");
        }

        log.debug("Finding rule {} for tenant: {}", ruleId, tenantId);

        // CRITICAL: Only return if rule belongs to tenant
        return repository.findByIdAndTenantId(ruleId, tenantId)
                .map(AntiFraudRuleDocument::toDomain);
    }

    @Override
    public List<AntiFraudRule> findByTenant(String tenantId) {
        validateTenantId(tenantId);
        log.debug("Finding all rules for tenant: {}", tenantId);

        // CRITICAL: Filter by tenantId
        return repository.findByTenantId(tenantId).stream()
                .map(AntiFraudRuleDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudRule> findActiveByTenant(String tenantId) {
        validateTenantId(tenantId);
        log.debug("Finding active rules for tenant: {}", tenantId);

        // CRITICAL: Filter by tenantId and active
        return repository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(AntiFraudRuleDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudRule> findActiveByTenantAndType(String tenantId, AntiFraudRule.RuleType ruleType) {
        validateTenantId(tenantId);
        log.debug("Finding active rules of type {} for tenant: {}", ruleType, tenantId);

        // CRITICAL: Filter by tenantId, type, and active
        return repository.findByTenantIdAndRuleTypeAndActiveTrue(tenantId, ruleType).stream()
                .map(AntiFraudRuleDocument::toDomain)
                .toList();
    }

    @Override
    public List<AntiFraudRule> findActiveByTenantOrderByPriority(String tenantId) {
        validateTenantId(tenantId);
        log.debug("Finding active rules ordered by priority for tenant: {}", tenantId);

        // CRITICAL: Filter by tenantId and order by priority
        return repository.findActiveByTenantIdOrderByPriorityDesc(tenantId).stream()
                .map(AntiFraudRuleDocument::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteRule(String tenantId, String ruleId) {
        validateTenantId(tenantId);
        if (ruleId == null || ruleId.isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be null or blank");
        }

        log.debug("Deleting rule {} for tenant: {}", ruleId, tenantId);

        // CRITICAL: Verify rule belongs to tenant before deleting
        if (!repository.existsByIdAndTenantId(ruleId, tenantId)) {
            throw new IllegalStateException(
                    "Rule not found or does not belong to tenant: " + tenantId);
        }

        repository.deleteById(ruleId);
        log.info("Deleted rule {} for tenant: {}", ruleId, tenantId);
    }

    @Override
    @Transactional
    public AntiFraudRule setActive(String tenantId, String ruleId, boolean active) {
        validateTenantId(tenantId);
        if (ruleId == null || ruleId.isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be null or blank");
        }

        log.debug("Setting active={} for rule {} in tenant: {}", active, ruleId, tenantId);

        // CRITICAL: Verify rule belongs to tenant before updating
        AntiFraudRuleDocument existing = repository.findByIdAndTenantId(ruleId, tenantId)
                .orElseThrow(() -> new IllegalStateException(
                        "Rule not found or does not belong to tenant: " + tenantId));

        AntiFraudRule updated = AntiFraudRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name(existing.name())
                .description(existing.description())
                .ruleType(existing.ruleType())
                .active(active)
                .priority(existing.priority())
                .conditions(existing.conditions())
                .actions(existing.actions())
                .createdAt(existing.createdAt())
                .createdBy(existing.createdBy())
                .build();

        AntiFraudRuleDocument document = AntiFraudRuleDocument.updateFromDomain(existing, updated);
        AntiFraudRuleDocument saved = repository.save(document);

        log.info("Set active={} for rule {} in tenant: {}", active, saved.id(), tenantId);
        return saved.toDomain();
    }

    @Override
    public long countActiveByTenant(String tenantId) {
        validateTenantId(tenantId);
        return repository.countByTenantIdAndActiveTrue(tenantId);
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
