package com.gogidix.rapidassist.dashboard.analytics.service.domain.port.in;

import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardAnalytics;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsCommand {

    CompletableFuture<DashboardAnalytics> recordEvent(
        String tenantId,
        String dashboardId,
        String userId,
        DashboardAnalytics.AnalyticsEventType eventType,
        java.util.Map<String, Object> eventData,
        DashboardAnalytics.UserSessionInfo sessionInfo,
        DashboardAnalytics.PerformanceMetrics performanceMetrics
    );

    CompletableFuture<Boolean> batchRecordEvents(java.util.List<DashboardAnalytics> events);

    CompletableFuture<Boolean> deleteEventsBefore(String tenantId, java.time.Instant timestamp);
}
