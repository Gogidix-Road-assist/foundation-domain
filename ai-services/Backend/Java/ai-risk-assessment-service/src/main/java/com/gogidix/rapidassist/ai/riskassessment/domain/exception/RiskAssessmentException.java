package com.gogidix.rapidassist.ai.riskassessment.domain.exception;

/**
 * Base exception for Risk Assessment domain
 */
public class RiskAssessmentException extends RuntimeException {

    public RiskAssessmentException(String message) {
        super(message);
    }

    public RiskAssessmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
