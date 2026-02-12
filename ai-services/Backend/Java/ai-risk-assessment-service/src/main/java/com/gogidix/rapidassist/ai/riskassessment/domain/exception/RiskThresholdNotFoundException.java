package com.gogidix.rapidassist.ai.riskassessment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when risk threshold is not found
 */
public class RiskThresholdNotFoundException extends RiskAssessmentException {

    public RiskThresholdNotFoundException(UUID id, String tenantId) {
        super(String.format("Risk threshold not found: id=%s, tenantId=%s", id, tenantId));
    }

    public RiskThresholdNotFoundException(String message) {
        super(message);
    }
}
