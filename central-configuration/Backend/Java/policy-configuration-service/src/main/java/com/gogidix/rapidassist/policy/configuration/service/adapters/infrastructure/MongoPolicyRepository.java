package com.gogidix.rapidassist.policy.configuration.service.adapters.infrastructure;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;
import com.gogidix.rapidassist.policy.configuration.service.domain.port.out.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class MongoPolicyRepository implements PolicyRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoPolicyRepository.class);
    private static final String CACHE_PREFIX = "policy:";
    private static final long CACHE_TTL_MINUTES = 15;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public CompletableFuture<Policy> save(Policy policy) {
        return CompletableFuture.supplyAsync(() -> {
            PolicyDocument doc = PolicyDocument.fromDomain(policy);
            PolicyDocument saved = mongoTemplate.save(doc);
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<Policy>> findById(String policyId) {
        return CompletableFuture.supplyAsync(() -> {
            PolicyDocument doc = mongoTemplate.findById(policyId, PolicyDocument.class);
            return Optional.ofNullable(doc).map(PolicyDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<Policy>> findByKey(String tenantId, String policyKey) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("policyKey").is(policyKey));
            PolicyDocument doc = mongoTemplate.findOne(query, PolicyDocument.class);
            return Optional.ofNullable(doc).map(PolicyDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<Policy>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            return mongoTemplate.find(query, PolicyDocument.class).stream()
                .map(PolicyDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<Policy>> findByType(String tenantId, Policy.PolicyType type) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("type").is(type));
            return mongoTemplate.find(query, PolicyDocument.class).stream()
                .map(PolicyDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<Policy>> findActive(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("environment").is(environment));
            query.addCriteria(Criteria.where("status").is(Policy.PolicyStatus.ACTIVE));
            return mongoTemplate.find(query, PolicyDocument.class).stream()
                .map(PolicyDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<Policy>> findEnforced(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("enforced").is(true));
            return mongoTemplate.find(query, PolicyDocument.class).stream()
                .map(PolicyDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<Policy>> findByTags(String tenantId, Set<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("tags").in(tags));
            return mongoTemplate.find(query, PolicyDocument.class).stream()
                .map(PolicyDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String policyId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(policyId));
            return mongoTemplate.remove(query, PolicyDocument.class).getDeletedCount() > 0;
        });
    }

    @Override
    public CompletableFuture<Void> cachePolicy(Policy policy) {
        return CompletableFuture.runAsync(() -> {
            if (redisTemplate != null) {
                String key = CACHE_PREFIX + policy.id();
                redisTemplate.opsForValue().set(key, policy, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Policy>> getCachedPolicy(String policyId) {
        return CompletableFuture.supplyAsync(() -> {
            if (redisTemplate != null) {
                String key = CACHE_PREFIX + policyId;
                Object cached = redisTemplate.opsForValue().get(key);
                if (cached instanceof Policy) {
                    return Optional.of((Policy) cached);
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> evictPolicy(String policyId) {
        return CompletableFuture.runAsync(() -> {
            if (redisTemplate != null) {
                String key = CACHE_PREFIX + policyId;
                redisTemplate.delete(key);
            }
        });
    }

    @Override
    public CompletableFuture<Void> evictAll(String tenantId) {
        return CompletableFuture.runAsync(() -> {
            if (redisTemplate != null) {
                Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
                if (keys != null) redisTemplate.delete(keys);
            }
        });
    }

    @org.springframework.data.mongodb.core.mapping.Document(collection = "policies")
    public static class PolicyDocument {
        private String id;
        private String tenantId;
        private String policyKey;
        private String name;
        private String description;
        private Policy.PolicyType type;
        private Policy.PolicyScope scope;
        private java.util.Map<String, Object> rules;
        private Policy.PolicyConstraints constraints;
        private boolean enforced;
        private int priority;
        private String environment;
        private Policy.PolicyStatus status;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;
        private Set<String> tags;

        public static PolicyDocument fromDomain(Policy policy) {
            PolicyDocument doc = new PolicyDocument();
            doc.id = policy.id();
            doc.tenantId = policy.tenantId();
            doc.policyKey = policy.policyKey();
            doc.name = policy.name();
            doc.description = policy.description();
            doc.type = policy.type();
            doc.scope = policy.scope();
            doc.rules = policy.rules();
            doc.constraints = policy.constraints();
            doc.enforced = policy.enforced();
            doc.priority = policy.priority();
            doc.environment = policy.environment();
            doc.status = policy.status();
            doc.createdBy = policy.createdBy();
            doc.createdAt = policy.createdAt();
            doc.updatedBy = policy.updatedBy();
            doc.updatedAt = policy.updatedAt();
            doc.version = policy.version();
            doc.tags = policy.tags();
            return doc;
        }

        public Policy toDomain() {
            return Policy.builder()
                .id(id).tenantId(tenantId).policyKey(policyKey).name(name)
                .description(description).type(type).scope(scope).rules(rules)
                .constraints(constraints).enforced(enforced).priority(priority)
                .environment(environment).status(status).createdBy(createdBy)
                .createdAt(createdAt).updatedBy(updatedBy).version(version).tags(tags)
                .build();
        }

        @org.springframework.data.annotation.Id
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getPolicyKey() { return policyKey; }
        public void setPolicyKey(String policyKey) { this.policyKey = policyKey; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Policy.PolicyType getType() { return type; }
        public void setType(Policy.PolicyType type) { this.type = type; }
        public Policy.PolicyScope getScope() { return scope; }
        public void setScope(Policy.PolicyScope scope) { this.scope = scope; }
        public java.util.Map<String, Object> getRules() { return rules; }
        public void setRules(java.util.Map<String, Object> rules) { this.rules = rules; }
        public Policy.PolicyConstraints getConstraints() { return constraints; }
        public void setConstraints(Policy.PolicyConstraints constraints) { this.constraints = constraints; }
        public boolean isEnforced() { return enforced; }
        public void setEnforced(boolean enforced) { this.enforced = enforced; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public Policy.PolicyStatus getStatus() { return status; }
        public void setStatus(Policy.PolicyStatus status) { this.status = status; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
        public Set<String> getTags() { return tags; }
        public void setTags(Set<String> tags) { this.tags = tags; }
    }
}
