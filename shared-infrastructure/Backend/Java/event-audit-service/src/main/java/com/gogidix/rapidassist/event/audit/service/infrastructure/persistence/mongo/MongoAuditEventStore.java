package com.gogidix.rapidassist.event.audit.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;
import com.gogidix.rapidassist.event.audit.service.domain.port.out.AuditEventStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoAuditEventStore implements AuditEventStore {

    private final MongoTemplate mongoTemplate;

    public MongoAuditEventStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String append(AuditEvent event, Instant expiresAt) {
        AuditEventDocument doc = toDocument(event, expiresAt);
        AuditEventDocument saved = mongoTemplate.insert(doc);
        return saved.getId();
    }

    @Override
    public List<AuditEvent> query(String tenantId, String country, String entityType, String entityId, Instant from, Instant to, int limit) {
        Criteria criteria = Criteria.where("tenantId").is(tenantId);

        if (country != null && !country.isBlank()) {
            criteria = criteria.and("country").is(country);
        }
        if (entityType != null && !entityType.isBlank()) {
            criteria = criteria.and("entityType").is(entityType);
        }
        if (entityId != null && !entityId.isBlank()) {
            criteria = criteria.and("entityId").is(entityId);
        }
        if (from != null || to != null) {
            Criteria timeCriteria = Criteria.where("occurredAt");
            if (from != null) {
                timeCriteria = timeCriteria.gte(from);
            }
            if (to != null) {
                timeCriteria = timeCriteria.lte(to);
            }
            criteria = new Criteria().andOperator(criteria, timeCriteria);
        }

        Query query = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "occurredAt"))
                .limit(limit);

        return mongoTemplate.find(query, AuditEventDocument.class)
                .stream()
                .map(MongoAuditEventStore::toDomain)
                .toList();
    }

    private static AuditEventDocument toDocument(AuditEvent event, Instant expiresAt) {
        AuditEventDocument doc = new AuditEventDocument();
        doc.setTenantId(event.tenantId());
        doc.setCountry(event.country());
        doc.setCorrelationId(event.correlationId());
        doc.setOccurredAt(event.occurredAt());
        doc.setExpiresAt(expiresAt);
        doc.setEventType(event.eventType());
        doc.setEntityType(event.entityType());
        doc.setEntityId(event.entityId());
        doc.setPayload(event.payload());
        return doc;
    }

    private static AuditEvent toDomain(AuditEventDocument doc) {
        return new AuditEvent(
                doc.getTenantId(),
                doc.getCountry(),
                doc.getCorrelationId(),
                doc.getOccurredAt(),
                doc.getEventType(),
                doc.getEntityType(),
                doc.getEntityId(),
                doc.getPayload()
        );
    }
}
