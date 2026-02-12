package com.gogidix.rapidassist.orchestration.matching.infrastructure.mongodb.repository;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingRequestRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MatchingRequestRepositoryImpl implements MatchingRequestRepositoryPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public MatchingRequest save(MatchingRequest request) {
        return mongoTemplate.save(request);
    }

    @Override
    public Optional<MatchingRequest> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MatchingRequest.class));
    }

    @Override
    public Optional<MatchingRequest> findByRequestId(String requestId) {
        Query query = new Query(Criteria.where("requestId").is(requestId));
        return Optional.ofNullable(mongoTemplate.findOne(query, MatchingRequest.class));
    }

    @Override
    public List<MatchingRequest> findByTenantId(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, MatchingRequest.class);
    }

    @Override
    public List<MatchingRequest> findByStatus(MatchingRequest.RequestStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, MatchingRequest.class);
    }

    @Override
    public List<MatchingRequest> findByIncidentId(String incidentId) {
        Query query = new Query(Criteria.where("incidentId").is(incidentId));
        return mongoTemplate.find(query, MatchingRequest.class);
    }

    @Override
    public List<MatchingRequest> findExpiredRequests(LocalDateTime currentTime) {
        Query query = new Query(Criteria.where("expiresAt").lt(currentTime)
            .and("status").in(MatchingRequest.RequestStatus.PENDING,
                             MatchingRequest.RequestStatus.PROCESSING));
        return mongoTemplate.find(query, MatchingRequest.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), MatchingRequest.class);
    }

    @Override
    public void deleteByRequestId(String requestId) {
        mongoTemplate.remove(Query.query(Criteria.where("requestId").is(requestId)), MatchingRequest.class);
    }

    @Override
    public boolean existsByRequestId(String requestId) {
        Query query = new Query(Criteria.where("requestId").is(requestId));
        return mongoTemplate.exists(query, MatchingRequest.class);
    }

    @Override
    public List<MatchingRequest> findByTenantIdAndStatus(
        String tenantId,
        MatchingRequest.RequestStatus status
    ) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
            .and("status").is(status));
        return mongoTemplate.find(query, MatchingRequest.class);
    }
}
