package com.gogidix.rapidassist.feature.flags.service.adapters.infrastructure;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagChange;
import com.gogidix.rapidassist.feature.flags.service.domain.port.out.FeatureFlagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * MongoDB implementation of feature flag repository with Redis caching
 */
@Component
public class MongoFeatureFlagRepository implements FeatureFlagRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoFeatureFlagRepository.class);
    private static final String CACHE_KEY_PREFIX = "feature-flag:";
    private static final long CACHE_TTL_MINUTES = 5;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public CompletableFuture<FeatureFlag> save(FeatureFlag flag) {
        return CompletableFuture.supplyAsync(() -> {
            FeatureFlagDocument doc = FeatureFlagDocument.fromDomain(flag);
            FeatureFlagDocument saved = mongoTemplate.save(doc);
            logger.debug("Saved feature flag: {}", saved.getKey());
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> findById(String flagId) {
        return CompletableFuture.supplyAsync(() -> {
            FeatureFlagDocument doc = mongoTemplate.findById(flagId, FeatureFlagDocument.class);
            return Optional.ofNullable(doc).map(FeatureFlagDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> findByKey(String tenantId, String key, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("key").is(key));
            query.addCriteria(Criteria.where("environment").is(environment));
            query.addCriteria(Criteria.where("status").ne(FeatureFlag.FlagStatus.ARCHIVED));

            FeatureFlagDocument doc = mongoTemplate.findOne(query, FeatureFlagDocument.class);
            return Optional.ofNullable(doc).map(FeatureFlagDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("status").ne(FeatureFlag.FlagStatus.ARCHIVED));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findByTenantAndEnvironment(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("environment").is(environment));
            query.addCriteria(Criteria.where("status").ne(FeatureFlag.FlagStatus.ARCHIVED));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findByStatus(String tenantId, FeatureFlag.FlagStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("status").is(status));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findByTags(String tenantId, Set<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("tags").in(tags));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> searchByKeyword(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(new Criteria().orOperator(
                Criteria.where("key").regex(keyword, "i"),
                Criteria.where("name").regex(keyword, "i"),
                Criteria.where("description").regex(keyword, "i")
            ));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findCreatedSince(String tenantId, Instant since) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("createdAt").gte(since));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findExpiringBefore(String tenantId, Instant before) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("expiresAt").lt(before));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> findAllActive(String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("environment").is(environment));
            query.addCriteria(Criteria.where("status").is(FeatureFlag.FlagStatus.ACTIVE));
            return mongoTemplate.find(query, FeatureFlagDocument.class).stream()
                .map(FeatureFlagDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String flagId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(flagId));
            return mongoTemplate.remove(query, FeatureFlagDocument.class).getDeletedCount() > 0;
        });
    }

    // Change tracking

    @Override
    public CompletableFuture<FeatureFlagChange> saveChange(FeatureFlagChange change) {
        return CompletableFuture.supplyAsync(() -> {
            FeatureFlagChangeDocument doc = FeatureFlagChangeDocument.fromDomain(change);
            FeatureFlagChangeDocument saved = mongoTemplate.save(doc);
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlagChange>> findChangesByFlagId(String flagId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("flagId").is(flagId));
            query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Order.desc("changedAt")));
            query.limit(100);
            return mongoTemplate.find(query, FeatureFlagChangeDocument.class).stream()
                .map(FeatureFlagChangeDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlagChange>> findChangesByTimeRange(String tenantId, Instant from, Instant to) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("changedAt").gte(from).lte(to));
            return mongoTemplate.find(query, FeatureFlagChangeDocument.class).stream()
                .map(FeatureFlagChangeDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlagChange>> findPendingApprovals(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("approvalStatus").is(FeatureFlagChange.ApprovalStatus.PENDING));
            return mongoTemplate.find(query, FeatureFlagChangeDocument.class).stream()
                .map(FeatureFlagChangeDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlagChange>> findChangeById(String changeId) {
        return CompletableFuture.supplyAsync(() -> {
            FeatureFlagChangeDocument doc = mongoTemplate.findById(changeId, FeatureFlagChangeDocument.class);
            return Optional.ofNullable(doc).map(FeatureFlagChangeDocument::toDomain);
        });
    }

    // Cache operations

    @Override
    public CompletableFuture<Void> cacheFlag(FeatureFlag flag) {
        return CompletableFuture.runAsync(() -> {
            String key = buildCacheKey(flag.tenantId(), flag.key(), flag.environment());
            redisTemplate.opsForValue().set(key, flag, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> getCachedFlag(String tenantId, String key, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = buildCacheKey(tenantId, key, environment);
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof FeatureFlag) {
                return Optional.of((FeatureFlag) cached);
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> evictFlag(String tenantId, String key, String environment) {
        return CompletableFuture.runAsync(() -> {
            String cacheKey = buildCacheKey(tenantId, key, environment);
            redisTemplate.delete(cacheKey);
        });
    }

    @Override
    public CompletableFuture<Void> evictAll(String tenantId) {
        return CompletableFuture.runAsync(() -> {
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + tenantId + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        });
    }

    private String buildCacheKey(String tenantId, String key, String environment) {
        return CACHE_KEY_PREFIX + tenantId + ":" + environment + ":" + key;
    }

    // MongoDB Documents

    @org.springframework.data.mongodb.core.mapping.Document(collection = "feature_flags")
    public static class FeatureFlagDocument {
        private String id;
        private String tenantId;
        private String key;
        private String name;
        private String description;
        private boolean enabled;
        private FeatureFlag.FlagType type;
        private FeatureFlag.RolloutStrategy rolloutStrategy;
        private Set<String> allowedTenants;
        private Set<String> allowedUsers;
        private Set<String> allowedCountries;
        private FeatureFlag.PercentageRollout percentageRollout;
        private String environment;
        private FeatureFlag.FlagStatus status;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;
        private Set<String> tags;
        private Boolean requiresApproval;
        private Instant expiresAt;

        public static FeatureFlagDocument fromDomain(FeatureFlag flag) {
            FeatureFlagDocument doc = new FeatureFlagDocument();
            doc.id = flag.id();
            doc.tenantId = flag.tenantId();
            doc.key = flag.key();
            doc.name = flag.name();
            doc.description = flag.description();
            doc.enabled = flag.enabled();
            doc.type = flag.type();
            doc.rolloutStrategy = flag.rolloutStrategy();
            doc.allowedTenants = flag.allowedTenants();
            doc.allowedUsers = flag.allowedUsers();
            doc.allowedCountries = flag.allowedCountries();
            doc.percentageRollout = flag.percentageRollout();
            doc.environment = flag.environment();
            doc.status = flag.status();
            doc.createdBy = flag.createdBy();
            doc.createdAt = flag.createdAt();
            doc.updatedBy = flag.updatedBy();
            doc.updatedAt = flag.updatedAt();
            doc.version = flag.version();
            doc.tags = flag.tags();
            doc.requiresApproval = flag.requiresApproval();
            doc.expiresAt = flag.expiresAt();
            return doc;
        }

        public FeatureFlag toDomain() {
            return FeatureFlag.builder()
                .id(id)
                .tenantId(tenantId)
                .key(key)
                .name(name)
                .description(description)
                .enabled(enabled)
                .type(type)
                .rolloutStrategy(rolloutStrategy)
                .allowedTenants(allowedTenants != null ? allowedTenants : Set.of())
                .allowedUsers(allowedUsers != null ? allowedUsers : Set.of())
                .allowedCountries(allowedCountries != null ? allowedCountries : Set.of())
                .percentageRollout(percentageRollout)
                .environment(environment)
                .status(status)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedBy(updatedBy)
                .updatedAt(updatedAt)
                .version(version)
                .tags(tags != null ? tags : Set.of())
                .requiresApproval(requiresApproval)
                .expiresAt(expiresAt)
                .build();
        }

        // Getters and setters for MongoDB
        @org.springframework.data.annotation.Id
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public FeatureFlag.FlagType getType() { return type; }
        public void setType(FeatureFlag.FlagType type) { this.type = type; }
        public FeatureFlag.RolloutStrategy getRolloutStrategy() { return rolloutStrategy; }
        public void setRolloutStrategy(FeatureFlag.RolloutStrategy rolloutStrategy) { this.rolloutStrategy = rolloutStrategy; }
        public Set<String> getAllowedTenants() { return allowedTenants; }
        public void setAllowedTenants(Set<String> allowedTenants) { this.allowedTenants = allowedTenants; }
        public Set<String> getAllowedUsers() { return allowedUsers; }
        public void setAllowedUsers(Set<String> allowedUsers) { this.allowedUsers = allowedUsers; }
        public Set<String> getAllowedCountries() { return allowedCountries; }
        public void setAllowedCountries(Set<String> allowedCountries) { this.allowedCountries = allowedCountries; }
        public FeatureFlag.PercentageRollout getPercentageRollout() { return percentageRollout; }
        public void setPercentageRollout(FeatureFlag.PercentageRollout percentageRollout) { this.percentageRollout = percentageRollout; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public FeatureFlag.FlagStatus getStatus() { return status; }
        public void setStatus(FeatureFlag.FlagStatus status) { this.status = status; }
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
        public Boolean getRequiresApproval() { return requiresApproval; }
        public void setRequiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; }
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    }

    @org.springframework.data.mongodb.core.mapping.Document(collection = "feature_flag_changes")
    public static class FeatureFlagChangeDocument {
        private String id;
        private String flagId;
        private String flagKey;
        private String tenantId;
        private FeatureFlagChange.ChangeType changeType;
        private Integer fromVersion;
        private Integer toVersion;
        private Boolean previousEnabled;
        private Boolean newEnabled;
        private String changedBy;
        private String reason;
        private Instant changedAt;
        private FeatureFlagChange.ApprovalStatus approvalStatus;
        private String approvedBy;
        private Instant approvedAt;

        public static FeatureFlagChangeDocument fromDomain(FeatureFlagChange change) {
            FeatureFlagChangeDocument doc = new FeatureFlagChangeDocument();
            doc.id = change.id();
            doc.flagId = change.flagId();
            doc.flagKey = change.flagKey();
            doc.tenantId = change.tenantId();
            doc.changeType = change.changeType();
            doc.fromVersion = change.fromVersion();
            doc.toVersion = change.toVersion();
            doc.previousEnabled = change.previousEnabled();
            doc.newEnabled = change.newEnabled();
            doc.changedBy = change.changedBy();
            doc.reason = change.reason();
            doc.changedAt = change.changedAt();
            doc.approvalStatus = change.approvalStatus();
            doc.approvedBy = change.approvedBy();
            doc.approvedAt = change.approvedAt();
            return doc;
        }

        public FeatureFlagChange toDomain() {
            return FeatureFlagChange.builder()
                .id(id)
                .flagId(flagId)
                .flagKey(flagKey)
                .tenantId(tenantId)
                .changeType(changeType)
                .fromVersion(fromVersion)
                .toVersion(toVersion)
                .previousEnabled(previousEnabled)
                .newEnabled(newEnabled)
                .changedBy(changedBy)
                .reason(reason)
                .changedAt(changedAt)
                .approvalStatus(approvalStatus)
                .approvedBy(approvedBy)
                .approvedAt(approvedAt)
                .build();
        }

        // Getters and setters for MongoDB
        @org.springframework.data.annotation.Id
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getFlagId() { return flagId; }
        public void setFlagId(String flagId) { this.flagId = flagId; }
        public String getFlagKey() { return flagKey; }
        public void setFlagKey(String flagKey) { this.flagKey = flagKey; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public FeatureFlagChange.ChangeType getChangeType() { return changeType; }
        public void setChangeType(FeatureFlagChange.ChangeType changeType) { this.changeType = changeType; }
        public Integer getFromVersion() { return fromVersion; }
        public void setFromVersion(Integer fromVersion) { this.fromVersion = fromVersion; }
        public Integer getToVersion() { return toVersion; }
        public void setToVersion(Integer toVersion) { this.toVersion = toVersion; }
        public Boolean getPreviousEnabled() { return previousEnabled; }
        public void setPreviousEnabled(Boolean previousEnabled) { this.previousEnabled = previousEnabled; }
        public Boolean getNewEnabled() { return newEnabled; }
        public void setNewEnabled(Boolean newEnabled) { this.newEnabled = newEnabled; }
        public String getChangedBy() { return changedBy; }
        public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Instant getChangedAt() { return changedAt; }
        public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
        public FeatureFlagChange.ApprovalStatus getApprovalStatus() { return approvalStatus; }
        public void setApprovalStatus(FeatureFlagChange.ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
        public Instant getApprovedAt() { return approvedAt; }
        public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }
    }
}
