package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.rate.limiting.service.domain.port.out.RateLimitCounterStore;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.util.Optional;

public class MongoRateLimitCounterStore implements RateLimitCounterStore {

    private final MongoTemplate mongoTemplate;

    public MongoRateLimitCounterStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Long> incrementAndGetCount(String tenantId, String key, Instant windowStart, Instant expiresAt) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("key").is(key)
                        .and("windowStart").is(windowStart)
        );

        Update update = new Update()
                .inc("count", 1)
                .setOnInsert("tenantId", tenantId)
                .setOnInsert("key", key)
                .setOnInsert("windowStart", windowStart)
                .set("expiresAt", expiresAt);

        FindAndModifyOptions options = FindAndModifyOptions.options()
                .upsert(true)
                .returnNew(true);

        RateLimitCounterDocument updated = mongoTemplate.findAndModify(query, update, options, RateLimitCounterDocument.class);
        if (updated == null) {
            return Optional.empty();
        }
        return Optional.of(updated.getCount());
    }
}
