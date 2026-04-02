package com.gogidix.rapidassist.shared.observability.library.aspect;

import com.gogidix.rapidassist.shared.observability.library.tracing.TracingService;
import io.micrometer.tracing.Span;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for automatic distributed tracing.
 * Intercepts methods annotated with @Traced and creates distributed tracing spans.
 */
@Aspect
@Component
public class TracingAspect {

    private final TracingService tracingService;

    public TracingAspect(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Around("@annotation(traced)")
    public Object traceMethod(ProceedingJoinPoint joinPoint, Traced traced) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        String spanName = traced.value().isEmpty()
                ? className + "." + methodName
                : traced.value();

        Span.Builder builder = tracingService.startSpan(spanName);

        // Add tags from annotation
        if (traced.tagArgs()) {
            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            if (paramNames != null) {
                for (int i = 0; i < Math.min(paramNames.length, args.length); i++) {
                    builder.tag("arg." + paramNames[i], String.valueOf(args[i]));
                }
            }
        }

        Span span = builder.tag("class", className)
                .tag("method", methodName)
                .start();

        try {
            Object result = joinPoint.proceed();

            // Add result tag if enabled and result is not too large
            if (traced.tagResult() && result != null) {
                String resultStr = String.valueOf(result);
                if (resultStr.length() > 100) {
                    resultStr = resultStr.substring(0, 100) + "...";
                }
                span.tag("result", resultStr);
            }

            span.tag("status", "success");
            return result;
        } catch (Exception e) {
            span.tag("status", "error");
            span.tag("error", e.getClass().getSimpleName());
            span.tag("error.message", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
