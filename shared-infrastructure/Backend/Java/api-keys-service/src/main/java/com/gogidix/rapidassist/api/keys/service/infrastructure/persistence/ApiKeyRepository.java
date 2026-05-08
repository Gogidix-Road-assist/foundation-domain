package com.gogidix.rapidassist.api.keys.service.infrastructure.persistence;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ApiKeyRepository {

    private final MongoTemplate mongoTemplate;

    public ApiKeyRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String generateKeyId() {
        return UUID.randomUUID().toString();
    }

    public boolean collectionExists() {
        return mongoTemplate.collectionExists("api_keys");
    }
}
