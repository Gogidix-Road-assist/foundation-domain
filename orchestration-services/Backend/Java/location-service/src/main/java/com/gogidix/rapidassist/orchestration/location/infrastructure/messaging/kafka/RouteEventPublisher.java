package com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.location.domain.model.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for Route events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.route-calculated:route-calculated}")
    private String routeCalculatedTopic;

    public void publishRouteCalculated(Route route) {
        try {
            String key = route.getTenantId() + ":" + route.getReferenceId();
            String payload = objectMapper.writeValueAsString(route);

            kafkaTemplate.send(routeCalculatedTopic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish route calculated event: {}", key, ex);
                        } else {
                            log.debug("Published route calculated event: {}", key);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize route event", e);
        }
    }
}
