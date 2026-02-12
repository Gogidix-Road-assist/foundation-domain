package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.exception;

import java.util.List;

/**
 * Exception thrown when validation fails.
 *
 * <p>This exception is used to encapsulate validation errors from request validation,
 * business rule validation, or data integrity constraints.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {

    private final List<String> validationErrors;

    /**
     * Constructs a new ValidationException with a single error message.
     *
     * @param message the validation error message
     */
    public ValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }

    /**
     * Constructs a new ValidationException with multiple validation errors.
     *
     * @param message the main validation error message
     * @param errors  list of specific validation errors
     */
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.validationErrors = errors;
    }

    /**
     * Gets the list of validation errors.
     *
     * @return list of validation error messages
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
