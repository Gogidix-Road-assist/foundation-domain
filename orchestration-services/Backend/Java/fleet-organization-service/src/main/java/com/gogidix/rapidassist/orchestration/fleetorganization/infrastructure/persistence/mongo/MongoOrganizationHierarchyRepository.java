package com.gogidix.rapidassist.orchestration.fleetorganization.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.OrganizationHierarchy;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository.OrganizationHierarchyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoOrganizationHierarchyRepository implements OrganizationHierarchyRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public OrganizationHierarchy save(OrganizationHierarchy hierarchy) {
        return mongoTemplate.save(hierarchy);
    }

    @Override
    public Iterable<OrganizationHierarchy> saveAll(List<OrganizationHierarchy> hierarchies) {
        return mongoTemplate.insert(hierarchies, OrganizationHierarchy.class);
    }

    @Override
    public List<OrganizationHierarchy> findByDescendantId(String descendantId) {
        Query query = Query.query(Criteria.where("descendantId").is(descendantId));
        return mongoTemplate.find(query, OrganizationHierarchy.class);
    }

    @Override
    public List<OrganizationHierarchy> findByAncestorId(String ancestorId) {
        Query query = Query.query(Criteria.where("ancestorId").is(ancestorId));
        return mongoTemplate.find(query, OrganizationHierarchy.class);
    }

    @Override
    public List<OrganizationHierarchy> findByAncestorIdAndDepth(String ancestorId, int depth) {
        Query query = Query.query(
                Criteria.where("ancestorId").is(ancestorId)
                        .and("depth").is(depth)
        );
        return mongoTemplate.find(query, OrganizationHierarchy.class);
    }

    @Override
    public OrganizationHierarchy findByAncestorIdAndDescendantIdAndDepth(
            String ancestorId, String descendantId, int depth) {
        Query query = Query.query(
                Criteria.where("ancestorId").is(ancestorId)
                        .and("descendantId").is(descendantId)
                        .and("depth").is(depth)
        );
        return mongoTemplate.findOne(query, OrganizationHierarchy.class);
    }

    @Override
    public List<OrganizationHierarchy> findByAncestorIdAndDepthGreaterThan(
            String ancestorId, int depth) {
        Query query = Query.query(
                Criteria.where("ancestorId").is(ancestorId)
                        .and("depth").gt(depth)
        );
        return mongoTemplate.find(query, OrganizationHierarchy.class);
    }

    @Override
    public void deleteByAncestorId(String ancestorId) {
        Query query = Query.query(Criteria.where("ancestorId").is(ancestorId));
        mongoTemplate.remove(query, OrganizationHierarchy.class);
    }

    @Override
    public void deleteByDescendantId(String descendantId) {
        Query query = Query.query(Criteria.where("descendantId").is(descendantId));
        mongoTemplate.remove(query, OrganizationHierarchy.class);
    }

    @Override
    public void deleteByAncestorIdAndDescendantId(String ancestorId, String descendantId) {
        Query query = Query.query(
                Criteria.where("ancestorId").is(ancestorId)
                        .and("descendantId").is(descendantId)
        );
        mongoTemplate.remove(query, OrganizationHierarchy.class);
    }

    @Override
    public void deleteByDescendantIdIn(List<String> descendantIds) {
        Query query = Query.query(Criteria.where("descendantId").in(descendantIds));
        mongoTemplate.remove(query, OrganizationHierarchy.class);
    }

    @Override
    public boolean existsByAncestorIdAndDescendantId(String ancestorId, String descendantId) {
        Query query = Query.query(
                Criteria.where("ancestorId").is(ancestorId)
                        .and("descendantId").is(descendantId)
        );
        return mongoTemplate.exists(query, OrganizationHierarchy.class);
    }

    @Override
    public Integer getDepth(String ancestorId, String descendantId) {
        Query query = Query.query(
                Criteria.where("ancestorId").is(ancestorId)
                        .and("descendantId").is(descendantId)
        );
        OrganizationHierarchy hierarchy = mongoTemplate.findOne(query, OrganizationHierarchy.class);
        return hierarchy != null ? hierarchy.getDepth() : null;
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(Query.query(new Criteria()), OrganizationHierarchy.class);
    }
}
