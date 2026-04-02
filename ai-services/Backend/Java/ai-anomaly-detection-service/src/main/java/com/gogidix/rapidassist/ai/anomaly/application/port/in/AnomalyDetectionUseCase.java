package com.gogidix.rapidassist.ai.anomaly.application.port.in;

import com.gogidix.rapidassist.ai.anomaly.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.ai.anomaly.application.command.CreateDetectionRuleCommand;
import com.gogidix.rapidassist.ai.anomaly.application.command.DetectAnomaliesCommand;
import com.gogidix.rapidassist.ai.anomaly.application.command.UpdateDetectionRuleCommand;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyAlertDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyDetectionDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyPatternDto;
import com.gogidix.rapidassist.ai.anomaly.application.dto.DetectionRuleDto;

import java.util.List;

/**
 * Use Case interface for Anomaly Detection (Hexagonal Architecture - Inbound Port).
 * Defines the business operations exposed by the application layer.
 */
public interface AnomalyDetectionUseCase {

    /**
     * Detect anomalies in data
     */
    List<AnomalyDetectionDto> detectAnomalies(DetectAnomaliesCommand command);

    /**
     * Get an anomaly detection by ID
     */
    AnomalyDetectionDto getDetectionById(String tenantId, java.util.UUID detectionId);

    /**
     * List all detections with pagination
     */
    List<AnomalyDetectionDto> listDetections(String tenantId, int page, int size);

    /**
     * Create a detection rule
     */
    DetectionRuleDto createRule(CreateDetectionRuleCommand command);

    /**
     * Update a detection rule
     */
    DetectionRuleDto updateRule(UpdateDetectionRuleCommand command);

    /**
     * Delete a detection rule
     */
    void deleteRule(String tenantId, java.util.UUID ruleId);

    /**
     * Get a rule by ID
     */
    DetectionRuleDto getRuleById(String tenantId, java.util.UUID ruleId);

    /**
     * List all rules
     */
    List<DetectionRuleDto> listRules(String tenantId);

    /**
     * List all patterns
     */
    List<AnomalyPatternDto> listPatterns(String tenantId);

    /**
     * Acknowledge an alert
     */
    AnomalyAlertDto acknowledgeAlert(AcknowledgeAlertCommand command);
}
