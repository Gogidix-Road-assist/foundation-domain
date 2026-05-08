package com.gogidix.rapidassist.api.keys.service.infrastructure.provider.mongo;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;
import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKeyVerificationResult;
import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;
import com.gogidix.rapidassist.api.keys.service.infrastructure.provider.ApiKeysProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB-based API Key provider with secure key generation and storage.
 */
public class MongoApiKeyProvider implements ApiKeyProvider {

    private static final Logger logger = LoggerFactory.getLogger(MongoApiKeyProvider.class);

    private static final int KEY_LENGTH = 32; // bytes
    private static final String KEY_PREFIX = "gogidix_";
    private static final int DEFAULT_EXPIRY_DAYS = 365;

    private final MongoTemplate mongoTemplate;
    private final ApiKeysProperties properties;
    private final SecureRandom secureRandom;

    public MongoApiKeyProvider(MongoTemplate mongoTemplate, ApiKeysProperties properties) {
        this.mongoTemplate = mongoTemplate;
        this.properties = properties;
        this.secureRandom = new SecureRandom();
        logger.info("MongoApiKeyProvider initialized with default expiry: {} days", DEFAULT_EXPIRY_DAYS);
    }

    @Override
    public ApiKey issue(String tenantId, String subject) {
        if (tenantId == null || subject == null) {
            throw new IllegalArgumentException("Tenant ID and subject are required");
        }

        // Generate API key
        String rawKey = generateApiKey();
        String keyId = UUID.randomUUID().toString();
        String hashedKey = hashApiKey(rawKey);
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getDefaultExpiryDays() * 24L * 60L * 60L);

        // Store in database
        ApiKeyDocument document = new ApiKeyDocument();
        document.setId(keyId);
        document.setTenantId(tenantId);
        document.setSubject(subject);
        document.setHashedKey(hashedKey);
        document.setCreatedAt(now);
        document.setExpiresAt(expiresAt);
        document.setActive(true);
        document.setLastUsedAt(null);
        document.setUsageCount(0);
        document.setMetadata(properties.getDefaultMetadata());

        mongoTemplate.save(document);

        logger.info("API key issued for tenant: {}, subject: {}, keyId: {}", tenantId, subject, keyId);

        // Return the API key with the raw key (only returned once)
        return new ApiKey(tenantId, keyId, rawKey, expiresAt);
    }

    @Override
    public ApiKeyVerificationResult verify(String tenantId, String apiKey) {
        if (apiKey == null || !apiKey.startsWith(KEY_PREFIX)) {
            return new ApiKeyVerificationResult(false, null, "Invalid API key format");
        }

        // Hash the provided key
        String hashedKey = hashApiKey(apiKey);

        // Query database
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
                              .and("hashedKey").is(hashedKey)
                              .and("active").is(true));

        ApiKeyDocument document = mongoTemplate.findOne(query, ApiKeyDocument.class);

        if (document == null) {
            return new ApiKeyVerificationResult(false, null, "API key not found or inactive");
        }

        // Check if key has expired
        if (document.getExpiresAt() != null && document.getExpiresAt().isBefore(Instant.now())) {
            // Deactivate expired key
            deactivateKey(document.getId());
            return new ApiKeyVerificationResult(false, null, "API key has expired");
        }

        // Check if key is within usage limits
        if (properties.getMaxUsagePerKey() > 0 &&
            document.getUsageCount() >= properties.getMaxUsagePerKey()) {
            return new ApiKeyVerificationResult(false, null, "API key usage limit exceeded");
        }

        // Update usage statistics
        updateKeyUsage(document.getId());

        logger.debug("API key verified successfully for tenant: {}, subject: {}",
                    document.getTenantId(), document.getSubject());

        return new ApiKeyVerificationResult(true, document.getSubject(), null);
    }

    @Override
    public void revoke(String tenantId, String keyId) {
        if (tenantId == null || keyId == null) {
            throw new IllegalArgumentException("Tenant ID and key ID are required");
        }

        Query query = new Query(Criteria.where("tenantId").is(tenantId)
                              .and("_id").is(keyId));

        Update update = new Update().set("active", false)
                                   .set("revokedAt", Instant.now());

        long modified = mongoTemplate.updateFirst(query, update, ApiKeyDocument.class).getModifiedCount();

        if (modified > 0) {
            logger.info("API key revoked for tenant: {}, keyId: {}", tenantId, keyId);
        } else {
            logger.warn("API key not found for revocation: tenant: {}, keyId: {}", tenantId, keyId);
        }
    }

    /**
     * Rotate an existing API key
     */
    public ApiKey rotate(String tenantId, String keyId) {
        if (tenantId == null || keyId == null) {
            throw new IllegalArgumentException("Tenant ID and key ID are required");
        }

        Query query = new Query(Criteria.where("tenantId").is(tenantId)
                              .and("_id").is(keyId)
                              .and("active").is(true));

        ApiKeyDocument existingKey = mongoTemplate.findOne(query, ApiKeyDocument.class);

        if (existingKey == null) {
            throw new IllegalArgumentException("API key not found or inactive");
        }

        // Deactivate old key
        deactivateKey(keyId);

        // Issue new key
        return issue(tenantId, existingKey.getSubject());
    }

    /**
     * List all API keys for a tenant
     */
    public List<ApiKeyInfo> listKeys(String tenantId, boolean includeInactive) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        if (!includeInactive) {
            query.addCriteria(Criteria.where("active").is(true));
        }
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<ApiKeyDocument> documents = mongoTemplate.find(query, ApiKeyDocument.class);

        return documents.stream()
                .map(doc -> new ApiKeyInfo(
                    doc.getId(),
                    doc.getSubject(),
                    doc.getCreatedAt(),
                    doc.getExpiresAt(),
                    doc.isActive(),
                    doc.getUsageCount(),
                    doc.getLastUsedAt(),
                    doc.getMetadata()
                ))
                .toList();
    }

    private String generateApiKey() {
        byte[] keyBytes = new byte[KEY_LENGTH];
        secureRandom.nextBytes(keyBytes);
        String base64Key = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
        return KEY_PREFIX + base64Key;
    }

    private String hashApiKey(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(apiKey.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash API key", e);
        }
    }

    private void updateKeyUsage(String keyId) {
        Query query = new Query(Criteria.where("_id").is(keyId));
        Update update = new Update()
                .inc("usageCount", 1)
                .set("lastUsedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, ApiKeyDocument.class);
    }

    private void deactivateKey(String keyId) {
        Query query = new Query(Criteria.where("_id").is(keyId));
        Update update = new Update()
                .set("active", false)
                .set("revokedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, ApiKeyDocument.class);
    }

    // Data models
    public record ApiKeyInfo(
        String keyId,
        String subject,
        Instant createdAt,
        Instant expiresAt,
        boolean active,
        long usageCount,
        Instant lastUsedAt,
        java.util.Map<String, Object> metadata
    ) {}

    // MongoDB document model
    private static class ApiKeyDocument {
        private String id;
        private String tenantId;
        private String subject;
        private String hashedKey;
        private Instant createdAt;
        private Instant expiresAt;
        private boolean active;
        private Instant revokedAt;
        private Instant lastUsedAt;
        private long usageCount;
        private java.util.Map<String, Object> metadata;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getHashedKey() { return hashedKey; }
        public void setHashedKey(String hashedKey) { this.hashedKey = hashedKey; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Instant getRevokedAt() { return revokedAt; }
        public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
        public Instant getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
        public long getUsageCount() { return usageCount; }
        public void setUsageCount(long usageCount) { this.usageCount = usageCount; }
        public java.util.Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(java.util.Map<String, Object> metadata) { this.metadata = metadata; }
    }
}