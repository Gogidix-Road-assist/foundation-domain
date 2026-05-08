package com.gogidix.rapidassist.shared.observability.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

import static com.gogidix.rapidassist.shared.observability.library.aspect.Logged.LogLevel;

/**
 * Aspect for automatic method entry/exit logging.
 * Intercepts methods annotated with @Logged and logs method execution.
 */
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(logged)")
    public Object logMethod(ProceedingJoinPoint joinPoint, Logged logged) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Logger logger = LoggerFactory.getLogger(signature.getDeclaringType());

        // Generate request ID if enabled
        if (logged.requestId()) {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
        }

        // Log method entry if enabled
        if (logged.level() != LogLevel.OFF) {
            String logMessage = buildEntryMessage(className, methodName, joinPoint.getArgs(), logged.logArgs());
            logAtLevel(logger, logged.level(), logMessage);
        }

        long startTime = logged.duration() ? System.currentTimeMillis() : 0;

        try {
            Object result = joinPoint.proceed();

            // Log method exit if enabled
            if (logged.level() != LogLevel.OFF && logged.logResult()) {
                String exitMessage = buildExitMessage(className, methodName, result, logged.logResult());
                logAtLevel(logger, logged.level(), exitMessage);
            }

            // Log duration if enabled
            if (logged.duration()) {
                long duration = System.currentTimeMillis() - startTime;
                MDC.put("duration", duration + "ms");
                logger.debug("Method {}.{} executed in {}ms", className, methodName, duration);
                MDC.remove("duration");
            }

            return result;
        } catch (Exception e) {
            // Log error with exception
            String errorMessage = String.format("Method %s.%s failed: %s",
                    className, methodName, e.getMessage());
            logAtLevel(logger, logged.errorLevel(), errorMessage);
            throw e;
        } finally {
            if (logged.requestId()) {
                MDC.remove("requestId");
            }
        }
    }

    private String buildEntryMessage(String className, String methodName, Object[] args, boolean logArgs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Entering ").append(className).append(".").append(methodName).append("(");
        if (logArgs && args.length > 0) {
            sb.append("args=").append(Arrays.toString(args));
        }
        sb.append(")");
        return sb.toString();
    }

    private String buildExitMessage(String className, String methodName, Object result, boolean logResult) {
        if (logResult && result != null) {
            return String.format("Exiting %s.%s with result: %s", className, methodName, result);
        }
        return String.format("Exiting %s.%s", className, methodName);
    }

    private void logAtLevel(Logger logger, LogLevel level, String message) {
        switch (level) {
            case TRACE -> logger.trace(message);
            case DEBUG -> logger.debug(message);
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERROR -> logger.error(message);
        }
    }
}
