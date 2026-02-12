package com.gogidix.rapidassist.shared.audit.library.infrastructure.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditActor;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEntityRef;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import com.gogidix.rapidassist.shared.audit.library.domain.port.out.AuditPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Database implementation of AuditPublisher.
 * Persists audit events to a relational database using JPA.
 *
 * <p>This implementation requires spring-boot-starter-data-jpa to be on the classpath.
 * It will automatically be configured when the AuditLogRepository is available.
 *
 * <p>To enable this publisher, ensure:
 * <ul>
 *   <li>Spring Data JPA is configured</li>
 *   <li>A datasource is configured</li>
 *   <li>The audit_logs table exists or JPA is set to create tables</li>
 * </ul>
 */
public class DatabaseAuditPublisher implements AuditPublisher {

    private static final Logger log = LoggerFactory.getLogger(DatabaseAuditPublisher.class);

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public DatabaseAuditPublisher(AuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(AuditEventEnvelope event) {
        try {
            AuditLogEntity entity = convertToEntity(event);
            repository.save(entity);
            log.debug("Audit event persisted to database: eventId={}, eventType={}",
                    event.eventId(), event.eventType());
        } catch (Exception e) {
            log.error("Failed to persist audit event to database: eventId={}", event.eventId(), e);
            // Don't throw - audit failures should not break the main application flow
        }
    }

    private AuditLogEntity convertToEntity(AuditEventEnvelope event) {
        String attributesJson = serializeMap(event.attributes());
        String payloadJson = serializeObject(event.payload());

        AuditActor actor = event.actor();
        AuditEntityRef entityRef = event.entity();

        return new AuditLogEntity(
                event.specVersion(),
                event.eventId(),
                event.eventType(),
                event.occurredAt(),
                event.correlationId(),
                event.country(),
                event.tenantId(),
                event.subTenantId(),
                actor.id(),
                actor.type(),
                actor.name(),
                entityRef.type(),
                entityRef.id(),
                entityRef.name(),
                attributesJson,
                payloadJson
        );
    }

    private String serializeMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize attributes to JSON: {}", e.getMessage());
            return null;
        }
    }

    private String serializeObject(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize payload to JSON: {}", e.getMessage());
            return "{}";
        }
    }
}
