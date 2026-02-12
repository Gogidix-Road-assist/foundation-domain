package com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kafka event publisher for Location events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.location-updated:location-updated}")
    private String locationUpdatedTopic;

    @Value("${kafka.topics.bulk-locations-updated:bulk-locations-updated}")
    private String bulkLocationsUpdatedTopic;

    public void publishLocationUpdated(Location location) {
        try {
            String key = location.getTenantId() + ":" + location.getEntityType() + ":" + location.getEntityId();
            String payload = objectMapper.writeValueAsString(location);

            kafkaTemplate.send(locationUpdatedTopic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish location updated event: {}", key, ex);
                        } else {
                            log.debug("Published location updated event: {}", key);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize location event", e);
        }
    }

    public void publishBulkLocationsUpdated(String tenantId, List<Location> locations) {
        try {
            String key = tenantId;
            String payload = objectMapper.writeValueAsString(locations);

            kafkaTemplate.send(bulkLocationsUpdatedTopic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish bulk locations updated event for tenant: {}", tenantId, ex);
                        } else {
                            log.debug("Published bulk locations updated event for tenant: {}", tenantId);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize bulk locations event", e);
        }
    }
}
