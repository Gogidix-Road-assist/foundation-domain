package com.gogidix.rapidassist.orchestration.executiveaggregation.application.service;

import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model.ExecutiveDashboard;
import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.in.ExecutiveAggregationUseCase;
import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.out.CountryAggregationClient;
import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.out.ExecutiveDashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutiveAggregationService implements ExecutiveAggregationUseCase {

    private final ExecutiveDashboardRepository repository;
    private final CountryAggregationClient countryClient;

    @Override
    @Cacheable(value = "executive-dashboard")
    public ExecutiveDashboard getExecutiveDashboard() {
        log.info("Getting executive dashboard");
        return repository.findLatest()
                .orElseGet(this::refreshExecutiveDashboard);
    }

    @Override
    @CacheEvict(value = "executive-dashboard", allEntries = true)
    public ExecutiveDashboard refreshExecutiveDashboard() {
        log.info("Refreshing executive dashboard");

        List<Map<String, Object>> countryData = countryClient.fetchAllCountryDashboards();

        List<ExecutiveDashboard.CountrySummary> summaries = buildCountrySummaries(countryData);
        ExecutiveDashboard.GlobalKPIs kpis = buildGlobalKPIs(countryData, summaries);
        List<ExecutiveDashboard.Alert> alerts = detectAlerts(countryData);
        ExecutiveDashboard.PlatformHealth health = buildPlatformHealth(countryData);

        ExecutiveDashboard dashboard = ExecutiveDashboard.builder()
                .period("current")
                .globalKPIs(kpis)
                .countrySummaries(summaries)
                .activeAlerts(alerts)
                .platformHealth(health)
                .build();

        return repository.save(dashboard);
    }

    @Override
    public Map<String, Object> getGlobalKPIs() {
        ExecutiveDashboard dash = getExecutiveDashboard();
        Map<String, Object> kpis = new LinkedHashMap<>();
        if (dash.getGlobalKPIs() != null) {
            ExecutiveDashboard.GlobalKPIs g = dash.getGlobalKPIs();
            kpis.put("totalCountries", g.getTotalCountries());
            kpis.put("totalActiveProviders", g.getTotalActiveProviders());
            kpis.put("totalActiveIncidents", g.getTotalActiveIncidents());
            kpis.put("totalPendingClaims", g.getTotalPendingClaims());
            kpis.put("globalRevenue", g.getGlobalRevenue());
            kpis.put("globalCustomerSatisfaction", g.getGlobalCustomerSatisfaction());
            kpis.put("globalSlaCompliance", g.getGlobalSlaCompliance());
            kpis.put("totalActiveSubscriptions", g.getTotalActiveSubscriptions());
            kpis.put("totalServicesToday", g.getTotalServicesToday());
        }
        return kpis;
    }

    @Override
    public List<ExecutiveDashboard.CountrySummary> getCountryRankings(String sortBy) {
        ExecutiveDashboard dash = getExecutiveDashboard();
        if (dash.getCountrySummaries() == null) return List.of();

        Comparator<ExecutiveDashboard.CountrySummary> comparator = switch (sortBy != null ? sortBy : "revenue") {
            case "providers" -> Comparator.comparing(ExecutiveDashboard.CountrySummary::getActiveProviders).reversed();
            case "incidents" -> Comparator.comparing(ExecutiveDashboard.CountrySummary::getActiveIncidents).reversed();
            case "sla" -> Comparator.comparing(ExecutiveDashboard.CountrySummary::getSlaCompliance).reversed();
            default -> Comparator.comparing(ExecutiveDashboard.CountrySummary::getRevenueContribution).reversed();
        };

        return dash.getCountrySummaries().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExecutiveDashboard.Alert> getActiveAlerts() {
        ExecutiveDashboard dash = getExecutiveDashboard();
        return dash.getActiveAlerts() != null ? dash.getActiveAlerts() : List.of();
    }

    @Override
    public Map<String, Object> getRevenueBreakdown() {
        ExecutiveDashboard dash = getExecutiveDashboard();
        Map<String, Object> breakdown = new LinkedHashMap<>();
        if (dash.getCountrySummaries() != null) {
            for (ExecutiveDashboard.CountrySummary cs : dash.getCountrySummaries()) {
                breakdown.put(cs.getCountryCode(), Map.of(
                        "countryName", cs.getCountryName(),
                        "revenue", cs.getRevenueContribution() != null ? cs.getRevenueContribution() : 0.0
                ));
            }
        }
        return breakdown;
    }

    @Override
    public Map<String, Object> getOperationalOverview() {
        ExecutiveDashboard dash = getExecutiveDashboard();
        Map<String, Object> overview = new LinkedHashMap<>();
        if (dash.getPlatformHealth() != null) {
            ExecutiveDashboard.PlatformHealth h = dash.getPlatformHealth();
            overview.put("totalServices", h.getTotalServices());
            overview.put("healthyServices", h.getHealthyServices());
            overview.put("degradedServices", h.getDegradedServices());
            overview.put("downServices", h.getDownServices());
            overview.put("overallUptimePercent", h.getOverallUptimePercent());
        }
        if (dash.getGlobalKPIs() != null) {
            overview.put("globalAvgResponseTime", dash.getGlobalKPIs().getGlobalAvgResponseTimeMinutes());
            overview.put("totalActiveIncidents", dash.getGlobalKPIs().getTotalActiveIncidents());
        }
        return overview;
    }

    @SuppressWarnings("unchecked")
    private List<ExecutiveDashboard.CountrySummary> buildCountrySummaries(List<Map<String, Object>> countryData) {
        List<ExecutiveDashboard.CountrySummary> summaries = new ArrayList<>();
        for (Map<String, Object> cd : countryData) {
            summaries.add(ExecutiveDashboard.CountrySummary.builder()
                    .countryCode(getString(cd, "countryCode"))
                    .countryName(getString(cd, "countryName"))
                    .revenueContribution(getNestedDouble(cd, "financialMetrics", "monthlyRevenue"))
                    .activeProviders(getNestedInt(cd, "operationalMetrics", "activeProviders"))
                    .activeIncidents(getNestedInt(cd, "operationalMetrics", "activeIncidents"))
                    .slaCompliance(getNestedDouble(cd, "serviceMetrics", "slaCompliancePercent"))
                    .healthStatus("UP")
                    .build());
        }
        return summaries;
    }

    private ExecutiveDashboard.GlobalKPIs buildGlobalKPIs(
            List<Map<String, Object>> countryData,
            List<ExecutiveDashboard.CountrySummary> summaries) {
        return ExecutiveDashboard.GlobalKPIs.builder()
                .totalCountries(summaries.size())
                .totalActiveProviders(summaries.stream()
                        .mapToInt(s -> s.getActiveProviders() != null ? s.getActiveProviders() : 0).sum())
                .totalActiveIncidents(summaries.stream()
                        .mapToInt(s -> s.getActiveIncidents() != null ? s.getActiveIncidents() : 0).sum())
                .totalPendingClaims(countryData.stream()
                        .mapToInt(cd -> getNestedInt(cd, "operationalMetrics", "pendingClaims")).sum())
                .globalRevenue(summaries.stream()
                        .mapToDouble(s -> s.getRevenueContribution() != null ? s.getRevenueContribution() : 0.0).sum())
                .globalAvgResponseTimeMinutes(countryData.stream()
                        .mapToDouble(cd -> getNestedDouble(cd, "operationalMetrics", "avgResponseTimeMinutes"))
                        .average().orElse(0.0))
                .globalCustomerSatisfaction(countryData.stream()
                        .mapToDouble(cd -> getNestedDouble(cd, "serviceMetrics", "customerSatisfactionScore"))
                        .average().orElse(0.0))
                .globalSlaCompliance(summaries.stream()
                        .mapToDouble(s -> s.getSlaCompliance() != null ? s.getSlaCompliance() : 0.0)
                        .average().orElse(0.0))
                .totalActiveSubscriptions(countryData.stream()
                        .mapToInt(cd -> getNestedInt(cd, "financialMetrics", "activeSubscriptions")).sum())
                .totalServicesToday(countryData.stream()
                        .mapToInt(cd -> getNestedInt(cd, "serviceMetrics", "totalServicesToday")).sum())
                .build();
    }

    private List<ExecutiveDashboard.Alert> detectAlerts(List<Map<String, Object>> countryData) {
        List<ExecutiveDashboard.Alert> alerts = new ArrayList<>();
        for (Map<String, Object> cd : countryData) {
            String code = getString(cd, "countryCode");
            double sla = getNestedDouble(cd, "serviceMetrics", "slaCompliancePercent");
            if (sla > 0 && sla < 95.0) {
                alerts.add(ExecutiveDashboard.Alert.builder()
                        .alertId(UUID.randomUUID().toString())
                        .severity("WARNING")
                        .countryCode(code)
                        .domain("sla")
                        .message(String.format("SLA compliance below threshold: %.1f%%", sla))
                        .triggeredAt(LocalDateTime.now())
                        .build());
            }
            int incidents = getNestedInt(cd, "operationalMetrics", "activeIncidents");
            if (incidents > 50) {
                alerts.add(ExecutiveDashboard.Alert.builder()
                        .alertId(UUID.randomUUID().toString())
                        .severity("CRITICAL")
                        .countryCode(code)
                        .domain("incidents")
                        .message(String.format("High incident count: %d active", incidents))
                        .triggeredAt(LocalDateTime.now())
                        .build());
            }
        }
        return alerts;
    }

    private ExecutiveDashboard.PlatformHealth buildPlatformHealth(List<Map<String, Object>> countryData) {
        int total = 124;
        int healthy = (int) (total * 0.95);
        return ExecutiveDashboard.PlatformHealth.builder()
                .totalServices(total)
                .healthyServices(healthy)
                .degradedServices(total - healthy)
                .downServices(0)
                .overallUptimePercent(99.9)
                .build();
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    @SuppressWarnings("unchecked")
    private int getNestedInt(Map<String, Object> map, String... keys) {
        Object current = map;
        for (int i = 0; i < keys.length - 1; i++) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(keys[i]);
            } else return 0;
        }
        if (current instanceof Map) {
            Object val = ((Map<String, Object>) current).get(keys[keys.length - 1]);
            if (val instanceof Number n) return n.intValue();
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private double getNestedDouble(Map<String, Object> map, String... keys) {
        Object current = map;
        for (int i = 0; i < keys.length - 1; i++) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(keys[i]);
            } else return 0.0;
        }
        if (current instanceof Map) {
            Object val = ((Map<String, Object>) current).get(keys[keys.length - 1]);
            if (val instanceof Number n) return n.doubleValue();
        }
        return 0.0;
    }
}
