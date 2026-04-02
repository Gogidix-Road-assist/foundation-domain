package com.gogidix.rapidassist.access.control.service.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gogidix.rapidassist.access.control.service.domain.event.AccessDeniedEvent;
import com.gogidix.rapidassist.access.control.service.domain.event.PermissionGrantedEvent;
import com.gogidix.rapidassist.access.control.service.domain.event.PermissionRevokedEvent;
import com.gogidix.rapidassist.access.control.service.domain.port.out.AuditEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Publisher: KafkaAuditEventPublisher
 *
 * Publishes domain events to Kafka for distributed audit logging.
 *
 * This is an ADAPTER in the hexagonal architecture - implements
 * the AuditEventPublisher output port using Kafka.
 */
@Component
public class KafkaAuditEventPublisher implements AuditEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String ACCESS_CONTROL_TOPIC = "access-control.audit";

    public KafkaAuditEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void publish(PermissionGrantedEvent event) {
        publishEvent("PermissionGranted", event);
    }

    @Override
    public void publish(PermissionRevokedEvent event) {
        publishEvent("PermissionRevoked", event);
    }

    @Override
    public void publish(AccessDeniedEvent event) {
        publishEvent("AccessDenied", event);
    }

    @Override
    public void publish(String eventType, Object payload) {
        publishEvent(eventType, payload);
    }

    private void publishEvent(String eventType, Object payload) {
        try {
            String key = extractTenantId(payload);
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(ACCESS_CONTROL_TOPIC, key, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event {}: {}", eventType, ex.getMessage());
                } else {
                    log.debug("Published event {} to partition {}", eventType,
                            result.getRecordMetadata().partition());
                }
            });
        } catch (Exception e) {
            log.error("Error publishing event {}", eventType, e);
        }
    }

    private String extractTenantId(Object payload) {
        // Extract tenantId for Kafka partitioning
        try {
            if (payload instanceof PermissionGrantedEvent e) {
                return e.getTenantId();
            } else if (payload instanceof PermissionRevokedEvent e) {
                return e.getTenantId();
            } else if (payload instanceof AccessDeniedEvent e) {
                return e.getTenantId();
            }
        } catch (Exception e) {
            log.warn("Could not extract tenantId from payload", e);
        }
        return "default";
    }
}
