package com.gogidix.rapidassist.shared.observability.library.aspect;

import com.gogidix.rapidassist.shared.observability.library.metrics.MetricsService;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Aspect for automatic method-level metrics collection.
 * Intercepts methods annotated with @Monitored and records metrics.
 */
@Aspect
@Component
public class MetricsAspect {

    private final MetricsService metricsService;

    public MetricsAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Around("@annotation(monitored)")
    public Object monitorMethod(ProceedingJoinPoint joinPoint, Monitored monitored) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        String metricName = monitored.value().isEmpty()
                ? className + "." + methodName
                : monitored.value();

        String[] tags = new String[]{
                "class", className,
                "method", methodName
        };

        if (monitored.recordLatency()) {
            Timer.Sample sample = metricsService.startTimer();
            try {
                Object result = joinPoint.proceed();
                metricsService.stopTimer(sample, metricName + ".latency", tags);
                metricsService.incrementCounter(metricName + ".success", tags);
                return result;
            } catch (Exception e) {
                metricsService.stopTimer(sample, metricName + ".latency",
                        extendTags(tags, "error", e.getClass().getSimpleName()));
                metricsService.incrementCounter(metricName + ".error",
                        extendTags(tags, "error", e.getClass().getSimpleName()));
                throw e;
            }
        } else {
            try {
                Object result = joinPoint.proceed();
                metricsService.incrementCounter(metricName + ".success", tags);
                return result;
            } catch (Exception e) {
                metricsService.incrementCounter(metricName + ".error",
                        extendTags(tags, "error", e.getClass().getSimpleName()));
                throw e;
            }
        }
    }

    @Around("@annotation(com.gogidix.rapidassist.shared.observability.library.aspect.Timed)")
    public Object timeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Timed timed = signature.getMethod().getAnnotation(Timed.class);

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String metricName = timed.value().isEmpty()
                ? "timer." + className + "." + methodName
                : timed.value();

        Timer.Sample sample = metricsService.startTimer();
        try {
            Object result = joinPoint.proceed();
            metricsService.stopTimer(sample, metricName);
            return result;
        } finally {
            // Timer stopped in stopTimer
        }
    }

    private String[] extendTags(String[] tags, String key, String value) {
        String[] extended = new String[tags.length + 2];
        System.arraycopy(tags, 0, extended, 0, tags.length);
        extended[tags.length] = key;
        extended[tags.length + 1] = value;
        return extended;
    }
}
