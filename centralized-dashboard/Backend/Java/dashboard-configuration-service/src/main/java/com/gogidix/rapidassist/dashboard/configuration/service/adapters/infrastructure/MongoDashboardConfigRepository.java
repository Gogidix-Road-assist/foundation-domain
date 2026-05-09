package com.gogidix.rapidassist.dashboard.configuration.service.adapters.infrastructure;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.port.out.DashboardConfigRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Repository
public class MongoDashboardConfigRepository implements DashboardConfigRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoDashboardConfigRepository.class);
    private static final String CACHE_PREFIX = "dashboard:config:";
    private static final long CACHE_TTL_HOURS = 1;

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public MongoDashboardConfigRepository(MongoTemplate mongoTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CompletableFuture<DashboardConfiguration> save(DashboardConfiguration dashboard) {
        return CompletableFuture.runAsync(() -> {
            mongoTemplate.save(dashboard);
            invalidateCache(dashboard.dashboardId());
            invalidateCacheList(dashboard.tenantId());
        }).thenApply(v -> dashboard);
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> findById(String dashboardId) {
        String cacheKey = CACHE_PREFIX + dashboardId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return CompletableFuture.completedFuture(Optional.of((DashboardConfiguration) cached));
        }

        Query query = new Query(Criteria.where("dashboardId").is(dashboardId));
        DashboardConfiguration result = mongoTemplate.findOne(query, DashboardConfiguration.class);

        if (result != null) {
            redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_HOURS, TimeUnit.HOURS);
        }

        return CompletableFuture.completedFuture(Optional.ofNullable(result));
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> findByTenantId(String tenantId) {
        String cacheKey = CACHE_PREFIX + "tenant:" + tenantId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return CompletableFuture.completedFuture((List<DashboardConfiguration>) cached);
        }

        Query query = new Query(Criteria.where("tenantId").is(tenantId))
            .with(Sort.by(Sort.Order.asc("name")));
        query.limit(100);

        List<DashboardConfiguration> results = mongoTemplate.find(query, DashboardConfiguration.class);

        redisTemplate.opsForValue().set(cacheKey, results, CACHE_TTL_HOURS, TimeUnit.HOURS);

        return CompletableFuture.completedFuture(results);
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> findAll() {
        Query query = new Query()
            .with(Sort.by(Sort.Order.asc("tenantId"), Sort.Order.asc("name")));
        query.limit(500);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardConfiguration.class));
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> findByCategory(String category) {
        Query query = new Query(Criteria.where("metadata.category").is(category))
            .with(Sort.by(Sort.Order.asc("name")));
        query.limit(100);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardConfiguration.class));
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> findActiveByTenant(String tenantId) {
        String cacheKey = CACHE_PREFIX + "tenant:" + tenantId + ":active";

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return CompletableFuture.completedFuture((List<DashboardConfiguration>) cached);
        }

        Query query = new Query(Criteria.where("tenantId").is(tenantId).and("active").is(true))
            .with(Sort.by(Sort.Order.asc("name")));
        query.limit(100);

        List<DashboardConfiguration> results = mongoTemplate.find(query, DashboardConfiguration.class);

        redisTemplate.opsForValue().set(cacheKey, results, CACHE_TTL_HOURS, TimeUnit.HOURS);

        return CompletableFuture.completedFuture(results);
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> searchByName(String tenantId, String keyword) {
        Query query = new Query(
            Criteria.where("tenantId").is(tenantId)
                .and("name").regex(keyword, "i")
        )
            .with(Sort.by(Sort.Order.asc("name")));
        query.limit(50);

        return CompletableFuture.completedFuture(mongoTemplate.find(query, DashboardConfiguration.class));
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String dashboardId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query(Criteria.where("dashboardId").is(dashboardId));
            DashboardConfiguration deleted = mongoTemplate.findAndRemove(query, DashboardConfiguration.class);

            if (deleted != null) {
                invalidateCache(dashboardId);
                invalidateCacheList(deleted.tenantId());
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> existsById(String dashboardId) {
        return findById(dashboardId).thenApply(Optional::isPresent);
    }

    private void invalidateCache(String dashboardId) {
        try {
            redisTemplate.delete(CACHE_PREFIX + dashboardId);
        } catch (Exception e) {
            logger.warn("Failed to invalidate cache for dashboard: {}", dashboardId, e);
        }
    }

    private void invalidateCacheList(String tenantId) {
        try {
            redisTemplate.delete(CACHE_PREFIX + "tenant:" + tenantId);
            redisTemplate.delete(CACHE_PREFIX + "tenant:" + tenantId + ":active");
        } catch (Exception e) {
            logger.warn("Failed to invalidate cache list for tenant: {}", tenantId, e);
        }
    }
}
