package com.gogidix.rapidassist.orchestration.fleetorganization.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization.OrganizationType;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository.OrganizationRepository;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of Organization repository
 * All queries MANDATORILY filter by tenantId for multi-tenancy
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoOrganizationRepository implements OrganizationRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Organization save(Organization organization) {
        log.debug("Saving organization: {} for tenant: {}",
                organization.getOrganizationId(), organization.getTenantId());
        return mongoTemplate.save(organization);
    }

    @Override
    public Optional<Organization> findByIdAndTenantId(String organizationId, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, Organization.class));
    }

    @Override
    public List<Organization> findByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, Organization.class);
    }

    @Override
    public List<Organization> findByParentIdAndTenantId(String parentId, String tenantId) {
        Query query = Query.query(
                Criteria.where("parentId").is(parentId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, Organization.class);
    }

    @Override
    public List<Organization> findByTypeAndTenantId(OrganizationType type, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationType").is(type)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, Organization.class);
    }

    @Override
    public Optional<Organization> findRootByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationType").is(OrganizationType.ROOT)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, Organization.class));
    }

    @Override
    public List<Organization> findByPathStartingWithAndTenantId(String pathPrefix, String tenantId) {
        Query query = Query.query(
                Criteria.where("path").regex("^" + pathPrefix)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, Organization.class);
    }

    @Override
    public List<Organization> findByTenantIdAndIsActive(String tenantId, boolean isActive) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("isActive").is(isActive)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, Organization.class);
    }

    @Override
    public void deleteByIdAndTenantId(String organizationId, String tenantId) {
        // Soft delete - set deletedAt timestamp
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
        );
        Organization organization = mongoTemplate.findOne(query, Organization.class);
        if (organization != null) {
            organization.softDelete();
            mongoTemplate.save(organization);
        }
    }

    @Override
    public boolean existsByIdAndTenantId(String organizationId, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.exists(query, Organization.class);
    }

    @Override
    public long countByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.count(query, Organization.class);
    }
}
