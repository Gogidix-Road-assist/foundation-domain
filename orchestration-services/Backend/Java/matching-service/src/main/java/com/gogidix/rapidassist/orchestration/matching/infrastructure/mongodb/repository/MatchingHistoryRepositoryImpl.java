package com.gogidix.rapidassist.orchestration.matching.infrastructure.mongodb.repository;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingHistory;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingHistoryRepositoryPort;
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
public class MatchingHistoryRepositoryImpl implements MatchingHistoryRepositoryPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public MatchingHistory save(MatchingHistory history) {
        return mongoTemplate.save(history);
    }

    @Override
    public Optional<MatchingHistory> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MatchingHistory.class));
    }

    @Override
    public Optional<MatchingHistory> findByRequestId(String requestId) {
        Query query = new Query(Criteria.where("requestId").is(requestId));
        return Optional.ofNullable(mongoTemplate.findOne(query, MatchingHistory.class));
    }

    @Override
    public List<MatchingHistory> findByTenantId(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, MatchingHistory.class);
    }

    @Override
    public List<MatchingHistory> findByProviderId(String providerId) {
        Query query = new Query(Criteria.where("providerId").is(providerId));
        return mongoTemplate.find(query, MatchingHistory.class);
    }

    @Override
    public List<MatchingHistory> findByIncidentId(String incidentId) {
        Query query = new Query(Criteria.where("incidentId").is(incidentId));
        return mongoTemplate.find(query, MatchingHistory.class);
    }

    @Override
    public List<MatchingHistory> findByDecisionStatus(MatchingHistory.DecisionStatus status) {
        Query query = new Query(Criteria.where("decisionStatus").is(status));
        return mongoTemplate.find(query, MatchingHistory.class);
    }

    @Override
    public List<MatchingHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query(Criteria.where("createdAt").gte(startDate).lte(endDate));
        return mongoTemplate.find(query, MatchingHistory.class);
    }

    @Override
    public List<MatchingHistory> findByProviderIdAndCreatedAtBetween(
        String providerId,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        Query query = new Query(Criteria.where("providerId").is(providerId)
            .and("createdAt").gte(startDate).lte(endDate));
        return mongoTemplate.find(query, MatchingHistory.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), MatchingHistory.class);
    }

    @Override
    public List<MatchingHistory> findByTenantIdAndCreatedAtAfter(String tenantId, LocalDateTime createdAt) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
            .and("createdAt").gte(createdAt));
        return mongoTemplate.find(query, MatchingHistory.class);
    }
}
