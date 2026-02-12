package com.gogidix.rapidassist.ai.anomaly.application.service;

import com.gogidix.rapidassist.ai.anomaly.application.dto.*;
import com.gogidix.rapidassist.ai.anomaly.application.mapper.AnomalyAlertMapper;
import com.gogidix.rapidassist.ai.anomaly.application.mapper.AnomalyDetectionMapper;
import com.gogidix.rapidassist.ai.anomaly.application.command.*;
import com.gogidix.rapidassist.ai.anomaly.application.mapper.DetectionRuleMapper;
import com.gogidix.rapidassist.ai.anomaly.application.port.in.AnomalyDetectionUseCase;
import com.gogidix.rapidassist.ai.anomaly.domain.model.*;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyAlertRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyDetectionRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.DetectionRuleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application Service for Anomaly Detection.
 * Implements the use cases and orchestrates business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnomalyDetectionApplicationService implements AnomalyDetectionUseCase {

    private final AnomalyDetectionRepositoryPort detectionRepository;
    private final DetectionRuleRepositoryPort ruleRepository;
    private final AnomalyAlertRepositoryPort alertRepository;

    private final AnomalyDetectionMapper detectionMapper;
    private final DetectionRuleMapper ruleMapper;
    private final AnomalyAlertMapper alertMapper;

    @Override
    public List<AnomalyDetectionDto> detectAnomalies(DetectAnomaliesCommand command) {
        log.info("Detecting anomalies for data source: {}", command.getDataSource());

        List<AnomalyDetection> detections = new ArrayList<>();

        // Get active rules for the data source
        List<DetectionRule> activeRules = ruleRepository.findByDataSource(command.getTenantId(), command.getDataSource())
                .stream()
                .filter(DetectionRule::isActiveRule)
                .collect(Collectors.toList());

        // Apply detection logic (simplified - in real scenario would use ML models)
        for (DetectionRule rule : activeRules) {
            if (matchesRule(command.getData(), rule.getConditions())) {
                AnomalyDetection detection = createDetection(command, rule);
                detections.add(detection);

                // Create alert if rule specifies
                if (rule.shouldCreateAlert()) {
                    AnomalyAlert alert = createAlert(detection, rule);
                    alertRepository.save(command.getTenantId(), alert);
                }
            }
        }

        // Save detections
        for (AnomalyDetection detection : detections) {
            detectionRepository.save(command.getTenantId(), detection);
        }

        log.info("Detected {} anomalies", detections.size());
        return detectionMapper.toDtoList(detections);
    }

    @Override
    public AnomalyDetectionDto getDetectionById(String tenantId, UUID detectionId) {
        return detectionRepository.findById(tenantId, detectionId)
                .map(detectionMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Detection not found: " + detectionId));
    }

    @Override
    public List<AnomalyDetectionDto> listDetections(String tenantId, int page, int size) {
        List<AnomalyDetection> detections = detectionRepository.findByTenantId(tenantId);

        // Simple pagination (in production, use MongoDB pagination)
        int start = page * size;
        int end = Math.min(start + size, detections.size());

        if (start >= detections.size()) {
            return List.of();
        }

        return detectionMapper.toDtoList(detections.subList(start, end));
    }

    @Override
    public DetectionRuleDto createRule(CreateDetectionRuleCommand command) {
        DetectionRule rule = DetectionRule.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .ruleType(command.getRuleType())
                .patternIds(command.getPatternIds())
                .conditions(command.getConditions())
                .dataSource(command.getDataSource())
                .priority(command.getPriority() != null ? command.getPriority() : 5)
                .isActive(true)
                .createAlert(command.getCreateAlert())
                .alertSeverity(command.getAlertSeverity())
                .notificationChannels(command.getNotificationChannels())
                .category(command.getCategory())
                .metadata(command.getMetadata())
                .createdBy(command.getCreatedBy())
                .updatedBy(command.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(1L)
                .build();

        DetectionRule savedRule = ruleRepository.save(command.getTenantId(), rule);
        log.info("Created detection rule: {}", savedRule.getId());

        return ruleMapper.toDto(savedRule);
    }

    @Override
    public DetectionRuleDto updateRule(UpdateDetectionRuleCommand command) {
        DetectionRule rule = ruleRepository.findById(command.getTenantId(), command.getRuleId())
                .orElseThrow(() -> new RuntimeException("Rule not found: " + command.getRuleId()));

        if (command.getName() != null) {
            rule.setName(command.getName());
        }
        if (command.getDescription() != null) {
            rule.setDescription(command.getDescription());
        }
        if (command.getConditions() != null) {
            rule.setConditions(command.getConditions());
        }
        if (command.getPriority() != null) {
            rule.setPriority(command.getPriority());
        }
        if (command.getIsActive() != null) {
            rule.setIsActive(command.getIsActive());
        }
        if (command.getMetadata() != null) {
            rule.setMetadata(command.getMetadata());
        }

        rule.setUpdatedBy(command.getUpdatedBy());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setVersion(rule.getVersion() + 1);

        DetectionRule savedRule = ruleRepository.save(command.getTenantId(), rule);
        log.info("Updated detection rule: {}", savedRule.getId());

        return ruleMapper.toDto(savedRule);
    }

    @Override
    public void deleteRule(String tenantId, UUID ruleId) {
        if (!ruleRepository.exists(tenantId, ruleId)) {
            throw new RuntimeException("Rule not found: " + ruleId);
        }
        ruleRepository.delete(tenantId, ruleId);
        log.info("Deleted detection rule: {}", ruleId);
    }

    @Override
    public DetectionRuleDto getRuleById(String tenantId, UUID ruleId) {
        return ruleRepository.findById(tenantId, ruleId)
                .map(ruleMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId));
    }

    @Override
    public List<DetectionRuleDto> listRules(String tenantId) {
        List<DetectionRule> rules = ruleRepository.findByTenantId(tenantId);
        return ruleMapper.toDtoList(rules);
    }

    @Override
    public List<AnomalyPatternDto> listPatterns(String tenantId) {
        // For now, return empty list as pattern repository is not implemented
        // In production, would query pattern repository
        return List.of();
    }

    @Override
    public AnomalyAlertDto acknowledgeAlert(AcknowledgeAlertCommand command) {
        AnomalyAlert alert = alertRepository.findById(command.getTenantId(), command.getAlertId())
                .orElseThrow(() -> new RuntimeException("Alert not found: " + command.getAlertId()));

        alert.acknowledge(command.getAcknowledgedBy());

        AnomalyAlert savedAlert = alertRepository.save(command.getTenantId(), alert);
        log.info("Acknowledged alert: {}", savedAlert.getId());

        return alertMapper.toDto(savedAlert);
    }

    /**
     * Helper method to check if data matches rule conditions
     */
    private boolean matchesRule(Map<String, Object> data, Map<String, Object> conditions) {
        // Simplified matching logic
        // In production, this would use sophisticated pattern matching
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, Object> condition : conditions.entrySet()) {
            String key = condition.getKey();
            Object expectedValue = condition.getValue();
            Object actualValue = data.get(key);

            if (actualValue == null || !actualValue.equals(expectedValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Helper method to create a detection
     */
    private AnomalyDetection createDetection(DetectAnomaliesCommand command, DetectionRule rule) {
        return AnomalyDetection.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .dataSource(command.getDataSource())
                .dataPoint(command.getData().toString())
                .severity(AnomalySeverity.MEDIUM)
                .status(AnomalyStatus.PENDING)
                .anomalyScore(0.75)
                .confidence(0.85)
                .detectionMethod(command.getDetectionMethod() != null ? command.getDetectionMethod() : "RULE_BASED")
                .data(command.getData())
                .ruleId(rule.getId().toString())
                .description("Anomaly detected by rule: " + rule.getName())
                .recommendation("Review and investigate the anomaly")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .detectedAt(LocalDateTime.now())
                .version(1L)
                .build();
    }

    /**
     * Helper method to create an alert
     */
    private AnomalyAlert createAlert(AnomalyDetection detection, DetectionRule rule) {
        return AnomalyAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(detection.getTenantId())
                .detectionId(detection.getId())
                .ruleId(rule.getId())
                .title("Anomaly Alert: " + rule.getName())
                .message(detection.getDescription())
                .severity(rule.getAlertSeverity() != null ?
                    AlertSeverity.valueOf(rule.getAlertSeverity().name()) : AlertSeverity.WARNING)
                .status(AlertStatus.OPEN)
                .notificationChannels(rule.getNotificationChannels())
                .triggeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .escalationLevel(0)
                .version(1L)
                .build();
    }
}
