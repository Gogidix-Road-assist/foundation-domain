package com.gogidix.rapidassist.crossdomain.gps.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotNullUUIDValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullUUID {
    String message() default "UUID must not be null or empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
