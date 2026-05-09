package com.gogidix.rapidassist.dashboard.reporting.service.adapters.infrastructure;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportExecution;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.port.out.ReportExecutionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class MongoReportExecutionRepository implements ReportExecutionRepository {

    private final MongoTemplate mongoTemplate;

    public MongoReportExecutionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CompletableFuture<ReportExecution> save(ReportExecution execution) {
        return CompletableFuture.supplyAsync(() -> mongoTemplate.save(execution));
    }

    @Override
    public CompletableFuture<Optional<ReportExecution>> findById(String executionId) {
        Query query = new Query(Criteria.where("executionId").is(executionId));
        ReportExecution result = mongoTemplate.findOne(query, ReportExecution.class);
        return CompletableFuture.completedFuture(Optional.ofNullable(result));
    }

    @Override
    public CompletableFuture<List<ReportExecution>> findByReportId(String reportId, int limit) {
        Query query = new Query(Criteria.where("reportId").is(reportId))
            .with(Sort.by(Sort.Order.desc("requestedAt")));
        query.limit(limit);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportExecution.class));
    }

    @Override
    public CompletableFuture<List<ReportExecution>> findByTenantIdAndStatus(String tenantId, ReportExecution.ExecutionStatus status) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId).and("status").is(status)
        ).with(Sort.by(Sort.Order.desc("requestedAt")));
        query.limit(100);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportExecution.class));
    }

    @Override
    public CompletableFuture<List<ReportExecution>> findByTenantIdAndRequestedBy(String tenantId, String requestedBy) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId).and("requestedBy").is(requestedBy)
        ).with(Sort.by(Sort.Order.desc("requestedAt")));
        query.limit(100);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportExecution.class));
    }

    @Override
    public CompletableFuture<List<ReportExecution>> findByTenantId(String tenantId, int limit) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId))
            .with(Sort.by(Sort.Order.desc("requestedAt")));
        query.limit(limit);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportExecution.class));
    }
}
