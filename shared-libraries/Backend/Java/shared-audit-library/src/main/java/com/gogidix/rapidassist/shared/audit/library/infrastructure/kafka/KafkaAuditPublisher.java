package com.gogidix.rapidassist.shared.audit.library.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import com.gogidix.rapidassist.shared.audit.library.domain.port.out.AuditPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka implementation of AuditPublisher.
 * Publishes audit events to a Kafka topic for distributed event processing.
 *
 * <p>This implementation requires spring-kafka to be on the classpath.
 * It will automatically be configured when a KafkaTemplate is available.
 *
 * <p>To enable this publisher, ensure:
 * <ul>
 *   <li>Spring Kafka is configured</li>
 *   <li>A Kafka broker is accessible</li>
 *   <li>The audit topic exists or auto-creation is enabled</li>
 * </ul>
 *
 * <p>The topic name can be configured via {@code audit.kafka.topic} property.
 */
public class KafkaAuditPublisher implements AuditPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditPublisher.class);

    private final KafkaTemplate<String, AuditEventEnvelope> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public KafkaAuditPublisher(KafkaTemplate<String, AuditEventEnvelope> kafkaTemplate,
                               ObjectMapper objectMapper,
                               String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Override
    public void publish(AuditEventEnvelope event) {
        try {
            String key = buildMessageKey(event);

            CompletableFuture<SendResult<String, AuditEventEnvelope>> future = kafkaTemplate.send(topic, key, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Audit event published to Kafka: eventId={}, topic={}, partition={}",
                            event.eventId(), topic,
                            result != null ? result.getRecordMetadata().partition() : null);
                } else {
                    log.error("Failed to publish audit event to Kafka: eventId={}, topic={}",
                            event.eventId(), topic, ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending audit event to Kafka: eventId={}", event.eventId(), e);
            // Don't throw - audit failures should not break the main application flow
        }
    }

    /**
     * Build the Kafka message key from the audit event.
     * Uses tenant ID as the key to ensure all events for a tenant go to the same partition.
     *
     * @param event the audit event
     * @return the message key
     */
    private String buildMessageKey(AuditEventEnvelope event) {
        return event.tenantId() + "|" + event.entity().type() + "|" + event.entity().id();
    }

    public String getTopic() {
        return topic;
    }
}
