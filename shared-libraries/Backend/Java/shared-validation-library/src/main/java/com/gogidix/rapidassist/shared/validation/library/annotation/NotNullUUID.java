package com.gogidix.rapidassist.shared.validation.library.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation to ensure a UUID string is valid.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface NotNullUUID {

    String message() default "Invalid UUID format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
