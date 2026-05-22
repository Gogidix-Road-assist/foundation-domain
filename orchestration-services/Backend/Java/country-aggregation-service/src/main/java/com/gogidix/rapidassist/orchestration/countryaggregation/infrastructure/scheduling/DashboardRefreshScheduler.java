package com.gogidix.rapidassist.orchestration.countryaggregation.infrastructure.scheduling;

import com.gogidix.rapidassist.orchestration.countryaggregation.application.service.CountryAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardRefreshScheduler {

    private final CountryAggregationService aggregationService;

    @Scheduled(fixedRateString = "${aggregation.cache.ttl-minutes:5}000")
    public void refreshAllDashboards() {
        log.debug("Scheduled refresh of all country dashboards");
        try {
            aggregationService.getAllCountryDashboards();
        } catch (Exception e) {
            log.error("Failed to refresh country dashboards: {}", e.getMessage());
        }
    }
}
