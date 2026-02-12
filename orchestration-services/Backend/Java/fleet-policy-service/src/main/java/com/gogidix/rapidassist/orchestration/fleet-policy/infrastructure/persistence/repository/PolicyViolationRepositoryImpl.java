package com.gogidix.rapidassist.orchestration.fleet_policy.infrastructure.persistence.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository.PolicyViolationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * MongoDB implementation of PolicyViolation repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PolicyViolationRepositoryImpl implements PolicyViolationRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PolicyViolation save(PolicyViolation violation) {
        return mongoTemplate.save(violation);
    }

    @Override
    public Optional<PolicyViolation> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PolicyViolation.class));
    }

    @Override
    public List<PolicyViolation> findByPolicyId(String policyId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("policyId").is(policyId));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findByRuleId(String ruleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("ruleId").is(ruleId));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findByEntityTypeAndEntityId(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityType").is(entityType));
        query.addCriteria(Criteria.where("entityId").is(entityId));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findByTenantId(String tenantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findByTenantIdAndStatus(
            String tenantId,
            PolicyViolation.ViolationStatus status
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("status").is(status));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findByTenantIdAndSeverity(
            String tenantId,
            PolicyViolation.ViolationSeverity severity
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("severity").is(severity));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findOpenViolationsByEntity(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityType").is(entityType));
        query.addCriteria(Criteria.where("entityId").is(entityId));
        query.addCriteria(Criteria.where("status").in(
                PolicyViolation.ViolationStatus.OPEN,
                PolicyViolation.ViolationStatus.ACKNOWLEDGED,
                PolicyViolation.ViolationStatus.IN_PROGRESS
        ));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findByTenantIdAndDetectedAtBetween(
            String tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("detectedAt").gte(startDate).lte(endDate));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findOverdueViolations(String tenantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("actionDueBy").lt(LocalDateTime.now()));
        query.addCriteria(Criteria.where("status").in(
                PolicyViolation.ViolationStatus.OPEN,
                PolicyViolation.ViolationStatus.ACKNOWLEDGED
        ));
        query.with(Sort.by(Sort.Direction.ASC, "actionDueBy"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public List<PolicyViolation> findViolationsRequiringEscalation(String tenantId) {
        List<PolicyViolation> allViolations = findByTenantId(tenantId);
        return allViolations.stream()
                .filter(PolicyViolation::requiresEscalation)
                .toList();
    }

    @Override
    public List<PolicyViolation> searchByEntityName(String tenantId, String entityName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("entityName").regex(entityName, "i"));
        query.with(Sort.by(Sort.Direction.DESC, "detectedAt"));
        return mongoTemplate.find(query, PolicyViolation.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), PolicyViolation.class);
    }

    @Override
    public long countByEntityTypeAndEntityId(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("entityType").is(entityType));
        query.addCriteria(Criteria.where("entityId").is(entityId));
        return mongoTemplate.count(query, PolicyViolation.class);
    }

    @Override
    public long countByTenantIdAndStatus(
            String tenantId,
            PolicyViolation.ViolationStatus status
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("status").is(status));
        return mongoTemplate.count(query, PolicyViolation.class);
    }

    @Override
    public Integer calculateTotalPoints(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    ) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("entityType").is(entityType)
                        .and("entityId").is(entityId)),
                group().sum("points").as("totalPoints")
        );

        AggregationResults<Result> results = mongoTemplate.aggregate(
                aggregation,
                PolicyViolation.class,
                Result.class
        );

        return results.getUniqueMappedResult() != null ?
                results.getUniqueMappedResult().getTotalPoints() : 0;
    }

    private static class Result {
        private Integer totalPoints;

        public Integer getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(Integer totalPoints) {
            this.totalPoints = totalPoints;
        }
    }
}
