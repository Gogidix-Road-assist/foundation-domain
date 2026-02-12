package com.gogidix.rapidassist.ai.analytics.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a metric is not found
 */
public class MetricNotFoundException extends AnalyticsException {

    public MetricNotFoundException(UUID metricId) {
        super("Metric not found with ID: " + metricId);
    }

    public MetricNotFoundException(String message) {
        super(message);
    }
}
