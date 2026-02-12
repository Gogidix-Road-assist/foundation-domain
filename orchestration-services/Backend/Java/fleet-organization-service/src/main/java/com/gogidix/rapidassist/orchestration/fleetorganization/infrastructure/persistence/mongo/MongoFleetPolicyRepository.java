package com.gogidix.rapidassist.orchestration.fleetorganization.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyScope;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyType;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository.FleetPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoFleetPolicyRepository implements FleetPolicyRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public FleetPolicy save(FleetPolicy policy) {
        log.debug("Saving fleet policy: {} for tenant: {}", policy.getPolicyId(), policy.getTenantId());
        return mongoTemplate.save(policy);
    }

    @Override
    public Optional<FleetPolicy> findByIdAndTenantId(String policyId, String tenantId) {
        Query query = Query.query(
                Criteria.where("policyId").is(policyId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, FleetPolicy.class));
    }

    @Override
    public List<FleetPolicy> findByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findByOrganizationIdAndTenantId(String organizationId, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findByTypeAndTenantId(PolicyType type, String tenantId) {
        Query query = Query.query(
                Criteria.where("policyType").is(type)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findByTenantIdAndIsActive(String tenantId, boolean isActive) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("isActive").is(isActive)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findEffectiveByTenantId(String tenantId, LocalDateTime currentDate) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("isActive").is(true)
                        .and("deletedAt").is(null)
                        .andOperator(
                                Criteria.where("effectiveFrom").lte(currentDate),
                                new Criteria().orOperator(
                                        Criteria.where("effectiveUntil").is(null),
                                        Criteria.where("effectiveUntil").gte(currentDate)
                                )
                        )
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findByScopeAndTenantId(PolicyScope scope, String tenantId) {
        Query query = Query.query(
                Criteria.where("scope").is(scope)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findByTenantIdOrderByPriorityDesc(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        ).with(Sort.by(Sort.Direction.DESC, "priority"));
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public List<FleetPolicy> findByOrganizationIdAndTypeAndTenantId(
            String organizationId, PolicyType type, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("policyType").is(type)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetPolicy.class);
    }

    @Override
    public void deleteByIdAndTenantId(String policyId, String tenantId) {
        Query query = Query.query(
                Criteria.where("policyId").is(policyId)
                        .and("tenantId").is(tenantId)
        );
        FleetPolicy policy = mongoTemplate.findOne(query, FleetPolicy.class);
        if (policy != null) {
            policy.softDelete();
            mongoTemplate.save(policy);
        }
    }

    @Override
    public boolean existsByIdAndTenantId(String policyId, String tenantId) {
        Query query = Query.query(
                Criteria.where("policyId").is(policyId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.exists(query, FleetPolicy.class);
    }

    @Override
    public long countByOrganizationIdAndTenantId(String organizationId, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.count(query, FleetPolicy.class);
    }

    @Override
    public long countByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.count(query, FleetPolicy.class);
    }
}
