package com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for Alert events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.alert-created:alert-created}")
    private String alertCreatedTopic;

    @Value("${kafka.topics.alert-acknowledged:alert-acknowledged}")
    private String alertAcknowledgedTopic;

    @Value("${kafka.topics.alert-resolved:alert-resolved}")
    private String alertResolvedTopic;

    @Value("${kafka.topics.alert-sent:alert-sent}")
    private String alertSentTopic;

    public void publishAlertCreated(LocationAlert alert) {
        publishEvent(alertCreatedTopic, alert, "alert created");
    }

    public void publishAlertAcknowledged(LocationAlert alert) {
        publishEvent(alertAcknowledgedTopic, alert, "alert acknowledged");
    }

    public void publishAlertResolved(LocationAlert alert) {
        publishEvent(alertResolvedTopic, alert, "alert resolved");
    }

    public void publishAlertSent(LocationAlert alert) {
        publishEvent(alertSentTopic, alert, "alert sent");
    }

    private void publishEvent(String topic, LocationAlert alert, String eventType) {
        try {
            String key = alert.getTenantId() + ":" + alert.getId();
            String payload = objectMapper.writeValueAsString(alert);

            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish {} event: {}", eventType, key, ex);
                        } else {
                            log.debug("Published {} event: {}", eventType, key);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize alert event", e);
        }
    }
}
