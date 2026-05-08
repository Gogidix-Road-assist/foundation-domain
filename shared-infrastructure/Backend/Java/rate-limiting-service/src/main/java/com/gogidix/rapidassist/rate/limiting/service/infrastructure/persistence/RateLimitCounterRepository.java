package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class RateLimitCounterRepository {

    private final MongoTemplate mongoTemplate;

    public RateLimitCounterRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long countActiveCounters(String tenantId) {
        var query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("tenantId").is(tenantId)
                        .and("active").is(true)
        );
        return mongoTemplate.count(query, "rate_limit_counters");
    }

    public void deleteExpired(Instant before) {
        var query = new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("expiresAt").lt(before)
        );
        mongoTemplate.remove(query, "rate_limit_counters");
    }
}
