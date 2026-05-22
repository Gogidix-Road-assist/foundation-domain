package com.gogidix.rapidassist.orchestration.executiveaggregation.interfaces.rest;

import com.gogidix.rapidassist.orchestration.executiveaggregation.application.service.ExecutiveAggregationService;
import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model.ExecutiveDashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/executive-aggregation")
@RequiredArgsConstructor
public class ExecutiveAggregationController {

    private final ExecutiveAggregationService service;

    @GetMapping("/dashboard")
    public ResponseEntity<ExecutiveDashboard> getDashboard() {
        log.info("REST request to get executive dashboard");
        return ResponseEntity.ok(service.getExecutiveDashboard());
    }

    @PostMapping("/dashboard/refresh")
    public ResponseEntity<ExecutiveDashboard> refreshDashboard() {
        log.info("REST request to refresh executive dashboard");
        return ResponseEntity.ok(service.refreshExecutiveDashboard());
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getGlobalKPIs() {
        return ResponseEntity.ok(service.getGlobalKPIs());
    }

    @GetMapping("/countries/rankings")
    public ResponseEntity<List<ExecutiveDashboard.CountrySummary>> getCountryRankings(
            @RequestParam(defaultValue = "revenue") String sortBy) {
        return ResponseEntity.ok(service.getCountryRankings(sortBy));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<ExecutiveDashboard.Alert>> getActiveAlerts() {
        return ResponseEntity.ok(service.getActiveAlerts());
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueBreakdown() {
        return ResponseEntity.ok(service.getRevenueBreakdown());
    }

    @GetMapping("/operational")
    public ResponseEntity<Map<String, Object>> getOperationalOverview() {
        return ResponseEntity.ok(service.getOperationalOverview());
    }
}
