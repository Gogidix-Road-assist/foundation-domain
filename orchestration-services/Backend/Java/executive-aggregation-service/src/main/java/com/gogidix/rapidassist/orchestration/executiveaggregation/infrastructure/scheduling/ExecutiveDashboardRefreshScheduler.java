package com.gogidix.rapidassist.orchestration.executiveaggregation.infrastructure.scheduling;

import com.gogidix.rapidassist.orchestration.executiveaggregation.application.service.ExecutiveAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutiveDashboardRefreshScheduler {

    private final ExecutiveAggregationService service;

    @Scheduled(fixedRateString = "${aggregation.cache.ttl-minutes:15}000")
    public void refreshDashboard() {
        log.debug("Scheduled refresh of executive dashboard");
        try {
            service.refreshExecutiveDashboard();
        } catch (Exception e) {
            log.error("Failed to refresh executive dashboard: {}", e.getMessage());
        }
    }
}
