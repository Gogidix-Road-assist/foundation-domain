package com.gogidix.rapidassist.ai.analytics.domain.model;

/**
 * Enum representing the type of metric
 */
public enum MetricType {
    /**
     * Counter metric (monotonically increasing)
     */
    COUNTER,

    /**
     * Gauge metric (can go up or down)
     */
    GAUGE,

    /**
     * Histogram metric
     */
    HISTOGRAM,

    /**
     * Summary metric
     */
    SUMMARY,

    /**
     * Custom calculated metric
     */
    CUSTOM
}
