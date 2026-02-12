package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import com.gogidix.rapidassist.orchestration.dispatching.domain.port.out.DispatchRepositoryPort;
import com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence.DispatchRepository;
import com.gogidix.rapidassist.orchestration.dispatching.shared.requestcontext.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of DispatchRepositoryPort
 * Enforces tenant isolation on all queries
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MongoDispatchRepositoryImpl implements DispatchRepositoryPort {

    private final DispatchRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Dispatch save(Dispatch dispatch) {
        try {
            return mongoRepository.save(dispatch);
        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic lock failure for dispatch: {}", dispatch.getDispatchId());
            throw new RuntimeException("Concurrent modification detected", e);
        }
    }

    @Override
    public Optional<Dispatch> findById(String id) {
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("id").is(id)
                .and("tenantId").is(tenantId)
                .and("deletedAt").is(null)
        );

        return Optional.ofNullable(mongoTemplate.findOne(query, Dispatch.class));
    }

    @Override
    public List<Dispatch> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public Optional<Dispatch> findByDispatchId(String dispatchId) {
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("dispatchId").is(dispatchId)
                .and("tenantId").is(tenantId)
                .and("deletedAt").is(null)
        );

        return Optional.ofNullable(mongoTemplate.findOne(query, Dispatch.class));
    }

    @Override
    public List<Dispatch> findByRequestId(String requestId) {
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("requestId").is(requestId)
                .and("tenantId").is(tenantId)
                .and("deletedAt").is(null)
        );

        return mongoTemplate.find(query, Dispatch.class);
    }

    @Override
    public List<Dispatch> findByTenantIdAndStatusIn(String tenantId, List<Dispatch.DispatchStatus> statuses) {
        return mongoRepository.findByTenantIdAndStatusIn(tenantId, statuses);
    }

    @Override
    public void deleteById(String id) {
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("id").is(id)
                .and("tenantId").is(tenantId)
        );

        Dispatch dispatch = mongoTemplate.findOne(query, Dispatch.class);
        if (dispatch != null) {
            // Soft delete
            dispatch.setDeletedAt(java.time.LocalDateTime.now());
            mongoRepository.save(dispatch);
        }
    }

    @Override
    public boolean existsByDispatchId(String dispatchId) {
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("dispatchId").is(dispatchId)
                .and("tenantId").is(tenantId)
                .and("deletedAt").is(null)
        );

        return mongoTemplate.exists(query, Dispatch.class);
    }

    /**
     * Find dispatches with pagination
     */
    public Page<Dispatch> findByTenantId(String tenantId, Pageable pageable) {
        return mongoRepository.findAll(pageable);
    }
}
