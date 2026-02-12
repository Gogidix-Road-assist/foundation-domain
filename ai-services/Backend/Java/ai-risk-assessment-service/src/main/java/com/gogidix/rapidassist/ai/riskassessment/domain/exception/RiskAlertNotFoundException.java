package com.gogidix.rapidassist.ai.riskassessment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when risk alert is not found
 */
public class RiskAlertNotFoundException extends RiskAssessmentException {

    public RiskAlertNotFoundException(UUID id, String tenantId) {
        super(String.format("Risk alert not found: id=%s, tenantId=%s", id, tenantId));
    }

    public RiskAlertNotFoundException(String message) {
        super(message);
    }
}
