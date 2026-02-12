package com.gogidix.rapidassist.ai.riskassessment.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when risk assessment is not found
 */
public class RiskAssessmentNotFoundException extends RiskAssessmentException {

    public RiskAssessmentNotFoundException(UUID id, String tenantId) {
        super(String.format("Risk assessment not found: id=%s, tenantId=%s", id, tenantId));
    }

    public RiskAssessmentNotFoundException(String code, String tenantId) {
        super(String.format("Risk assessment not found: code=%s, tenantId=%s", code, tenantId));
    }

    public RiskAssessmentNotFoundException(String message) {
        super(message);
    }
}
