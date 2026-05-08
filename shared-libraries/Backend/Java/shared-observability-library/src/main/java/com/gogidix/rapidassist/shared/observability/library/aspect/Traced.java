package com.gogidix.rapidassist.shared.observability.library.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic distributed tracing.
 * When applied to a method, the TracingAspect will create a distributed tracing span.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {

    /**
     * Custom span name. If empty, uses className.methodName
     * @return span name
     */
    String value() default "";

    /**
     * Whether to tag method arguments
     * @return true if arguments should be tagged
     */
    boolean tagArgs() default false;

    /**
     * Whether to tag the return value
     * @return true if result should be tagged
     */
    boolean tagResult() default false;

    /**
     * Span kind for this trace
     * @return span kind
     */
    SpanKind kind() default SpanKind.INTERNAL;

    /**
     * Span kind enumeration
     */
    enum SpanKind {
        INTERNAL, SERVER, CLIENT, PRODUCER, CONSUMER
    }
}
