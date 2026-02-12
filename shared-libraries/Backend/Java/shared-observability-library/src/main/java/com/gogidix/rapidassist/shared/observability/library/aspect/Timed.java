package com.gogidix.rapidassist.shared.observability.library.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic timing.
 * Simpler than @Monitored - only records timing metrics.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {

    /**
     * Custom metric name. If empty, uses className.methodName
     * @return metric name
     */
    String value() default "";

    /**
     * Whether to record percentiles (p50, p95, p99)
     * @return true if percentiles should be recorded
     */
    boolean percentiles() default true;

    /**
     * Description of what is being timed
     * @return description
     */
    String description() default "";
}
