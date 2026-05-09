package com.gogidix.rapidassist.dashboard.analytics.service.adapters.infrastructure;

import com.gogidix.rapidassist.dashboard.analytics.service.domain.model.DashboardAnalytics;
import com.gogidix.rapidassist.dashboard.analytics.service.domain.port.out.AnalyticsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class MongoAnalyticsRepository implements AnalyticsRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoAnalyticsRepository.class);
    private final MongoTemplate mongoTemplate;

    public MongoAnalyticsRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CompletableFuture<DashboardAnalytics> save(DashboardAnalytics analytics) {
        return CompletableFuture.supplyAsync(() -> mongoTemplate.save(analytics));
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> saveAll(List<DashboardAnalytics> analytics) {
        return CompletableFuture.supplyAsync(() -> {
            return analytics.stream()
                .map(mongoTemplate::save)
                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> findByDashboardIdAndDateRange(
        String dashboardId, Instant startDate, Instant endDate) {

        Query query = new Query(
            Criteria.where("dashboardId").is(dashboardId)
                .and("timestamp").gte(startDate).lte(endDate)
        ).with(Sort.by(Sort.Order.desc("timestamp")));
        query.limit(1000);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardAnalytics.class));
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> findByTenantIdAndUserIdAndDateRange(
        String tenantId, String userId, Instant startDate, Instant endDate) {

        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("userId").is(userId)
                .and("timestamp").gte(startDate).lte(endDate)
        ).with(Sort.by(Sort.Order.desc("timestamp")));
        query.limit(500);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardAnalytics.class));
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> findByTenantIdAndEventTypeAndDateRange(
        String tenantId, DashboardAnalytics.AnalyticsEventType eventType, Instant startDate, Instant endDate) {

        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("eventType").is(eventType)
                .and("timestamp").gte(startDate).lte(endDate)
        ).with(Sort.by(Sort.Order.desc("timestamp")));
        query.limit(1000);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardAnalytics.class));
    }

    @Override
    public CompletableFuture<Map<String, Long>> countViewsByDashboard(String tenantId, Instant startDate, Instant endDate) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("eventType").is(DashboardAnalytics.AnalyticsEventType.DASHBOARD_VIEW)
                .and("timestamp").gte(startDate).lte(endDate)
        );

        List<DashboardAnalytics> events = mongoTemplate.find(query, DashboardAnalytics.class);

        Map<String, Long> counts = events.stream()
            .collect(Collectors.groupingBy(
                DashboardAnalytics::dashboardId,
                Collectors.counting()
            ));

        return CompletableFuture.completedFuture(counts);
    }

    @Override
    public CompletableFuture<Map<String, Long>> countByUserId(String tenantId, Instant startDate, Instant endDate) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("timestamp").gte(startDate).lte(endDate)
        );

        List<DashboardAnalytics> events = mongoTemplate.find(query, DashboardAnalytics.class);

        Map<String, Long> counts = events.stream()
            .collect(Collectors.groupingBy(
                DashboardAnalytics::userId,
                Collectors.counting()
            ));

        return CompletableFuture.completedFuture(counts);
    }

    @Override
    public CompletableFuture<List<DashboardAnalytics>> findSlowLoadingEvents(String tenantId, long thresholdMs, int limit) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("performanceMetrics.loadTimeMs").gt(thresholdMs)
        ).with(Sort.by(Sort.Order.desc("performanceMetrics.loadTimeMs")));
        query.limit(limit);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardAnalytics.class));
    }

    @Override
    public CompletableFuture<Boolean> deleteByTenantIdAndTimestampBefore(String tenantId, Instant timestamp) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("timestamp").lt(timestamp)
        );

        return CompletableFuture.supplyAsync(() -> {
            var result = mongoTemplate.remove(query, DashboardAnalytics.class);
            return result.wasAcknowledged();
        });
    }
}
