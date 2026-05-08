package com.gogidix.rapidassist.alerting.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertEventStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoAlertEventStore implements AlertEventStore {

    private final MongoTemplate template;

    public MongoAlertEventStore(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void append(AlertEvent event, Instant expiresAt) {
        template.save(toDocument(event, expiresAt));
    }

    @Override
    public List<AlertEvent> query(String tenantId, String ruleId, Instant from, Instant to, int limit) {
        Criteria criteria = Criteria.where("tenantId").is(tenantId);

        if (ruleId != null && !ruleId.isBlank()) {
            criteria = criteria.and("ruleId").is(ruleId);
        }

        if (from != null || to != null) {
            Criteria ts = Criteria.where("occurredAt");
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
                .with(Sort.by(Sort.Direction.DESC, "occurredAt"))
                .limit(limit);

        return template.find(q, AlertEventDocument.class).stream().map(MongoAlertEventStore::toDomain).toList();
    }

    private static AlertEventDocument toDocument(AlertEvent event, Instant expiresAt) {
        AlertEventDocument doc = new AlertEventDocument();
        doc.setTenantId(event.tenantId());
        doc.setCountry(event.country());
        doc.setCorrelationId(event.correlationId());
        doc.setOccurredAt(event.occurredAt());
        doc.setRuleId(event.ruleId());
        doc.setSeverity(event.severity());
        doc.setMessage(event.message());
        doc.setAttributes(event.attributes());
        doc.setExpiresAt(expiresAt);
        return doc;
    }

    private static AlertEvent toDomain(AlertEventDocument doc) {
        return new AlertEvent(
                doc.getTenantId(),
                doc.getCountry(),
                doc.getCorrelationId(),
                doc.getOccurredAt(),
                doc.getRuleId(),
                doc.getSeverity(),
                doc.getMessage(),
                doc.getAttributes()
        );
    }
}
