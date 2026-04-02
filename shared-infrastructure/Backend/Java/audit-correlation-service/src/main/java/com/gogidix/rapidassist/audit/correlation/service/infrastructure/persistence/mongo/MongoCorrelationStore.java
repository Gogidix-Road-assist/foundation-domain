package com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.out.CorrelationStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.Optional;

public class MongoCorrelationStore implements CorrelationStore {

    private final MongoTemplate template;

    public MongoCorrelationStore(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void upsert(CorrelationRecord record, Instant expiresAt) {
        CorrelationDocument doc = toDocument(record, expiresAt);
        template.save(doc);
    }

    @Override
    public Optional<CorrelationRecord> get(String tenantId, String correlationId) {
        Query q = new Query(Criteria.where("tenantId").is(tenantId).and("correlationId").is(correlationId));
        CorrelationDocument found = template.findOne(q, CorrelationDocument.class);
        return Optional.ofNullable(found).map(MongoCorrelationStore::toDomain);
    }

    private static CorrelationDocument toDocument(CorrelationRecord record, Instant expiresAt) {
        CorrelationDocument doc = new CorrelationDocument();
        doc.setTenantId(record.tenantId());
        doc.setCountry(record.country());
        doc.setCorrelationId(record.correlationId());
        doc.setCreatedAt(record.createdAt());
        doc.setExpiresAt(expiresAt);
        doc.setTags(record.tags());
        return doc;
    }

    private static CorrelationRecord toDomain(CorrelationDocument doc) {
        return new CorrelationRecord(
                doc.getTenantId(),
                doc.getCountry(),
                doc.getCorrelationId(),
                doc.getCreatedAt(),
                doc.getTags()
        );
    }
}
