package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository Implementation: MongoPermissionRepository
 *
 * Implements the PermissionRepository port using MongoDB.
 * ALL queries enforce tenant isolation via tenantId filtering.
 *
 * This is an ADAPTER in the hexagonal architecture.
 */
@Repository
public class MongoPermissionRepository implements PermissionRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoPermissionRepository.class);

    private final MongoTemplate mongoTemplate;
    private final PermissionDocumentConverter converter;

    public MongoPermissionRepository(MongoTemplate mongoTemplate,
                                     PermissionDocumentConverter converter) {
        this.mongoTemplate = mongoTemplate;
        this.converter = converter;
    }

    @Override
    public Permission save(Permission permission) {
        log.debug("Saving permission: id={}, tenantId={}", permission.getId(), permission.getTenantId());
        PermissionDocument document = converter.toDocument(permission);
        PermissionDocument saved = mongoTemplate.save(document);
        return converter.toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        PermissionDocument document = mongoTemplate.findOne(query, PermissionDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public List<Permission> findBySubjectId(String subjectId, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectId").is(subjectId)
        );
        return mongoTemplate.find(query, PermissionDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findByTenantId(String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, PermissionDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findValidBySubjectId(String subjectId, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectId").is(subjectId)
                        .and("active").is(true)
                        .orOperator(
                                Criteria.where("validUntil").is(null),
                                Criteria.where("validUntil").gt(Instant.now())
                        )
        );
        return mongoTemplate.find(query, PermissionDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findBySubjectAndResourceAndAction(String subjectId, String resource,
                                                             String action, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectId").is(subjectId)
                        .and("resource").in(resource, "*")
                        .and("action").in(action, "*")
                        .and("active").is(true)
        );
        return mongoTemplate.find(query, PermissionDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findByRole(String roleId, String tenantId) {
        // Find permissions where subjectId is the roleId and subjectType is ROLE
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectId").is(roleId)
                        .and("subjectType").is("ROLE")
                        .and("active").is(true)
        );
        return mongoTemplate.find(query, PermissionDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        var result = mongoTemplate.remove(query, PermissionDocument.class);
        log.debug("Deleted permission: id={}, deletedCount={}", id, result.getDeletedCount());
        return result.getDeletedCount() > 0;
    }

    @Override
    public int deleteBySubjectId(String subjectId, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectId").is(subjectId)
        );
        var result = mongoTemplate.remove(query, PermissionDocument.class);
        return (int) result.getDeletedCount();
    }

    @Override
    public boolean exists(String subjectId, String resource, String action, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectId").is(subjectId)
                        .and("resource").is(resource)
                        .and("action").is(action)
                        .and("active").is(true)
        );
        return mongoTemplate.exists(query, PermissionDocument.class);
    }
}
