package com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.out.TelemetryEventStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoTelemetryEventStore implements TelemetryEventStore {

    private final MongoTemplate template;

    public MongoTelemetryEventStore(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void append(TelemetryEvent event, Instant expiresAt) {
        template.save(toDocument(event, expiresAt));
    }

    @Override
    public List<TelemetryEvent> query(String tenantId, String correlationId, Instant from, Instant to, String type, String name, int limit) {
        Criteria criteria = Criteria.where("tenantId").is(tenantId);

        if (correlationId != null && !correlationId.isBlank()) {
            criteria = criteria.and("correlationId").is(correlationId);
        }

        if (type != null && !type.isBlank()) {
            criteria = criteria.and("type").is(type);
        }

        if (name != null && !name.isBlank()) {
            criteria = criteria.and("name").is(name);
        }

        if (from != null || to != null) {
            Criteria ts = Criteria.where("timestamp");
            if (from != null && to != null) {
                ts = ts.gte(from).lte(to);
            } else if (from != null) {
                ts = ts.gte(from);
            } else {
                ts = ts.lte(to);
            }
            criteria = new Criteria().andOperator(criteria, ts);
        }

        Query q = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(limit);

        return template.find(q, TelemetryEventDocument.class).stream().map(MongoTelemetryEventStore::toDomain).toList();
    }

    private static TelemetryEventDocument toDocument(TelemetryEvent event, Instant expiresAt) {
        TelemetryEventDocument doc = new TelemetryEventDocument();
        doc.setTenantId(event.tenantId());
        doc.setCountry(event.country());
        doc.setCorrelationId(event.correlationId());
        doc.setTimestamp(event.timestamp());
        doc.setType(event.type());
        doc.setName(event.name());
        doc.setValue(event.value());
        doc.setAttributes(event.attributes());
        doc.setExpiresAt(expiresAt);
        return doc;
    }

    private static TelemetryEvent toDomain(TelemetryEventDocument doc) {
        return new TelemetryEvent(
                doc.getTenantId(),
                doc.getCountry(),
                doc.getCorrelationId(),
                doc.getTimestamp(),
                doc.getType(),
                doc.getName(),
                doc.getValue(),
                doc.getAttributes()
        );
    }
}
