package com.gogidix.rapidassist.orchestration.matching.infrastructure.messaging;

import java.time.LocalDateTime;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for matching-related events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String MATCHING_COMPLETED_TOPIC = "matching.completed";
    private static final String PROVIDER_ASSIGNED_TOPIC = "provider.assigned";
    private static final String NO_PROVIDERS_TOPIC = "matching.no-providers";

    /**
     * Publish matching completed event
     */
    public void publishMatchingCompleted(MatchingResult result) {
        try {
            MatchingCompletedEvent event = MatchingCompletedEvent.builder()
                .requestId(result.getRequestId())
                .incidentId(result.getIncidentId())
                .tenantId(result.getTenantId())
                .algorithm(result.getAlgorithm().toString())
                .providerCount(result.getTotalProviders())
                .topProviderId(result.getTopProvider() != null ? result.getTopProvider().getProviderId() : null)
                .status(result.getStatus().toString())
                .timestamp(java.time.LocalDateTime.now())
                .build();

            CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(MATCHING_COMPLETED_TOPIC, result.getRequestId(), event);

            future.whenComplete((result1, ex) -> {
                if (ex == null) {
                    log.info("Published matching completed event for request: {}", result.getRequestId());
                } else {
                    log.error("Failed to publish matching completed event", ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing matching completed event", e);
        }
    }

    /**
     * Publish provider assigned event
     */
    public void publishProviderAssigned(MatchingResult result) {
        if (result.getTopProvider() == null) {
            return;
        }

        try {
            ProviderAssignedEvent event = ProviderAssignedEvent.builder()
                .requestId(result.getRequestId())
                .incidentId(result.getIncidentId())
                .tenantId(result.getTenantId())
                .providerId(result.getTopProvider().getProviderId())
                .providerName(result.getTopProvider().getProviderName())
                .score(result.getTopProvider().getScore())
                .estimatedCost(result.getTopProvider().getEstimatedCost())
                .estimatedArrival(result.getTopProvider().getEstimatedArrival())
                .timestamp(java.time.LocalDateTime.now())
                .build();

            kafkaTemplate.send(PROVIDER_ASSIGNED_TOPIC, result.getRequestId(), event);
            log.info("Published provider assigned event for request: {}", result.getRequestId());

        } catch (Exception e) {
            log.error("Error publishing provider assigned event", e);
        }
    }

    /**
     * Publish no providers found event
     */
    public void publishNoProvidersFound(MatchingResult result) {
        try {
            NoProvidersEvent event = NoProvidersEvent.builder()
                .requestId(result.getRequestId())
                .incidentId(result.getIncidentId())
                .tenantId(result.getTenantId())
                .reason(result.getStatusMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();

            kafkaTemplate.send(NO_PROVIDERS_TOPIC, result.getRequestId(), event);
            log.info("Published no providers event for request: {}", result.getRequestId());

        } catch (Exception e) {
            log.error("Error publishing no providers event", e);
        }
    }

    @lombok.Data
    @lombok.Builder
    private static class MatchingCompletedEvent {
        private String requestId;
        private String incidentId;
        private String tenantId;
        private String algorithm;
        private Integer providerCount;
        private String topProviderId;
        private String status;
        private java.time.LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    private static class ProviderAssignedEvent {
        private String requestId;
        private String incidentId;
        private String tenantId;
        private String providerId;
        private String providerName;
        private Double score;
        private Double estimatedCost;
        private java.time.LocalDateTime estimatedArrival;
        private java.time.LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    private static class NoProvidersEvent {
        private String requestId;
        private String incidentId;
        private String tenantId;
        private String reason;
        private java.time.LocalDateTime timestamp;
    }
}
