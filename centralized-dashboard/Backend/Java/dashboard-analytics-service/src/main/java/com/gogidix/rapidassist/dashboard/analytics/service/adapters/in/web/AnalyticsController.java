package com.gogidix.rapidassist.dashboard.analytics.service.adapters.in.web;

import com.gogidix.rapidassist.dashboard.analytics.service.application.DashboardAnalyticsService;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardAnalytics;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardUsageReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final DashboardAnalyticsService analyticsService;

    public AnalyticsController(DashboardAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "dashboard-analytics-service"
        ));
    }

    @PostMapping("/events")
    public ResponseEntity<DashboardAnalytics> recordEvent(@RequestBody RecordEventRequest request) {
        DashboardAnalytics event = analyticsService.recordEvent(
            request.tenantId(),
            request.dashboardId(),
            request.userId(),
            request.eventType(),
            request.eventData(),
            request.sessionInfo(),
            request.performanceMetrics()
        ).join();

        return ResponseEntity.created(URI.create("/api/analytics/events/" + event.id())).body(event);
    }

    @PostMapping("/events/batch")
    public ResponseEntity<Map<String, String>> batchRecordEvents(@RequestBody BatchEventsRequest request) {
        analyticsService.batchRecordEvents(request.events()).join();
        return ResponseEntity.ok(Map.of("status", "recorded", "count", String.valueOf(request.events().size())));
    }

    @GetMapping("/events/dashboard/{dashboardId}")
    public ResponseEntity<List<DashboardAnalytics>> getEventsByDashboard(
        @PathVariable String dashboardId,
        @RequestParam long startDate,
        @RequestParam long endDate) {

        List<DashboardAnalytics> events = analyticsService.getEventsByDashboard(
            dashboardId,
            Instant.ofEpochMilli(startDate),
            Instant.ofEpochMilli(endDate)
        ).join();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/user/{userId}")
    public ResponseEntity<List<DashboardAnalytics>> getEventsByUser(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestParam long startDate,
        @RequestParam long endDate) {

        List<DashboardAnalytics> events = analyticsService.getEventsByUser(
            tenantId,
            userId,
            Instant.ofEpochMilli(startDate),
            Instant.ofEpochMilli(endDate)
        ).join();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/views")
    public ResponseEntity<Map<String, Long>> getDashboardViewCounts(
        @RequestParam String tenantId,
        @RequestParam long startDate,
        @RequestParam long endDate) {

        Map<String, Long> counts = analyticsService.getDashboardViewCounts(
            tenantId,
            Instant.ofEpochMilli(startDate),
            Instant.ofEpochMilli(endDate)
        ).join();

        return ResponseEntity.ok(counts);
    }

    @GetMapping("/slow-loading")
    public ResponseEntity<List<DashboardAnalytics>> getSlowLoadingDashboards(
        @RequestParam String tenantId,
        @RequestParam(defaultValue = "3000") long thresholdMs,
        @RequestParam(defaultValue = "50") int limit) {

        List<DashboardAnalytics> events = analyticsService.getSlowLoadingDashboards(
            tenantId,
            thresholdMs,
            limit
        ).join();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/reports/usage")
    public ResponseEntity<DashboardUsageReport> generateUsageReport(
        @RequestParam String tenantId,
        @RequestParam String dashboardId,
        @RequestParam long startDate,
        @RequestParam long endDate) {

        return analyticsService.generateUsageReport(
            tenantId,
            dashboardId,
            Instant.ofEpochMilli(startDate),
            Instant.ofEpochMilli(endDate)
        ).join()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/events/before")
    public ResponseEntity<Map<String, String>> deleteEventsBefore(
        @RequestParam String tenantId,
        @RequestParam long timestamp) {

        boolean success = analyticsService.deleteEventsBefore(
            tenantId,
            Instant.ofEpochMilli(timestamp)
        ).join();

        return ResponseEntity.ok(Map.of("status", success ? "deleted" : "failed"));
    }

    // Request records
    record RecordEventRequest(
        String tenantId,
        String dashboardId,
        String userId,
        DashboardAnalytics.AnalyticsEventType eventType,
        Map<String, Object> eventData,
        DashboardAnalytics.UserSessionInfo sessionInfo,
        DashboardAnalytics.PerformanceMetrics performanceMetrics
    ) {}

    record BatchEventsRequest(List<DashboardAnalytics> events) {}
}
