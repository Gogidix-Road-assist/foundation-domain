package com.gogidix.rapidassist.orchestration.fleet_policy.infrastructure.persistence.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of PolicyRule repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PolicyRuleRepositoryImpl implements PolicyRuleRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PolicyRule save(PolicyRule rule) {
        return mongoTemplate.save(rule);
    }

    @Override
    public Optional<PolicyRule> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PolicyRule.class));
    }

    @Override
    public Optional<PolicyRule> findByTenantIdAndRuleCode(String tenantId, String ruleCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("ruleCode").is(ruleCode));
        return Optional.ofNullable(mongoTemplate.findOne(query, PolicyRule.class));
    }

    @Override
    public List<PolicyRule> findByPolicyId(String policyId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("policyId").is(policyId));
        return mongoTemplate.find(query, PolicyRule.class);
    }

    @Override
    public List<PolicyRule> findByTenantId(String tenantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, PolicyRule.class);
    }

    @Override
    public List<PolicyRule> findByTenantIdAndIsActive(String tenantId, Boolean isActive) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("isActive").is(isActive));
        return mongoTemplate.find(query, PolicyRule.class);
    }

    @Override
    public List<PolicyRule> findByTenantIdAndRuleType(String tenantId, PolicyRule.RuleType ruleType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("ruleType").is(ruleType));
        return mongoTemplate.find(query, PolicyRule.class);
    }

    @Override
    public List<PolicyRule> findByPolicyIdAndIsMandatory(String policyId, Boolean isMandatory) {
        Query query = new Query();
        query.addCriteria(Criteria.where("policyId").is(policyId));
        query.addCriteria(Criteria.where("isMandatory").is(isMandatory));
        return mongoTemplate.find(query, PolicyRule.class);
    }

    @Override
    public List<PolicyRule> searchRules(String tenantId, String searchTerm) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));

        Criteria searchCriteria = new Criteria().orOperator(
                Criteria.where("name").regex(searchTerm, "i"),
                Criteria.where("description").regex(searchTerm, "i"),
                Criteria.where("ruleCode").regex(searchTerm, "i")
        );
        query.addCriteria(searchCriteria);

        return mongoTemplate.find(query, PolicyRule.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), PolicyRule.class);
    }

    @Override
    public void deleteByPolicyId(String policyId) {
        mongoTemplate.remove(Query.query(Criteria.where("policyId").is(policyId)), PolicyRule.class);
    }

    @Override
    public boolean existsByTenantIdAndRuleCode(String tenantId, String ruleCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("ruleCode").is(ruleCode));
        return mongoTemplate.exists(query, PolicyRule.class);
    }

    @Override
    public long countByPolicyId(String policyId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("policyId").is(policyId));
        return mongoTemplate.count(query, PolicyRule.class);
    }
}
