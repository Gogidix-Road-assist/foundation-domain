package com.gogidix.rapidassist.dashboard.analytics.service.domain.port.out;

import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardAnalytics;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsRepository {

    CompletableFuture<DashboardAnalytics> save(DashboardAnalytics analytics);

    CompletableFuture<List<DashboardAnalytics>> saveAll(List<DashboardAnalytics> analytics);

    CompletableFuture<List<DashboardAnalytics>> findByDashboardIdAndDateRange(String dashboardId, Instant startDate, Instant endDate);

    CompletableFuture<List<DashboardAnalytics>> findByTenantIdAndUserIdAndDateRange(String tenantId, String userId, Instant startDate, Instant endDate);

    CompletableFuture<List<DashboardAnalytics>> findByTenantIdAndEventTypeAndDateRange(String tenantId, DashboardAnalytics.AnalyticsEventType eventType, Instant startDate, Instant endDate);

    CompletableFuture<Map<String, Long>> countViewsByDashboard(String tenantId, Instant startDate, Instant endDate);

    CompletableFuture<Map<String, Long>> countByUserId(String tenantId, Instant startDate, Instant endDate);

    CompletableFuture<List<DashboardAnalytics>> findSlowLoadingEvents(String tenantId, long thresholdMs, int limit);

    CompletableFuture<Boolean> deleteByTenantIdAndTimestampBefore(String tenantId, Instant timestamp);
}
