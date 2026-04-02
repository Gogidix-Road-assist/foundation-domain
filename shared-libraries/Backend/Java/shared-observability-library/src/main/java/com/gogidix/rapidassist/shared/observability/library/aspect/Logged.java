package com.gogidix.rapidassist.shared.observability.library.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic entry/exit logging.
 * When applied to a method, the LoggingAspect will log method execution.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {

    /**
     * Log level for method logging
     * @return log level
     */
    LogLevel level() default LogLevel.DEBUG;

    /**
     * Log level for error logging
     * @return error log level
     */
    LogLevel errorLevel() default LogLevel.ERROR;

    /**
     * Whether to log method arguments
     * @return true if arguments should be logged
     */
    boolean logArgs() default false;

    /**
     * Whether to log method result
     * @return true if result should be logged
     */
    boolean logResult() default false;

    /**
     * Whether to generate and log request ID
     * @return true if request ID should be generated
     */
    boolean requestId() default true;

    /**
     * Whether to log method execution duration
     * @return true if duration should be logged
     */
    boolean duration() default true;

    /**
     * Log level enumeration
     */
    enum LogLevel {
        OFF, TRACE, DEBUG, INFO, WARN, ERROR
    }
}
