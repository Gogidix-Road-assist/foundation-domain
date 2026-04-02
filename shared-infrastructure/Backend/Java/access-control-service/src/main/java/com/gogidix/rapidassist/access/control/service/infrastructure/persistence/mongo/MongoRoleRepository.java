package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Role;
import com.gogidix.rapidassist.access.control.service.domain.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository Implementation: MongoRoleRepository
 *
 * Implements the RoleRepository port using MongoDB.
 * ALL queries enforce tenant isolation via tenantId filtering.
 */
@Repository
public class MongoRoleRepository implements RoleRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoRoleRepository.class);

    private final MongoTemplate mongoTemplate;
    private final RoleDocumentConverter converter;

    public MongoRoleRepository(MongoTemplate mongoTemplate, RoleDocumentConverter converter) {
        this.mongoTemplate = mongoTemplate;
        this.converter = converter;
    }

    @Override
    public Role save(Role role) {
        log.debug("Saving role: id={}, tenantId={}, name={}", role.getId(), role.getTenantId(), role.getName());
        RoleDocument document = converter.toDocument(role);
        RoleDocument saved = mongoTemplate.save(document);
        return converter.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        RoleDocument document = mongoTemplate.findOne(query, RoleDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("name").is(name)
        );
        RoleDocument document = mongoTemplate.findOne(query, RoleDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public List<Role> findByTenantId(String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, RoleDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Role> findActiveByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("active").is(true)
        );
        return mongoTemplate.find(query, RoleDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Role> findBySubjectId(String subjectId, String tenantId) {
        // This would require a subject_roles collection or join
        // For now, return empty list as roles are assigned to subjects via the subject document
        return List.of();
    }

    @Override
    public boolean deleteById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        var result = mongoTemplate.remove(query, RoleDocument.class);
        log.debug("Deleted role: id={}, deletedCount={}", id, result.getDeletedCount());
        return result.getDeletedCount() > 0;
    }

    @Override
    public boolean existsByName(String name, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("name").is(name)
        );
        return mongoTemplate.exists(query, RoleDocument.class);
    }
}
