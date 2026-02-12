package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.adapter.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * Generic REST client for calling external services
 */
@Slf4j
@Component
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    @Value("${external.services.base-url:http://localhost:8080}")
    private String baseUrl;

    public ExternalServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public <T> Optional<T> get(String service, String path, Class<T> responseType) {
        try {
            String url = baseUrl + "/" + service + path;
            log.debug("Calling external service: {}", url);

            T response = restTemplate.getForObject(url, responseType);
            return Optional.ofNullable(response);

        } catch (Exception e) {
            log.error("Error calling external service: {}/{}", service, path, e);
            return Optional.empty();
        }
    }

    public <T> Optional<T> post(String service, String path, Object body, Class<T> responseType) {
        try {
            String url = baseUrl + "/" + service + path;
            log.debug("Posting to external service: {}", url);

            T response = restTemplate.postForObject(url, body, responseType);
            return Optional.ofNullable(response);

        } catch (Exception e) {
            log.error("Error posting to external service: {}/{}", service, path, e);
            return Optional.empty();
        }
    }

    public Map<String, Object> getHealthStatus(String service) {
        return get(service, "/actuator/health", Map.class).orElse(Map.of("status", "DOWN"));
    }
}
