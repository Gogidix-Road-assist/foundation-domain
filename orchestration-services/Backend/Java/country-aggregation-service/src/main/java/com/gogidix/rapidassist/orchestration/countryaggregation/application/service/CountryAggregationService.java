package com.gogidix.rapidassist.orchestration.countryaggregation.application.service;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.CountryDashboard;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.DomainHealthSnapshot;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.in.CountryAggregationUseCase;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.out.CountryDashboardRepository;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.out.DownstreamServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryAggregationService implements CountryAggregationUseCase {

    private final CountryDashboardRepository repository;
    private final DownstreamServiceClient downstreamClient;

    private static final List<String> SUPPORTED_COUNTRIES = List.of(
            "DE", "FR", "ES", "IT", "NL", "BE", "AT", "CH", "PL", "PT", "GB", "US", "CA", "AU"
    );

    private static final Map<String, String> COUNTRY_NAMES = Map.ofEntries(
            Map.entry("DE", "Germany"), Map.entry("FR", "France"),
            Map.entry("ES", "Spain"), Map.entry("IT", "Italy"),
            Map.entry("NL", "Netherlands"), Map.entry("BE", "Belgium"),
            Map.entry("AT", "Austria"), Map.entry("CH", "Switzerland"),
            Map.entry("PL", "Poland"), Map.entry("PT", "Portugal"),
            Map.entry("GB", "United Kingdom"), Map.entry("US", "United States"),
            Map.entry("CA", "Canada"), Map.entry("AU", "Australia")
    );

    @Override
    @Cacheable(value = "country-dashboards", key = "#countryCode")
    public CountryDashboard getCountryDashboard(String countryCode) {
        log.info("Getting dashboard for country: {}", countryCode);
        return repository.findByCountryCode(countryCode.toUpperCase())
                .orElseGet(() -> refreshCountryDashboard(countryCode));
    }

    @Override
    public List<CountryDashboard> getAllCountryDashboards() {
        log.info("Getting all country dashboards");
        List<CountryDashboard> cached = repository.findAll();
        if (!cached.isEmpty()) {
            return cached;
        }
        List<CountryDashboard> result = new ArrayList<>();
        for (String code : SUPPORTED_COUNTRIES) {
            result.add(refreshCountryDashboard(code));
        }
        return result;
    }

    @Override
    @CacheEvict(value = "country-dashboards", key = "#countryCode")
    public CountryDashboard refreshCountryDashboard(String countryCode) {
        log.info("Refreshing dashboard for country: {}", countryCode);
        String code = countryCode.toUpperCase();

        Map<String, Object> foundationMetrics = safeFetch(() ->
                downstreamClient.fetchFoundationMetrics(code), "foundation", code);
        Map<String, Object> sharedBizMetrics = safeFetch(() ->
                downstreamClient.fetchSharedBizMetrics(code), "shared-biz", code);
        Map<String, Object> claimMetrics = safeFetch(() ->
                downstreamClient.fetchClaimMetrics(code), "claim", code);
        Map<String, Object> intelMetrics = safeFetch(() ->
                downstreamClient.fetchIntelligenceMetrics(code), "intelligence", code);

        CountryDashboard dashboard = CountryDashboard.builder()
                .countryCode(code)
                .countryName(COUNTRY_NAMES.getOrDefault(code, code))
                .operationalMetrics(buildOperationalMetrics(foundationMetrics, intelMetrics))
                .financialMetrics(buildFinancialMetrics(sharedBizMetrics, claimMetrics))
                .serviceMetrics(buildServiceMetrics(foundationMetrics, claimMetrics))
                .build();

        return repository.save(dashboard);
    }

    @Override
    public List<DomainHealthSnapshot> getCountryDomainHealth(String countryCode) {
        return repository.findHealthByCountryCode(countryCode.toUpperCase());
    }

    @Override
    public DomainHealthSnapshot getDomainHealth(String countryCode, String domainName) {
        return repository.findHealthByCountryAndDomain(countryCode.toUpperCase(), domainName)
                .orElseThrow(() -> new RuntimeException(
                        "No health snapshot for " + domainName + " in " + countryCode));
    }

    @Override
    public List<String> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }

    private Map<String, Object> safeFetch(Supplier<Map<String, Object>> fetch, String domain, String country) {
        try {
            Map<String, Object> result = fetch.get();
            return result != null ? result : Map.of();
        } catch (Exception e) {
            log.warn("Failed to fetch {} metrics for {}: {}", domain, country, e.getMessage());
            return Map.of();
        }
    }

    @SuppressWarnings("unchecked")
    private CountryDashboard.OperationalMetrics buildOperationalMetrics(
            Map<String, Object> foundation, Map<String, Object> intel) {
        return CountryDashboard.OperationalMetrics.builder()
                .activeProviders(getInt(foundation, "activeProviders"))
                .activeIncidents(getInt(intel, "activeIncidents"))
                .pendingClaims(getInt(foundation, "pendingClaims"))
                .avgResponseTimeMinutes(getDouble(foundation, "avgResponseTimeMinutes"))
                .activeEmergencies(getInt(intel, "activeEmergencies"))
                .crossBorderOperations(getInt(intel, "crossBorderOperations"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private CountryDashboard.FinancialMetrics buildFinancialMetrics(
            Map<String, Object> sharedBiz, Map<String, Object> claim) {
        return CountryDashboard.FinancialMetrics.builder()
                .dailyRevenue(getDouble(sharedBiz, "dailyRevenue"))
                .monthlyRevenue(getDouble(sharedBiz, "monthlyRevenue"))
                .totalPayouts(getDouble(claim, "totalPayouts"))
                .activeSubscriptions(getInt(sharedBiz, "activeSubscriptions"))
                .avgClaimValue(getDouble(claim, "avgClaimValue"))
                .currency(getString(sharedBiz, "currency"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private CountryDashboard.ServiceMetrics buildServiceMetrics(
            Map<String, Object> foundation, Map<String, Object> claim) {
        return CountryDashboard.ServiceMetrics.builder()
                .totalServicesToday(getInt(foundation, "totalServicesToday"))
                .completedToday(getInt(foundation, "completedToday"))
                .cancelledToday(getInt(foundation, "cancelledToday"))
                .customerSatisfactionScore(getDouble(claim, "customerSatisfactionScore"))
                .slaCompliancePercent(getDouble(foundation, "slaCompliancePercent"))
                .firstResponseRate(getDouble(foundation, "firstResponseRate"))
                .build();
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number n) return n.intValue();
        return 0;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
