package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Resource;
import com.gogidix.rapidassist.access.control.service.domain.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository Implementation: MongoResourceRepository
 *
 * Implements the ResourceRepository port using MongoDB.
 * ALL queries enforce tenant isolation via tenantId filtering.
 */
@Repository
public class MongoResourceRepository implements ResourceRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoResourceRepository.class);

    private final MongoTemplate mongoTemplate;
    private final ResourceDocumentConverter converter;

    public MongoResourceRepository(MongoTemplate mongoTemplate, ResourceDocumentConverter converter) {
        this.mongoTemplate = mongoTemplate;
        this.converter = converter;
    }

    @Override
    public Resource save(Resource resource) {
        log.debug("Saving resource: id={}, tenantId={}, path={}",
                resource.getId(), resource.getTenantId(), resource.getResourcePath());
        ResourceDocument document = converter.toDocument(resource);
        ResourceDocument saved = mongoTemplate.save(document);
        return converter.toDomain(saved);
    }

    @Override
    public Optional<Resource> findById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        ResourceDocument document = mongoTemplate.findOne(query, ResourceDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public Optional<Resource> findByResourcePath(String resourcePath, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("resourcePath").is(resourcePath)
        );
        ResourceDocument document = mongoTemplate.findOne(query, ResourceDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public List<Resource> findByTenantId(String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, ResourceDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Resource> findActiveByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("active").is(true)
        );
        return mongoTemplate.find(query, ResourceDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Resource> findByResourceType(String resourceType, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("resourceType").is(resourceType)
        );
        return mongoTemplate.find(query, ResourceDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Resource> findByOwner(String owner, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("owner").is(owner)
        );
        return mongoTemplate.find(query, ResourceDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        var result = mongoTemplate.remove(query, ResourceDocument.class);
        log.debug("Deleted resource: id={}, deletedCount={}", id, result.getDeletedCount());
        return result.getDeletedCount() > 0;
    }

    @Override
    public boolean existsByResourcePath(String resourcePath, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("resourcePath").is(resourcePath)
        );
        return mongoTemplate.exists(query, ResourceDocument.class);
    }
}
