package com.gogidix.rapidassist.orchestration.executiveaggregation.infrastructure.client;

import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.out.CountryAggregationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WebClientCountryAggregationAdapter implements CountryAggregationClient {

    private final WebClient webClient;
    private final String countryAggregationUrl;

    public WebClientCountryAggregationAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${aggregation.downstream.country-aggregation-url}") String countryAggregationUrl) {
        this.webClient = webClientBuilder.build();
        this.countryAggregationUrl = countryAggregationUrl;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAllCountryDashboards() {
        try {
            List<Map<String, Object>> result = webClient.get()
                    .uri(countryAggregationUrl + "/api/v1/country-aggregation/countries")
                    .retrieve()
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            log.warn("Failed to fetch country dashboards: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchCountryDashboard(String countryCode) {
        try {
            Map<String, Object> result = webClient.get()
                    .uri(countryAggregationUrl + "/api/v1/country-aggregation/countries/{code}/dashboard", countryCode)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            return result != null ? result : Map.of();
        } catch (Exception e) {
            log.warn("Failed to fetch dashboard for {}: {}", countryCode, e.getMessage());
            return Map.of();
        }
    }
}
