package com.gogidix.rapidassist.ai.riskassessment.domain.exception;

/**
 * Exception thrown when risk assessment data is invalid
 */
public class InvalidRiskAssessmentException extends RiskAssessmentException {

    public InvalidRiskAssessmentException(String message) {
        super(message);
    }

    public InvalidRiskAssessmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
