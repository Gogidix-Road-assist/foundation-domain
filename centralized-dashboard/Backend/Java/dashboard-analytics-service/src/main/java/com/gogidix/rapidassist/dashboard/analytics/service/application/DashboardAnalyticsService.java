package com.gogidix.rapidassist.dashboard.analytics.service.application;

import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardAnalytics;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardUsageReport;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.port.in.AnalyticsCommand;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.port.in.AnalyticsQuery;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.port.out.AnalyticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DashboardAnalyticsService implements AnalyticsCommand, AnalyticsQuery {

    private static final Logger logger = LoggerFactory.getLogger(DashboardAnalyticsService.class);
    private final AnalyticsRepository repository;

    public DashboardAnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    // Command implementations
    @Override
    public CompletableFuture<DashboardAnalytics> recordEvent(
        String tenantId, String dashboardId, String userId,
        DashboardAnalytics.AnalyticsEventType eventType,
        Map<String, Object> eventData,
        DashboardAnalytics.UserSessionInfo sessionInfo,
        DashboardAnalytics.PerformanceMetrics performanceMetrics) {

        logger.info("Recording event: {} for dashboard: {}", eventType, dashboardId);

        DashboardAnalytics analytics = DashboardAnalytics.builder()
            .tenantId(tenantId)
            .dashboardId(dashboardId)
            .userId(userId)
            .eventType(eventType)
            .eventData(eventData)
            .sessionInfo(sessionInfo)
            .performanceMetrics(performanceMetrics)
            .build();

        return repository.save(analytics);
    }

    @Override
    public CompletableFuture<Boolean> batchRecordEvents(List<DashboardAnalytics> events) {
        logger.info("Batch recording {} events", events.size());
        return repository.saveAll(events).thenApply(saved -> true);
    }

    @Override
    public CompletableFuture<Boolean> deleteEventsBefore(String tenantId, Instant timestamp) {
        return repository.deleteByTenantIdAndTimestampBefore(tenantId, timestamp);
    }

    // Query implementations
    @Override
    public CompletableFuture<List<DashboardAnalytics>> getEventsByDashboard(String dashboardId, Instant startDate, Instant endDate) {
        return repository.findByDashboardIdAndDateRange(dashboardId, startDate, endDate);
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> getEventsByUser(String tenantId, String userId, Instant startDate, Instant endDate) {
        return repository.findByTenantIdAndUserIdAndDateRange(tenantId, userId, startDate, endDate);
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> getEventsByType(String tenantId, DashboardAnalytics.AnalyticsEventType eventType, Instant startDate, Instant endDate) {
        return repository.findByTenantIdAndEventTypeAndDateRange(tenantId, eventType, startDate, endDate);
    }

    @Override
    public CompletableFuture<Map<String, Long>> getDashboardViewCounts(String tenantId, Instant startDate, Instant endDate) {
        return repository.countViewsByDashboard(tenantId, startDate, endDate);
    }

    @Override
    public CompletableFuture<Map<String, Long>> getUserActivityCounts(String tenantId, Instant startDate, Instant endDate) {
        return repository.countByUserId(tenantId, startDate, endDate);
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> getSlowLoadingDashboards(String tenantId, long thresholdMs, int limit) {
        return repository.findSlowLoadingEvents(tenantId, thresholdMs, limit);
    }

    @Override
    public CompletableFuture<Optional<DashboardUsageReport>> generateUsageReport(
        String tenantId, String dashboardId, Instant startDate, Instant endDate) {

        return repository.findByDashboardIdAndDateRange(dashboardId, startDate, endDate)
            .thenApplyAsync(events -> {
                if (events.isEmpty()) {
                    return Optional.empty();
                }

                Map<String, Long> viewsByUser = events.stream()
                    .filter(e -> e.eventType() == DashboardAnalytics.AnalyticsEventType.DASHBOARD_VIEW)
                    .collect(Collectors.groupingBy(DashboardAnalytics::userId, Collectors.counting()));

                Map<String, Long> viewsByDay = events.stream()
                    .filter(e -> e.eventType() == DashboardAnalytics.AnalyticsEventType.DASHBOARD_VIEW)
                    .collect(Collectors.groupingBy(e ->
                        LocalDate.ofInstant(e.timestamp(), ZoneId.systemDefault()).toString(),
                        Collectors.counting()
                    ));

                long totalViews = viewsByUser.values().stream().mapToLong(Long::longValue).sum();
                long uniqueUsers = viewsByUser.size();
                long totalEdits = events.stream()
                    .filter(e -> e.eventType() == DashboardAnalytics.AnalyticsEventType.DASHBOARD_EDIT).count();

                Map<String, Integer> widgetInteractions = new HashMap<>();
                events.stream()
                    .filter(e -> e.eventType() == DashboardAnalytics.AnalyticsEventType.WIDGET_CLICK)
                    .forEach(e -> {
                        String widgetId = (String) e.eventData().get("widgetId");
                        if (widgetId != null) {
                            widgetInteractions.merge(widgetId, 1, Integer::sum);
                        }
                    });

                List<String> topWidgets = widgetInteractions.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

                double avgLoadTime = events.stream()
                    .filter(e -> e.performanceMetrics() != null)
                    .mapToLong(e -> e.performanceMetrics().loadTimeMs())
                    .average()
                    .orElse(0.0);

                DashboardUsageReport report = DashboardUsageReport.builder()
                    .tenantId(tenantId)
                    .dashboardId(dashboardId)
                    .period(new DashboardUsageReport.ReportingPeriod(startDate, endDate, "CUSTOM"))
                    .usageStats(new DashboardUsageReport.UsageStatistics(
                        totalViews,
                        uniqueUsers,
                        totalEdits,
                        0L,  // shares
                        0L,  // exports
                        0L,  // avg session duration
                        viewsByUser,
                        viewsByDay
                    ))
                    .engagementMetrics(new DashboardUsageReport.UserEngagementMetrics(
                        0.0,  // avg time
                        0.0,  // bounce rate
                        topWidgets.size(),
                        topWidgets,
                        widgetInteractions
                    ))
                    .performanceReport(new DashboardUsageReport.PerformanceReport(
                        avgLoadTime,
                        0.0,  // avg render time
                        0.0,  // avg data fetch time
                        events.stream().filter(DashboardAnalytics::isPerformanceCritical).count(),
                        0L,  // error count
                        Map.of()
                    ))
                    .build();

                return Optional.of(report);
            });
    }

    @Override
    public CompletableFuture<Map<DashboardAnalytics.AnalyticsEventType, Long>> getEventTypeCounts(String tenantId, String dashboardId, Instant startDate, Instant endDate) {
        return repository.findByDashboardIdAndDateRange(dashboardId, startDate, endDate)
            .thenApplyAsync(events -> events.stream()
                .collect(Collectors.groupingBy(
                    DashboardAnalytics::eventType,
                    Collectors.counting()
                )));
    }
}
