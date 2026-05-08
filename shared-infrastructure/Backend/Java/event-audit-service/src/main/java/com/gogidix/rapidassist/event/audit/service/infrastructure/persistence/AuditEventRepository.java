package com.gogidix.rapidassist.event.audit.service.infrastructure.persistence;

import com.gogidix.rapidassist.event.audit.service.infrastructure.persistence.mongo.AuditEventDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class AuditEventRepository {

    private final MongoTemplate mongoTemplate;

    public AuditEventRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AuditEventDocument save(AuditEventDocument document) {
        return mongoTemplate.save(document);
    }

    public List<AuditEventDocument> findByTenantId(String tenantId, int limit) {
        var query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("tenantId").is(tenantId)
        ).limit(limit);
        return mongoTemplate.find(query, AuditEventDocument.class);
    }

    public long countByTenantId(String tenantId) {
        var query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("tenantId").is(tenantId)
        );
        return mongoTemplate.count(query, AuditEventDocument.class);
    }
}
