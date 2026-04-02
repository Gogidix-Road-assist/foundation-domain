package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Subject;
import com.gogidix.rapidassist.access.control.service.domain.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository Implementation: MongoSubjectRepository
 *
 * Implements the SubjectRepository port using MongoDB.
 * ALL queries enforce tenant isolation via tenantId filtering.
 */
@Repository
public class MongoSubjectRepository implements SubjectRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoSubjectRepository.class);

    private final MongoTemplate mongoTemplate;
    private final SubjectDocumentConverter converter;

    public MongoSubjectRepository(MongoTemplate mongoTemplate, SubjectDocumentConverter converter) {
        this.mongoTemplate = mongoTemplate;
        this.converter = converter;
    }

    @Override
    public Subject save(Subject subject) {
        log.debug("Saving subject: id={}, tenantId={}, subjectKey={}",
                subject.getId(), subject.getTenantId(), subject.getSubjectKey());
        SubjectDocument document = converter.toDocument(subject);
        SubjectDocument saved = mongoTemplate.save(document);
        return converter.toDomain(saved);
    }

    @Override
    public Optional<Subject> findById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        SubjectDocument document = mongoTemplate.findOne(query, SubjectDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public Optional<Subject> findBySubjectKey(String subjectKey, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectKey").is(subjectKey)
        );
        SubjectDocument document = mongoTemplate.findOne(query, SubjectDocument.class);
        return Optional.ofNullable(document).map(converter::toDomain);
    }

    @Override
    public List<Subject> findByTenantId(String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, SubjectDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Subject> findActiveByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("active").is(true)
        );
        return mongoTemplate.find(query, SubjectDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<Subject> findBySubjectType(String subjectType, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectType").is(subjectType)
        );
        return mongoTemplate.find(query, SubjectDocument.class).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(String id, String tenantId) {
        Query query = Query.query(
                Criteria.where("id").is(id)
                        .and("tenantId").is(tenantId)
        );
        var result = mongoTemplate.remove(query, SubjectDocument.class);
        log.debug("Deleted subject: id={}, deletedCount={}", id, result.getDeletedCount());
        return result.getDeletedCount() > 0;
    }

    @Override
    public boolean existsBySubjectKey(String subjectKey, String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("subjectKey").is(subjectKey)
        );
        return mongoTemplate.exists(query, SubjectDocument.class);
    }
}
