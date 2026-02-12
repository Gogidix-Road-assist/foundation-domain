package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.exception;

/**
 * Exception thrown when a business rule is violated.
 *
 * <p>This exception is used for business logic violations that are not
 * validation errors but rather violations of business rules or constraints.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class BusinessRuleException extends RuntimeException {

    private final String ruleCode;

    /**
     * Constructs a new BusinessRuleException.
     *
     * @param message the error message describing the rule violation
     */
    public BusinessRuleException(String message) {
        super(message);
        this.ruleCode = "BUSINESS_RULE_VIOLATION";
    }

    /**
     * Constructs a new BusinessRuleException with a specific rule code.
     *
     * @param ruleCode the code identifying the specific business rule
     * @param message  the error message describing the rule violation
     */
    public BusinessRuleException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    /**
     * Gets the rule code.
     *
     * @return the business rule code
     */
    public String getRuleCode() {
        return ruleCode;
    }
}
