package com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence;

import com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence.mongo.CorrelationDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CorrelationRepository {

    private final MongoTemplate mongoTemplate;

    public CorrelationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public CorrelationDocument save(CorrelationDocument document) {
        return mongoTemplate.save(document);
    }

    public Optional<CorrelationDocument> findByTenantAndCorrelationId(String tenantId, String correlationId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
                .and("correlationId").is(correlationId));
        return Optional.ofNullable(mongoTemplate.findOne(query, CorrelationDocument.class));
    }
}
