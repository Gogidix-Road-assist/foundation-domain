package com.gogidix.rapidassist.dashboard.analytics.service.domain.port.in;

import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardAnalytics;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardUsageReport;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsQuery {

    CompletableFuture<List<DashboardAnalytics>> getEventsByDashboard(String dashboardId, Instant startDate, Instant endDate);

    CompletableFuture<List<DashboardAnalytics>> getEventsByUser(String tenantId, String userId, Instant startDate, Instant endDate);

    CompletableFuture<List<DashboardAnalytics>> getEventsByType(String tenantId, DashboardAnalytics.AnalyticsEventType eventType, Instant startDate, Instant endDate);

    CompletableFuture<Map<String, Long>> getDashboardViewCounts(String tenantId, Instant startDate, Instant endDate);

    CompletableFuture<Map<String, Long>> getUserActivityCounts(String tenantId, Instant startDate, Instant endDate);

    CompletableFuture<List<DashboardAnalytics>> getSlowLoadingDashboards(String tenantId, long thresholdMs, int limit);

    CompletableFuture<Optional<DashboardUsageReport>> generateUsageReport(
        String tenantId,
        String dashboardId,
        Instant startDate,
        Instant endDate
    );

    CompletableFuture<Map<DashboardAnalytics.AnalyticsEventType, Long>> getEventTypeCounts(String tenantId, String dashboardId, Instant startDate, Instant endDate);
}
