package com.gogidix.rapidassist.shared.observability.library.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic metrics collection.
 * When applied to a method, the MetricsAspect will intercept and record metrics.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {

    /**
     * Custom metric name. If empty, uses className.methodName
     * @return metric name
     */
    String value() default "";

    /**
     * Whether to record latency (timing) for this method
     * @return true if latency should be recorded
     */
    boolean recordLatency() default true;

    /**
     * Whether to record errors separately
     * @return true if errors should be recorded separately
     */
    boolean recordErrors() default true;
}
