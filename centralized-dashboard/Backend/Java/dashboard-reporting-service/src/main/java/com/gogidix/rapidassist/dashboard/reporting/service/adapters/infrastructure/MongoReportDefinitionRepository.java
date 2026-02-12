package com.gogidix.rapidassist.dashboard.reporting.service.adapters.infrastructure;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportDefinition;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.port.out.ReportDefinitionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class MongoReportDefinitionRepository implements ReportDefinitionRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoReportDefinitionRepository.class);
    private final MongoTemplate mongoTemplate;

    public MongoReportDefinitionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CompletableFuture<ReportDefinition> save(ReportDefinition definition) {
        return CompletableFuture.supplyAsync(() -> mongoTemplate.save(definition));
    }

    @Override
    public CompletableFuture<Optional<ReportDefinition>> findById(String reportId) {
        Query query = new Query(Criteria.where("reportId").is(reportId));
        ReportDefinition result = mongoTemplate.findOne(query, ReportDefinition.class);
        return CompletableFuture.completedFuture(Optional.ofNullable(result));
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> findByTenantId(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId))
            .with(Sort.by(Sort.Order.asc("name")));
        query.limit(100);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportDefinition.class));
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> findAll() {
        Query query = new Query()
            .with(Sort.by(Sort.Order.asc("tenantId"), Sort.Order.asc("name")));
        query.limit(500);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportDefinition.class));
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> findActiveByTenant(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId).and("active").is(true))
            .with(Sort.by(Sort.Order.asc("name")));
        query.limit(100);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportDefinition.class));
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> findByType(String tenantId, ReportDefinition.ReportType type) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId).and("reportType").is(type)
        ).with(Sort.by(Sort.Order.asc("name")));
        query.limit(100);
        return CompletableFuture.completedFuture(mongoTemplate.find(query, ReportDefinition.class));
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String reportId) {
        Query query = new Query(Criteria.where("reportId").is(reportId));
        return CompletableFuture.supplyAsync(() -> {
            var result = mongoTemplate.remove(query, ReportDefinition.class);
            return result.wasAcknowledged() && result.getDeletedCount() > 0;
        });
    }

    @Override
    public CompletableFuture<Boolean> existsById(String reportId) {
        return findById(reportId).thenApply(Optional::isPresent);
    }
}
