package com.gogidix.rapidassist.dynamic.routing.config.service.adapters.infrastructure;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.out.RoutingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class MongoRoutingRepository implements RoutingRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoRoutingRepository.class);
    private static final String CACHE_PREFIX = "routing-rule:";
    private static final String TENANT_CACHE_PREFIX = "tenant-rules:";
    private static final long CACHE_TTL_MINUTES = 10;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public CompletableFuture<RoutingRule> save(RoutingRule rule) {
        return CompletableFuture.supplyAsync(() -> {
            RuleDocument doc = RuleDocument.fromDomain(rule);
            RuleDocument saved = mongoTemplate.save(doc);
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> findById(String ruleId) {
        return CompletableFuture.supplyAsync(() -> {
            RuleDocument doc = mongoTemplate.findById(ruleId, RuleDocument.class);
            return Optional.ofNullable(doc).map(RuleDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> findByNaturalKey(String tenantId, String ruleName, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("ruleName").is(ruleName));
            query.addCriteria(Criteria.where("environment").is(environment));
            RuleDocument doc = mongoTemplate.findOne(query, RuleDocument.class);
            return Optional.ofNullable(doc).map(RuleDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            return mongoTemplate.find(query, RuleDocument.class).stream()
                .map(RuleDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> findByTenantAndEnvironment(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("environment").is(environment));
            return mongoTemplate.find(query, RuleDocument.class).stream()
                .map(RuleDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> findActiveRules(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("environment").is(environment));
            query.addCriteria(Criteria.where("active").is(true));
            return mongoTemplate.find(query, RuleDocument.class).stream()
                .map(RuleDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> findByPriorityRange(String tenantId, int min, int max) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("priority").gte(min).lte(max));
            return mongoTemplate.find(query, RuleDocument.class).stream()
                .map(RuleDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String ruleId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(ruleId));
            return mongoTemplate.remove(query, RuleDocument.class).getDeletedCount() > 0;
        });
    }

    @Override
    public CompletableFuture<Void> cacheRule(RoutingRule rule) {
        return CompletableFuture.runAsync(() -> {
            String key = CACHE_PREFIX + rule.id();
            redisTemplate.opsForValue().set(key, rule, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        });
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> getCachedRule(String ruleId) {
        return CompletableFuture.supplyAsync(() -> {
            String key = CACHE_PREFIX + ruleId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof RoutingRule) {
                return Optional.of((RoutingRule) cached);
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> getCachedRules(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            String key = TENANT_CACHE_PREFIX + tenantId + ":" + environment;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof List) {
                return (List<RoutingRule>) cached;
            }
            return List.of();
        });
    }

    @Override
    public CompletableFuture<Void> evictRule(String ruleId) {
        return CompletableFuture.runAsync(() -> {
            String key = CACHE_PREFIX + ruleId;
            redisTemplate.delete(key);
        });
    }

    @Override
    public CompletableFuture<Void> evictAll(String tenantId) {
        return CompletableFuture.runAsync(() -> {
            Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
            Set<String> tenantKeys = redisTemplate.keys(TENANT_CACHE_PREFIX + tenantId + ":*");
            if (keys != null) redisTemplate.delete(keys);
            if (tenantKeys != null) redisTemplate.delete(tenantKeys);
        });
    }

    @org.springframework.data.mongodb.core.mapping.Document(collection = "routing_rules")
    public static class RuleDocument {
        private String id;
        private String tenantId;
        private String ruleName;
        private RoutingRule.RoutePattern pattern;
        private RoutingRule.RouteTarget target;
        private RoutingRule.RoutingStrategy strategy;
        private List<RoutingRule.Condition> conditions;
        private RoutingRule.RouteConfig config;
        private int priority;
        private boolean active;
        private String environment;
        private String createdBy;
        private java.time.Instant createdAt;
        private String updatedBy;
        private java.time.Instant updatedAt;
        private Integer version;

        public static RuleDocument fromDomain(RoutingRule rule) {
            RuleDocument doc = new RuleDocument();
            doc.id = rule.id();
            doc.tenantId = rule.tenantId();
            doc.ruleName = rule.ruleName();
            doc.pattern = rule.pattern();
            doc.target = rule.target();
            doc.strategy = rule.strategy();
            doc.conditions = rule.conditions();
            doc.config = rule.config();
            doc.priority = rule.priority();
            doc.active = rule.active();
            doc.environment = rule.environment();
            doc.createdBy = rule.createdBy();
            doc.createdAt = rule.createdAt();
            doc.updatedBy = rule.updatedBy();
            doc.updatedAt = rule.updatedAt();
            doc.version = rule.version();
            return doc;
        }

        public RoutingRule toDomain() {
            return RoutingRule.builder()
                .id(id)
                .tenantId(tenantId)
                .ruleName(ruleName)
                .pattern(pattern)
                .target(target)
                .strategy(strategy)
                .conditions(conditions)
                .config(config)
                .priority(priority)
                .active(active)
                .environment(environment)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedBy(updatedBy)
                .version(version)
                .build();
        }

        @org.springframework.data.annotation.Id
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public RoutingRule.RoutePattern getPattern() { return pattern; }
        public void setPattern(RoutingRule.RoutePattern pattern) { this.pattern = pattern; }
        public RoutingRule.RouteTarget getTarget() { return target; }
        public void setTarget(RoutingRule.RouteTarget target) { this.target = target; }
        public RoutingRule.RoutingStrategy getStrategy() { return strategy; }
        public void setStrategy(RoutingRule.RoutingStrategy strategy) { this.strategy = strategy; }
        public List<RoutingRule.Condition> getConditions() { return conditions; }
        public void setConditions(List<RoutingRule.Condition> conditions) { this.conditions = conditions; }
        public RoutingRule.RouteConfig getConfig() { return config; }
        public void setConfig(RoutingRule.RouteConfig config) { this.config = config; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public java.time.Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        public java.time.Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
    }
}
