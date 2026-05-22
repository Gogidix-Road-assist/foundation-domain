package com.gogidix.rapidassist.orchestration.countryaggregation.infrastructure.client;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.out.DownstreamServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class WebFluxDownstreamClient implements DownstreamServiceClient {

    private final WebClient webClient;
    private final String foundationGatewayUrl;
    private final String sharedBizGatewayUrl;
    private final String claimGatewayUrl;
    private final String intelligenceBaseUrl;

    @SuppressWarnings("unchecked")
    public WebFluxDownstreamClient(
            WebClient.Builder webClientBuilder,
            @Value("${aggregation.downstream.foundation-gateway-url}") String foundationGatewayUrl,
            @Value("${aggregation.downstream.shared-biz-gateway-url}") String sharedBizGatewayUrl,
            @Value("${aggregation.downstream.claim-gateway-url}") String claimGatewayUrl,
            @Value("${aggregation.downstream.intelligence-base-url}") String intelligenceBaseUrl) {
        this.webClient = webClientBuilder.build();
        this.foundationGatewayUrl = foundationGatewayUrl;
        this.sharedBizGatewayUrl = sharedBizGatewayUrl;
        this.claimGatewayUrl = claimGatewayUrl;
        this.intelligenceBaseUrl = intelligenceBaseUrl;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchFoundationMetrics(String countryCode) {
        return fetchFrom(foundationGatewayUrl + "/api/v1/metrics/country/" + countryCode, "foundation");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchSharedBizMetrics(String countryCode) {
        return fetchFrom(sharedBizGatewayUrl + "/api/v1/metrics/country/" + countryCode, "shared-biz");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchClaimMetrics(String countryCode) {
        return fetchFrom(claimGatewayUrl + "/api/v1/metrics/country/" + countryCode, "claim");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchIntelligenceMetrics(String countryCode) {
        return fetchFrom(intelligenceBaseUrl + "/api/v1/metrics/country/" + countryCode, "intelligence");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkServiceHealth(String serviceUrl) {
        return fetchFrom(serviceUrl + "/actuator/health", "health");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchFrom(String url, String domain) {
        try {
            Map<String, Object> result = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            return result != null ? result : Map.of();
        } catch (Exception e) {
            log.warn("Failed to fetch from {} at {}: {}", domain, url, e.getMessage());
            return Map.of();
        }
    }
}
