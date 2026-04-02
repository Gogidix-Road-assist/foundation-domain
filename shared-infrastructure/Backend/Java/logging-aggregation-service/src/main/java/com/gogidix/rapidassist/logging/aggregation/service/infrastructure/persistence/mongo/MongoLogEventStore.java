package com.gogidix.rapidassist.logging.aggregation.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.out.LogEventStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoLogEventStore implements LogEventStore {

    private final MongoTemplate template;

    public MongoLogEventStore(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void append(LogEvent event, Instant expiresAt) {
        template.save(toDocument(event, expiresAt));
    }

    @Override
    public List<LogEvent> query(String tenantId, String correlationId, Instant from, Instant to, String level, int limit) {
        Criteria criteria = Criteria.where("tenantId").is(tenantId);

        if (correlationId != null && !correlationId.isBlank()) {
            criteria = criteria.and("correlationId").is(correlationId);
        }

        if (level != null && !level.isBlank()) {
            criteria = criteria.and("level").is(level);
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

        return template.find(q, LogEventDocument.class).stream().map(MongoLogEventStore::toDomain).toList();
    }

    private static LogEventDocument toDocument(LogEvent event, Instant expiresAt) {
        LogEventDocument doc = new LogEventDocument();
        doc.setTenantId(event.tenantId());
        doc.setCountry(event.country());
        doc.setCorrelationId(event.correlationId());
        doc.setTimestamp(event.timestamp());
        doc.setLevel(event.level());
        doc.setLogger(event.logger());
        doc.setMessage(event.message());
        doc.setAttributes(event.attributes());
        doc.setExpiresAt(expiresAt);
        return doc;
    }

    private static LogEvent toDomain(LogEventDocument doc) {
        return new LogEvent(
                doc.getTenantId(),
                doc.getCountry(),
                doc.getCorrelationId(),
                doc.getTimestamp(),
                doc.getLevel(),
                doc.getLogger(),
                doc.getMessage(),
                doc.getAttributes()
        );
    }
}
