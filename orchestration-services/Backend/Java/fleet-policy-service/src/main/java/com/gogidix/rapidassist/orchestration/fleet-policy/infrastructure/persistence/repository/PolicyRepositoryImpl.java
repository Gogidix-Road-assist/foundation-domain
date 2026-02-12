package com.gogidix.rapidassist.orchestration.fleet_policy.infrastructure.persistence.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of Policy repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PolicyRepositoryImpl implements PolicyRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Policy save(Policy policy) {
        return mongoTemplate.save(policy);
    }

    @Override
    public Optional<Policy> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Policy.class));
    }

    @Override
    public Optional<Policy> findByTenantIdAndPolicyCode(String tenantId, String policyCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("policyCode").is(policyCode));
        return Optional.ofNullable(mongoTemplate.findOne(query, Policy.class));
    }

    @Override
    public List<Policy> findByTenantId(String tenantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public List<Policy> findByTenantIdAndIsActive(String tenantId, Boolean isActive) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("isActive").is(isActive));
        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public List<Policy> findByTenantIdAndPolicyType(String tenantId, Policy.PolicyType policyType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("policyType").is(policyType));
        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public List<Policy> findEffectivePolicies(String tenantId, LocalDateTime dateTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("isActive").is(true));
        query.addCriteria(Criteria.where("status").is(Policy.PolicyStatus.ACTIVE));

        // Check effective date range
        Criteria dateCriteria = new Criteria().orOperator(
                Criteria.where("effectiveFrom").lte(dateTime),
                Criteria.where("effectiveFrom").exists(false)
        );
        query.addCriteria(dateCriteria);

        Criteria endDateCriteria = new Criteria().orOperator(
                Criteria.where("effectiveTo").gte(dateTime),
                Criteria.where("effectiveTo").exists(false)
        );
        query.addCriteria(endDateCriteria);

        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public List<Policy> findByTenantIdAndStatus(String tenantId, Policy.PolicyStatus status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("status").is(status));
        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public List<Policy> findByTenantIdAndRequiresApproval(String tenantId, Boolean requiresApproval) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("requiresApproval").is(requiresApproval));
        query.addCriteria(Criteria.where("status").is(Policy.PolicyStatus.PENDING_APPROVAL));
        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public List<Policy> searchPolicies(String tenantId, String searchTerm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));

        Criteria searchCriteria = new Criteria().orOperator(
                Criteria.where("name").regex(searchTerm, "i"),
                Criteria.where("description").regex(searchTerm, "i"),
                Criteria.where("policyCode").regex(searchTerm, "i")
        );
        query.addCriteria(searchCriteria);

        return mongoTemplate.find(query, Policy.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), Policy.class);
    }

    @Override
    public boolean existsByTenantIdAndPolicyCode(String tenantId, String policyCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("policyCode").is(policyCode));
        return mongoTemplate.exists(query, Policy.class);
    }

    @Override
    public long countByTenantIdAndPolicyType(String tenantId, Policy.PolicyType policyType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("policyType").is(policyType));
        return mongoTemplate.count(query, Policy.class);
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(new Query(), Policy.class);
    }
}
