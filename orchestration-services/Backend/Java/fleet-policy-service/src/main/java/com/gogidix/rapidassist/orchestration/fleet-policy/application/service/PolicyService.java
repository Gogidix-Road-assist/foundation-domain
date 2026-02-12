package com.gogidix.rapidassist.orchestration.fleet_policy.application.service;

import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyValidationRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyComplianceResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyViolationResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.mapper.PolicyMapper;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyCompliance;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.*;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.service.PolicyValidationEngine;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.exception.ValidationException;
import com.gogidix.rapidassist.orchestration.fleet_policy.shared.requestcontext.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Policy management
 * Implements business logic and coordination
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyRuleRepository ruleRepository;
    private final PolicyComplianceRepository complianceRepository;
    private final PolicyViolationRepository violationRepository;
    private final PolicyMapper policyMapper;
    private final PolicyValidationEngine validationEngine;

    /**
     * Create a new policy
     */
    @Transactional
    public PolicyResponseDto createPolicy(PolicyRequestDto dto) {
        String tenantId = RequestContextHolder.getTenantId();

        // Check if policy code already exists
        if (policyRepository.existsByTenantIdAndPolicyCode(tenantId, dto.getPolicyCode())) {
            throw new ValidationException("Policy code already exists: " + dto.getPolicyCode());
        }

        Policy policy = policyMapper.toEntity(dto);
        policy.setTenantId(tenantId);
        policy.setCreatedBy(RequestContextHolder.getUserId());
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedBy(RequestContextHolder.getUserId());
        policy.setUpdatedAt(LocalDateTime.now());

        // Set default status if not provided
        if (policy.getStatus() == null) {
            policy.setStatus(Policy.PolicyStatus.DRAFT);
        }

        // Save policy
        policy = policyRepository.save(policy);

        // Save rules if provided
        if (dto.getRules() != null && !dto.getRules().isEmpty()) {
            for (var ruleDto : dto.getRules()) {
                PolicyRule rule = policyMapper.toRuleEntity(ruleDto);
                rule.setTenantId(tenantId);
                rule.setPolicyId(policy.getId());
                rule.setCreatedBy(RequestContextHolder.getUserId());
                rule.setCreatedAt(LocalDateTime.now());
                rule.setUpdatedBy(RequestContextHolder.getUserId());
                rule.setUpdatedAt(LocalDateTime.now());
                rule = ruleRepository.save(rule);
                policy.addRule(rule);
            }
        }

        log.info("Created policy: {} for tenant: {}", policy.getPolicyCode(), tenantId);
        return policyMapper.toResponseDto(policy);
    }

    /**
     * Update an existing policy
     */
    @Transactional
    public PolicyResponseDto updatePolicy(String id, PolicyRequestDto dto) {
        String tenantId = RequestContextHolder.getTenantId();

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found: " + id));

        // Verify tenant ownership
        if (!policy.getTenantId().equals(tenantId)) {
            throw new ValidationException("Policy does not belong to tenant");
        }

        // Check if new policy code conflicts
        if (!policy.getPolicyCode().equals(dto.getPolicyCode()) &&
            policyRepository.existsByTenantIdAndPolicyCode(tenantId, dto.getPolicyCode())) {
            throw new ValidationException("Policy code already exists: " + dto.getPolicyCode());
        }

        policyMapper.updateEntityFromDto(dto, policy);
        policy.setUpdatedBy(RequestContextHolder.getUserId());
        policy.setUpdatedAt(LocalDateTime.now());
        policy.setVersion(policy.getVersion() + 1);

        policy = policyRepository.save(policy);

        log.info("Updated policy: {} for tenant: {}", policy.getPolicyCode(), tenantId);
        return policyMapper.toResponseDto(policy);
    }

    /**
     * Get policy by ID
     */
    public PolicyResponseDto getPolicyById(String id) {
        String tenantId = RequestContextHolder.getTenantId();

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found: " + id));

        // Verify tenant ownership
        if (!policy.getTenantId().equals(tenantId)) {
            throw new ValidationException("Policy does not belong to tenant");
        }

        return policyMapper.toResponseDto(policy);
    }

    /**
     * Get all policies for tenant
     */
    public List<PolicyResponseDto> getAllPolicies() {
        String tenantId = RequestContextHolder.getTenantId();
        List<Policy> policies = policyRepository.findByTenantId(tenantId);
        return policyMapper.toPolicyResponseDtoList(policies);
    }

    /**
     * Get active policies
     */
    public List<PolicyResponseDto> getActivePolicies() {
        String tenantId = RequestContextHolder.getTenantId();
        List<Policy> policies = policyRepository.findByTenantIdAndIsActive(tenantId, true);
        return policyMapper.toPolicyResponseDtoList(policies);
    }

    /**
     * Get policies by type
     */
    public List<PolicyResponseDto> getPoliciesByType(Policy.PolicyType type) {
        String tenantId = RequestContextHolder.getTenantId();
        List<Policy> policies = policyRepository.findByTenantIdAndPolicyType(tenantId, type);
        return policyMapper.toPolicyResponseDtoList(policies);
    }

    /**
     * Activate a policy
     */
    @Transactional
    public PolicyResponseDto activatePolicy(String id) {
        String tenantId = RequestContextHolder.getTenantId();
        String userId = RequestContextHolder.getUserId();

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found: " + id));

        if (!policy.getTenantId().equals(tenantId)) {
            throw new ValidationException("Policy does not belong to tenant");
        }

        policy.activate(userId);
        policy.setUpdatedBy(userId);
        policy.setUpdatedAt(LocalDateTime.now());

        policy = policyRepository.save(policy);

        log.info("Activated policy: {} for tenant: {}", policy.getPolicyCode(), tenantId);
        return policyMapper.toResponseDto(policy);
    }

    /**
     * Deactivate a policy
     */
    @Transactional
    public PolicyResponseDto deactivatePolicy(String id) {
        String tenantId = RequestContextHolder.getTenantId();

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found: " + id));

        if (!policy.getTenantId().equals(tenantId)) {
            throw new ValidationException("Policy does not belong to tenant");
        }

        policy.deactivate();
        policy.setUpdatedBy(RequestContextHolder.getUserId());
        policy.setUpdatedAt(LocalDateTime.now());

        policy = policyRepository.save(policy);

        log.info("Deactivated policy: {} for tenant: {}", policy.getPolicyCode(), tenantId);
        return policyMapper.toResponseDto(policy);
    }

    /**
     * Delete a policy
     */
    @Transactional
    public void deletePolicy(String id) {
        String tenantId = RequestContextHolder.getTenantId();

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found: " + id));

        if (!policy.getTenantId().equals(tenantId)) {
            throw new ValidationException("Policy does not belong to tenant");
        }

        // Archive instead of delete
        policy.archive();
        policy.setUpdatedBy(RequestContextHolder.getUserId());
        policy.setUpdatedAt(LocalDateTime.now());

        policyRepository.save(policy);

        log.info("Archived policy: {} for tenant: {}", policy.getPolicyCode(), tenantId);
    }

    /**
     * Validate against a policy
     */
    @Transactional
    public List<PolicyViolationResponseDto> validatePolicy(PolicyValidationRequestDto dto) {
        String tenantId = RequestContextHolder.getTenantId();

        Policy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new NotFoundException("Policy not found: " + dto.getPolicyId()));

        if (!policy.getTenantId().equals(tenantId)) {
            throw new ValidationException("Policy does not belong to tenant");
        }

        // Validate against policy
        List<PolicyViolation> violations = validationEngine.validatePolicy(
                policy,
                dto.getEntityType(),
                dto.getEntityId(),
                dto.getEntityName(),
                dto.getData()
        );

        // Add location information if provided
        if (dto.getLocation() != null || dto.getLatitude() != null) {
            violations.forEach(v -> {
                v.setLocation(dto.getLocation());
                v.setLatitude(dto.getLatitude());
                v.setLongitude(dto.getLongitude());
            });
        }

        // Save violations
        List<PolicyViolation> savedViolations = violations.stream()
                .map(violationRepository::save)
                .collect(Collectors.toList());

        log.info("Validation completed for policy: {}, violations: {}", policy.getPolicyCode(), savedViolations.size());
        return policyMapper.toViolationResponseDtoList(savedViolations);
    }

    /**
     * Get compliance records for an entity
     */
    public List<PolicyComplianceResponseDto> getEntityCompliance(
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    ) {
        String tenantId = RequestContextHolder.getTenantId();

        List<PolicyCompliance> complianceRecords = complianceRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .filter(c -> c.getTenantId().equals(tenantId))
                .collect(Collectors.toList());

        return policyMapper.toComplianceResponseDtoList(complianceRecords);
    }

    /**
     * Get violations for an entity
     */
    public List<PolicyViolationResponseDto> getEntityViolations(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    ) {
        String tenantId = RequestContextHolder.getTenantId();

        List<PolicyViolation> violations = violationRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .filter(v -> v.getTenantId().equals(tenantId))
                .collect(Collectors.toList());

        return policyMapper.toViolationResponseDtoList(violations);
    }

    /**
     * Get open violations for an entity
     */
    public List<PolicyViolationResponseDto> getOpenViolations(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    ) {
        String tenantId = RequestContextHolder.getTenantId();

        List<PolicyViolation> violations = violationRepository.findOpenViolationsByEntity(entityType, entityId)
                .stream()
                .filter(v -> v.getTenantId().equals(tenantId))
                .collect(Collectors.toList());

        return policyMapper.toViolationResponseDtoList(violations);
    }

    /**
     * Search policies
     */
    public List<PolicyResponseDto> searchPolicies(String searchTerm) {
        String tenantId = RequestContextHolder.getTenantId();
        List<Policy> policies = policyRepository.searchPolicies(tenantId, searchTerm);
        return policyMapper.toPolicyResponseDtoList(policies);
    }
}
