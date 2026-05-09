package com.gogidix.rapidassist.dashboard.analytics.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for dashboard analytics and metrics
 */
public class DashboardAnalytics {

    private final String id;
    private final String tenantId;
    private final String dashboardId;
    private final String userId;
    private final AnalyticsEventType eventType;
    private final Map<String, Object> eventData;
    private final UserSessionInfo sessionInfo;
    private final PerformanceMetrics performanceMetrics;
    private final String environment;
    private final Instant timestamp;
    private final Integer version;

    public enum AnalyticsEventType {
        DASHBOARD_VIEW,
        DASHBOARD_EDIT,
        WIDGET_CLICK,
        WIDGET_REFRESH,
        DASHBOARD_SHARE,
        DASHBOARD_EXPORT,
        DASHBOARD_CLONE,
        FILTER_APPLY,
        TIME_RANGE_CHANGE,
        CUSTOM_EVENT
    }

    public record UserSessionInfo(
        String sessionId,
        String userId,
        String userRole,
        String userAgent,
        String ipAddress,
        Map<String, String> customAttributes
    ) {
        public static UserSessionInfo defaults() {
            return new UserSessionInfo("", "", "", "", "", Map.of());
        }
    }

    public record PerformanceMetrics(
        long loadTimeMs,
        long renderTimeMs,
        long dataFetchTimeMs,
        int widgetCount,
        int dataPointCount,
        Map<String, Long> customMetrics
    ) {
        public static PerformanceMetrics defaults() {
            return new PerformanceMetrics(0, 0, 0, 0, 0, Map.of());
        }
    }

    private DashboardAnalytics(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.dashboardId = builder.dashboardId;
        this.userId = builder.userId;
        this.eventType = builder.eventType;
        this.eventData = builder.eventData;
        this.sessionInfo = builder.sessionInfo;
        this.performanceMetrics = builder.performanceMetrics;
        this.environment = builder.environment;
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPerformanceCritical() {
        return performanceMetrics.loadTimeMs() > 3000 ||
               performanceMetrics.renderTimeMs() > 2000 ||
               performanceMetrics.dataFetchTimeMs() > 5000;
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String dashboardId() { return dashboardId; }
    public String userId() { return userId; }
    public AnalyticsEventType eventType() { return eventType; }
    public Map<String, Object> eventData() { return eventData; }
    public UserSessionInfo sessionInfo() { return sessionInfo; }
    public PerformanceMetrics performanceMetrics() { return performanceMetrics; }
    public String environment() { return environment; }
    public Instant timestamp() { return timestamp; }
    public Integer version() { return version; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String dashboardId;
        private String userId;
        private AnalyticsEventType eventType;
        private Map<String, Object> eventData = Map.of();
        private UserSessionInfo sessionInfo = UserSessionInfo.defaults();
        private PerformanceMetrics performanceMetrics = PerformanceMetrics.defaults();
        private String environment = "production";
        private Instant timestamp;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder dashboardId(String dashboardId) { this.dashboardId = dashboardId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder eventType(AnalyticsEventType eventType) { this.eventType = eventType; return this; }
        public Builder eventData(Map<String, Object> eventData) { this.eventData = eventData; return this; }
        public Builder sessionInfo(UserSessionInfo sessionInfo) { this.sessionInfo = sessionInfo; return this; }
        public Builder performanceMetrics(PerformanceMetrics performanceMetrics) { this.performanceMetrics = performanceMetrics; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public DashboardAnalytics build() {
            return new DashboardAnalytics(this);
        }
    }
}
