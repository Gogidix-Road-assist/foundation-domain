package com.gogidix.rapidassist.ai.fraud.application.service;

import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudAlertDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudCaseDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudPatternDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudRuleDto;
import com.gogidix.rapidassist.ai.fraud.application.mapper.FraudDetectionMapper;
import com.gogidix.rapidassist.ai.fraud.domain.model.*;
import com.gogidix.rapidassist.ai.fraud.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application Service for Fraud Detection operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudDetectionRepositoryPort detectionRepository;
    private final FraudAlertRepositoryPort alertRepository;
    private final FraudCaseRepositoryPort caseRepository;
    private final FraudPatternRepositoryPort patternRepository;
    private final FraudRuleRepositoryPort ruleRepository;
    private final FraudDetectionMapper mapper;

    // ==================== Fraud Detection Operations ====================

    public FraudDetectionDto detectFraud(String tenantId, String entityType, String entityId,
                                        FraudRiskLevel riskLevel, Double riskScore,
                                        String detectionMethod, java.util.Map<String, Object> details) {
        log.info("Detecting fraud for tenant: {}, entity: {}/{}", tenantId, entityType, entityId);

        var detection = FraudDetection.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(riskLevel)
                .riskScore(riskScore)
                .detectionMethod(detectionMethod)
                .detectionDetails(details)
                .detectedPatterns(new java.util.ArrayList<>())
                .status("PENDING")
                .requiresReview(riskLevel == FraudRiskLevel.HIGH || riskLevel == FraudRiskLevel.CRITICAL)
                .detectedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = detectionRepository.save(tenantId, detection);
        log.info("Fraud detection created: {}", saved.getId());
        return mapper.toDto(saved);
    }

    public FraudDetectionDto getDetectionById(String tenantId, UUID id) {
        log.info("Getting fraud detection: {} for tenant: {}", id, tenantId);
        var detection = detectionRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudDetection not found: " + id));
        return mapper.toDto(detection);
    }

    public List<FraudDetectionDto> getAllDetections(String tenantId) {
        log.info("Getting all fraud detections for tenant: {}", tenantId);
        var detections = detectionRepository.findByTenantId(tenantId);
        return mapper.toDtoList(detections);
    }

    public List<FraudDetectionDto> getDetectionsByStatus(String tenantId, String status) {
        log.info("Getting fraud detections by status: {} for tenant: {}", status, tenantId);
        var detections = detectionRepository.findByStatus(tenantId, status);
        return mapper.toDtoList(detections);
    }

    public List<FraudDetectionDto> getPendingReviewDetections(String tenantId) {
        log.info("Getting pending review detections for tenant: {}", tenantId);
        var detections = detectionRepository.findPendingReview(tenantId);
        return mapper.toDtoList(detections);
    }

    public FraudDetectionDto updateDetectionReview(String tenantId, UUID id, String reviewer, String notes) {
        log.info("Updating detection review: {} by reviewer: {}", id, reviewer);
        var detection = detectionRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudDetection not found: " + id));
        detection.markAsReviewed(reviewer, notes);
        var saved = detectionRepository.save(tenantId, detection);
        return mapper.toDto(saved);
    }

    // ==================== Fraud Alert Operations ====================

    public FraudAlertDto createAlert(String tenantId, UUID detectionId, String alertType,
                                    String severity, String title, String description) {
        log.info("Creating alert for detection: {} in tenant: {}", detectionId, tenantId);

        var alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .fraudDetectionId(detectionId)
                .alertType(alertType)
                .severity(severity)
                .title(title)
                .description(description)
                .status("PENDING")
                .escalationLevel(0)
                .caseCreated(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = alertRepository.save(tenantId, alert);
        log.info("Fraud alert created: {}", saved.getId());
        return mapper.toDto(saved);
    }

    public List<FraudAlertDto> getAllAlerts(String tenantId) {
        log.info("Getting all fraud alerts for tenant: {}", tenantId);
        var alerts = alertRepository.findByTenantId(tenantId);
        return mapper.toAlertDtoList(alerts);
    }

    public List<FraudAlertDto> getAlertsByStatus(String tenantId, String status) {
        log.info("Getting fraud alerts by status: {} for tenant: {}", status, tenantId);
        var alerts = alertRepository.findByStatus(tenantId, status);
        return mapper.toAlertDtoList(alerts);
    }

    public FraudAlertDto acknowledgeAlert(String tenantId, UUID id, String acknowledgedBy, String notes) {
        log.info("Acknowledging alert: {} by user: {}", id, acknowledgedBy);
        var alert = alertRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudAlert not found: " + id));
        alert.acknowledge(acknowledgedBy, notes);
        var saved = alertRepository.save(tenantId, alert);
        return mapper.toDto(saved);
    }

    public FraudAlertDto resolveAlert(String tenantId, UUID id, String resolvedBy, String notes) {
        log.info("Resolving alert: {} by user: {}", id, resolvedBy);
        var alert = alertRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudAlert not found: " + id));
        alert.resolve(resolvedBy, notes);
        var saved = alertRepository.save(tenantId, alert);
        return mapper.toDto(saved);
    }

    public List<FraudAlertDto> getPendingAlerts(String tenantId) {
        log.info("Getting pending alerts for tenant: {}", tenantId);
        var alerts = alertRepository.findPendingAlerts(tenantId);
        return mapper.toAlertDtoList(alerts);
    }

    public List<FraudAlertDto> getCriticalAlerts(String tenantId) {
        log.info("Getting critical alerts for tenant: {}", tenantId);
        var alerts = alertRepository.findCriticalAlerts(tenantId);
        return mapper.toAlertDtoList(alerts);
    }

    // ==================== Fraud Case Operations ====================

    public FraudCaseDto createCase(String tenantId, String caseType, String title, String description,
                                  String priority, String assignedTo) {
        log.info("Creating fraud case: {} for tenant: {}", title, tenantId);

        var caseEntity = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .caseNumber(generateCaseNumber())
                .caseType(caseType)
                .title(title)
                .description(description)
                .priority(priority)
                .assignedTo(assignedTo)
                .linkedAlerts(new java.util.ArrayList<>())
                .linkedDetections(new java.util.ArrayList<>())
                .status("OPEN")
                .openedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        caseEntity.open(caseType, assignedTo);
        var saved = caseRepository.save(tenantId, caseEntity);
        log.info("Fraud case created: {}", saved.getCaseNumber());
        return mapper.toDto(saved);
    }

    public List<FraudCaseDto> getAllCases(String tenantId) {
        log.info("Getting all fraud cases for tenant: {}", tenantId);
        var cases = caseRepository.findByTenantId(tenantId);
        return mapper.toCaseDtoList(cases);
    }

    public FraudCaseDto getCaseById(String tenantId, UUID id) {
        log.info("Getting fraud case: {} for tenant: {}", id, tenantId);
        var caseEntity = caseRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudCase not found: " + id));
        return mapper.toDto(caseEntity);
    }

    public FraudCaseDto updateCase(String tenantId, UUID id, String assignedTo, String finding, String decision) {
        log.info("Updating fraud case: {} for tenant: {}", id, tenantId);
        var caseEntity = caseRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudCase not found: " + id));

        if (assignedTo != null) {
            caseEntity.assignTo(assignedTo);
        }

        caseEntity.updateFindings(null, finding, decision);
        var saved = caseRepository.save(tenantId, caseEntity);
        return mapper.toDto(saved);
    }

    public FraudCaseDto closeCase(String tenantId, UUID id, String closedBy, String reason, String outcome) {
        log.info("Closing fraud case: {} by user: {}", id, closedBy);
        var caseEntity = caseRepository.findById(tenantId, id)
                .orElseThrow(() -> new RuntimeException("FraudCase not found: " + id));
        caseEntity.close(closedBy, reason, outcome);
        var saved = caseRepository.save(tenantId, caseEntity);
        return mapper.toDto(saved);
    }

    public List<FraudCaseDto> getOpenCases(String tenantId) {
        log.info("Getting open cases for tenant: {}", tenantId);
        var cases = caseRepository.findOpenCases(tenantId);
        return mapper.toCaseDtoList(cases);
    }

    // ==================== Fraud Pattern Operations ====================

    public FraudPatternDto createPattern(String tenantId, String patternName, String patternType,
                                       String description, java.util.Map<String, Object> definition) {
        log.info("Creating fraud pattern: {} for tenant: {}", patternName, tenantId);

        var pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .patternName(patternName)
                .patternType(patternType)
                .description(description)
                .patternDefinition(definition)
                .isActive(true)
                .priority(0)
                .detectionCount(0)
                .falsePositiveCount(0)
                .truePositiveCount(0)
                .precision(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = patternRepository.save(tenantId, pattern);
        log.info("Fraud pattern created: {}", saved.getId());
        return mapper.toDto(saved);
    }

    public List<FraudPatternDto> getAllPatterns(String tenantId) {
        log.info("Getting all fraud patterns for tenant: {}", tenantId);
        var patterns = patternRepository.findByTenantId(tenantId);
        return mapper.toPatternDtoList(patterns);
    }

    public List<FraudPatternDto> getActivePatterns(String tenantId) {
        log.info("Getting active patterns for tenant: {}", tenantId);
        var patterns = patternRepository.findActivePatterns(tenantId);
        return mapper.toPatternDtoList(patterns);
    }

    // ==================== Fraud Rule Operations ====================

    public FraudRuleDto createRule(String tenantId, String ruleName, String ruleCode, String description,
                                  String ruleType, java.util.Map<String, Object> conditions,
                                  String action, Integer priority) {
        log.info("Creating fraud rule: {} for tenant: {}", ruleName, tenantId);

        var rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .ruleName(ruleName)
                .ruleCode(ruleCode)
                .description(description)
                .ruleType(ruleType)
                .conditions(conditions)
                .action(action)
                .priority(priority)
                .isActive(true)
                .autoBlock(false)
                .requireReview(false)
                .executionCount(0)
                .triggerCount(0)
                .falsePositiveRate(0.0)
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = ruleRepository.save(tenantId, rule);
        log.info("Fraud rule created: {}", saved.getId());
        return mapper.toDto(saved);
    }

    public List<FraudRuleDto> getAllRules(String tenantId) {
        log.info("Getting all fraud rules for tenant: {}", tenantId);
        var rules = ruleRepository.findByTenantId(tenantId);
        return mapper.toRuleDtoList(rules);
    }

    public List<FraudRuleDto> getActiveRules(String tenantId) {
        log.info("Getting active rules for tenant: {}", tenantId);
        var rules = ruleRepository.findActiveRules(tenantId);
        return mapper.toRuleDtoList(rules);
    }

    // ==================== Helper Methods ====================

    private String generateCaseNumber() {
        return "FC-" + System.currentTimeMillis();
    }
}
