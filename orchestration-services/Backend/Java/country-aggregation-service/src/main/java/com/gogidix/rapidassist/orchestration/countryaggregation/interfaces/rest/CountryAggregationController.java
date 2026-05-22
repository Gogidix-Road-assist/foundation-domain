package com.gogidix.rapidassist.orchestration.countryaggregation.interfaces.rest;

import com.gogidix.rapidassist.orchestration.countryaggregation.application.service.CountryAggregationService;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.CountryDashboard;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.DomainHealthSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/country-aggregation")
@RequiredArgsConstructor
public class CountryAggregationController {

    private final CountryAggregationService aggregationService;

    @GetMapping("/countries/{countryCode}/dashboard")
    public ResponseEntity<CountryDashboard> getCountryDashboard(
            @PathVariable String countryCode) {
        log.info("REST request to get dashboard for country: {}", countryCode);
        CountryDashboard dashboard = aggregationService.getCountryDashboard(countryCode);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDashboard>> getAllCountryDashboards() {
        log.info("REST request to get all country dashboards");
        List<CountryDashboard> dashboards = aggregationService.getAllCountryDashboards();
        return ResponseEntity.ok(dashboards);
    }

    @PostMapping("/countries/{countryCode}/refresh")
    public ResponseEntity<CountryDashboard> refreshCountryDashboard(
            @PathVariable String countryCode) {
        log.info("REST request to refresh dashboard for country: {}", countryCode);
        CountryDashboard dashboard = aggregationService.refreshCountryDashboard(countryCode);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/countries/{countryCode}/health")
    public ResponseEntity<List<DomainHealthSnapshot>> getCountryDomainHealth(
            @PathVariable String countryCode) {
        log.info("REST request to get domain health for country: {}", countryCode);
        List<DomainHealthSnapshot> health = aggregationService.getCountryDomainHealth(countryCode);
        return ResponseEntity.ok(health);
    }

    @GetMapping("/countries/{countryCode}/health/{domainName}")
    public ResponseEntity<DomainHealthSnapshot> getDomainHealth(
            @PathVariable String countryCode,
            @PathVariable String domainName) {
        log.info("REST request to get {} health for country: {}", domainName, countryCode);
        DomainHealthSnapshot snapshot = aggregationService.getDomainHealth(countryCode, domainName);
        return ResponseEntity.ok(snapshot);
    }

    @GetMapping("/supported-countries")
    public ResponseEntity<List<String>> getSupportedCountries() {
        return ResponseEntity.ok(aggregationService.getSupportedCountries());
    }
}
