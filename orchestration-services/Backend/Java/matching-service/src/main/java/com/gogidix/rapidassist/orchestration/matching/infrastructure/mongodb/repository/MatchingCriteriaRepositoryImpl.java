package com.gogidix.rapidassist.orchestration.matching.infrastructure.mongodb.repository;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingCriteriaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MatchingCriteriaRepositoryImpl implements MatchingCriteriaRepositoryPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public MatchingCriteria save(MatchingCriteria criteria) {
        return mongoTemplate.save(criteria);
    }

    @Override
    public Optional<MatchingCriteria> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MatchingCriteria.class));
    }

    @Override
    public Optional<MatchingCriteria> findByCriteriaId(String criteriaId) {
        Query query = new Query(Criteria.where("criteriaId").is(criteriaId));
        return Optional.ofNullable(mongoTemplate.findOne(query, MatchingCriteria.class));
    }

    @Override
    public List<MatchingCriteria> findByTenantId(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, MatchingCriteria.class);
    }

    @Override
    public List<MatchingCriteria> findByTenantIdAndIsActive(String tenantId, Boolean isActive) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
            .and("isActive").is(isActive));
        return mongoTemplate.find(query, MatchingCriteria.class);
    }

    @Override
    public List<MatchingCriteria> findByServiceType(String serviceType) {
        Query query = new Query(Criteria.where("serviceType").is(serviceType));
        return mongoTemplate.find(query, MatchingCriteria.class);
    }

    @Override
    public List<MatchingCriteria> findValidCriteria(String tenantId, LocalDateTime currentTime) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
            .and("isActive").is(true)
            .orOperator(
                Criteria.where("validFrom").isNull(),
                Criteria.where("validFrom").lte(currentTime)
            )
            .orOperator(
                Criteria.where("validTo").isNull(),
                Criteria.where("validTo").gte(currentTime)
            ));
        return mongoTemplate.find(query, MatchingCriteria.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), MatchingCriteria.class);
    }

    @Override
    public void deleteByCriteriaId(String criteriaId) {
        mongoTemplate.remove(Query.query(Criteria.where("criteriaId").is(criteriaId)), MatchingCriteria.class);
    }

    @Override
    public boolean existsByCriteriaId(String criteriaId) {
        Query query = new Query(Criteria.where("criteriaId").is(criteriaId));
        return mongoTemplate.exists(query, MatchingCriteria.class);
    }
}
