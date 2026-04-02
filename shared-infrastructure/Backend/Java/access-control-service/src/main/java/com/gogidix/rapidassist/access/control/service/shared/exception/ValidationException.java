package com.gogidix.rapidassist.access.control.service.shared.exception;

/**
 * Exception: ValidationException
 *
 * Thrown when input validation fails.
 */
public class ValidationException extends AccessControlException {

    private final String field;

    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message), "VALIDATION_ERROR");
        this.field = field;
    }

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.field = null;
    }

    public String getField() { return field; }
}
