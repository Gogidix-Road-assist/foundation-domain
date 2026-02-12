package com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for Geofence events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeofenceEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.geofence-created:geofence-created}")
    private String geofenceCreatedTopic;

    @Value("${kafka.topics.geofence-updated:geofence-updated}")
    private String geofenceUpdatedTopic;

    @Value("${kafka.topics.geofence-deleted:geofence-deleted}")
    private String geofenceDeletedTopic;

    @Value("${kafka.topics.alert-created:alert-created}")
    private String alertCreatedTopic;

    public void publishGeofenceCreated(Geofence geofence) {
        publishEvent(geofenceCreatedTopic, geofence, "geofence created");
    }

    public void publishGeofenceUpdated(Geofence geofence) {
        publishEvent(geofenceUpdatedTopic, geofence, "geofence updated");
    }

    public void publishGeofenceDeleted(Geofence geofence) {
        publishEvent(geofenceDeletedTopic, geofence, "geofence deleted");
    }

    public void publishAlertCreated(LocationAlert alert) {
        publishAlertEvent(alertCreatedTopic, alert, "alert created");
    }

    private void publishEvent(String topic, Geofence geofence, String eventType) {
        try {
            String key = geofence.getTenantId() + ":" + geofence.getId();
            String payload = objectMapper.writeValueAsString(geofence);

            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish {} event: {}", eventType, key, ex);
                        } else {
                            log.debug("Published {} event: {}", eventType, key);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize geofence event", e);
        }
    }

    private void publishAlertEvent(String topic, LocationAlert alert, String eventType) {
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
