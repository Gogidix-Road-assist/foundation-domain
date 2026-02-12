package com.gogidix.rapidassist.orchestration.fleet_policy.infrastructure.persistence.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyCompliance;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyComplianceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * MongoDB implementation of PolicyCompliance repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PolicyComplianceRepositoryImpl implements PolicyComplianceRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PolicyCompliance save(PolicyCompliance compliance) {
        return mongoTemplate.save(compliance);
    }

    @Override
    public Optional<PolicyCompliance> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PolicyCompliance.class));
    }

    @Override
    public Optional<PolicyCompliance> findByPolicyIdAndEntityTypeAndEntityId(
            String policyId,
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("policyId").is(policyId));
        query.addCriteria(Criteria.where("entityType").is(entityType));
        query.addCriteria(Criteria.where("entityId").is(entityId));
        query.with(Sort.by(Sort.Direction.DESC, "complianceDate"));
        return Optional.ofNullable(mongoTemplate.findOne(query, PolicyCompliance.class));
    }

    @Override
    public List<PolicyCompliance> findByPolicyIdAndComplianceDate(String policyId, LocalDate complianceDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("policyId").is(policyId));
        query.addCriteria(Criteria.where("complianceDate").is(complianceDate));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public List<PolicyCompliance> findByEntityTypeAndEntityId(
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityType").is(entityType));
        query.addCriteria(Criteria.where("entityId").is(entityId));
        query.with(Sort.by(Sort.Direction.DESC, "complianceDate"));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public List<PolicyCompliance> findByTenantId(String tenantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public List<PolicyCompliance> findByTenantIdAndStatus(
            String tenantId,
            PolicyCompliance.ComplianceStatus status
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("status").is(status));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public List<PolicyCompliance> findByTenantIdAndRequiresAction(
            String tenantId,
            Boolean requiresAction
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("requiresAction").is(requiresAction));
        query.addCriteria(Criteria.where("actionCompleted").is(false));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public List<PolicyCompliance> findOverdueActions(String tenantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("requiresAction").is(true));
        query.addCriteria(Criteria.where("actionCompleted").is(false));
        query.addCriteria(Criteria.where("actionDueBy").lt(LocalDateTime.now()));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public List<PolicyCompliance> findByTenantIdAndComplianceDateBetween(
            String tenantId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("complianceDate").gte(startDate).lte(endDate));
        return mongoTemplate.find(query, PolicyCompliance.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), PolicyCompliance.class);
    }

    @Override
    public Double calculateAverageComplianceScore(
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    ) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("entityType").is(entityType)
                        .and("entityId").is(entityId)),
                group().avg("complianceScore").as("averageScore")
        );

        AggregationResults<Result> results = mongoTemplate.aggregate(
                aggregation,
                PolicyCompliance.class,
                Result.class
        );

        return results.getUniqueMappedResult() != null ?
                results.getUniqueMappedResult().getAverageScore() : 0.0;
    }

    @Override
    public long countByTenantIdAndStatus(
            String tenantId,
            PolicyCompliance.ComplianceStatus status
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("status").is(status));
        return mongoTemplate.count(query, PolicyCompliance.class);
    }

    private static class Result {
        private Double averageScore;

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }
    }
}
