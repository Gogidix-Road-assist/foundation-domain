package com.gogidix.rapidassist.orchestration.matching.infrastructure.mongodb.repository;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingResultRepositoryPort;
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
public class MatchingResultRepositoryImpl implements MatchingResultRepositoryPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public MatchingResult save(MatchingResult result) {
        return mongoTemplate.save(result);
    }

    @Override
    public Optional<MatchingResult> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MatchingResult.class));
    }

    @Override
    public Optional<MatchingResult> findByRequestId(String requestId) {
        Query query = new Query(Criteria.where("requestId").is(requestId));
        return Optional.ofNullable(mongoTemplate.findOne(query, MatchingResult.class));
    }

    @Override
    public List<MatchingResult> findByTenantId(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, MatchingResult.class);
    }

    @Override
    public List<MatchingResult> findByIncidentId(String incidentId) {
        Query query = new Query(Criteria.where("incidentId").is(incidentId));
        return mongoTemplate.find(query, MatchingResult.class);
    }

    @Override
    public List<MatchingResult> findByStatus(MatchingResult.ResultStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, MatchingResult.class);
    }

    @Override
    public List<MatchingResult> findExpiredResults(LocalDateTime currentTime) {
        Query query = new Query(Criteria.where("expiresAt").lt(currentTime)
            .and("isExpired").is(false));
        return mongoTemplate.find(query, MatchingResult.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), MatchingResult.class);
    }

    @Override
    public void deleteByRequestId(String requestId) {
        mongoTemplate.remove(Query.query(Criteria.where("requestId").is(requestId)), MatchingResult.class);
    }

    @Override
    public List<MatchingResult> findByProviderId(String providerId) {
        Query query = new Query(Criteria.where("topProvider.providerId").is(providerId));
        return mongoTemplate.find(query, MatchingResult.class);
    }

    @Override
    public List<MatchingResult> findByTenantIdAndCreatedAtAfter(
        String tenantId,
        LocalDateTime createdAt
    ) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
            .and("createdAt").gte(createdAt));
        return mongoTemplate.find(query, MatchingResult.class);
    }
}
