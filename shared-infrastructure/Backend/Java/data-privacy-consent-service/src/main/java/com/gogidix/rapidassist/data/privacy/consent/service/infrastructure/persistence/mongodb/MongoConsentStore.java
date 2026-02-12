package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.out.ConsentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class MongoConsentStore implements ConsentStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoConsentStore.class);
    private static final String CACHE_PREFIX = "consent:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final ConsentPreferencesRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    public MongoConsentStore(ConsentPreferencesRepository repository,
                           RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        logger.info("MongoConsentStore initialized with MongoDB and Redis caching");
    }

    @Override
    public Optional<ConsentPreferences> find(String tenantId, String subject) {
        try {
            // Try cache first
            String cacheKey = buildCacheKey(tenantId, subject);
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                logger.debug("Cache hit for consent: tenant={}, subject={}", tenantId, subject);
                return deserializeConsent(cached);
            }

            // Fetch from MongoDB
            Optional<ConsentPreferencesDocument> document = repository.findByTenantIdAndSubject(tenantId, subject);
            if (document.isPresent()) {
                ConsentPreferences consent = document.get().toDomain();
                // Cache the result
                redisTemplate.opsForValue().set(cacheKey, serializeConsent(consent), CACHE_TTL);
                logger.debug("Retrieved and cached consent: tenant={}, subject={}", tenantId, subject);
                return Optional.of(consent);
            }

            logger.debug("Consent not found: tenant={}, subject={}", tenantId, subject);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error finding consent for tenant: {}, subject: {}", tenantId, subject, e);
            throw new RuntimeException("Failed to find consent", e);
        }
    }

    @Override
    public ConsentPreferences upsert(ConsentPreferences preferences) {
        try {
            Optional<ConsentPreferencesDocument> existing = repository.findByTenantIdAndSubject(
                preferences.tenantId(),
                preferences.subject()
            );

            ConsentPreferencesDocument document;
            if (existing.isPresent()) {
                // Update existing
                document = ConsentPreferencesDocument.updateFromDomain(existing.get(), preferences);
                logger.debug("Updating existing consent: tenant={}, subject={}",
                    preferences.tenantId(), preferences.subject());
            } else {
                // Create new
                document = ConsentPreferencesDocument.fromDomain(preferences);
                logger.debug("Creating new consent: tenant={}, subject={}",
                    preferences.tenantId(), preferences.subject());
            }

            // Save to MongoDB
            ConsentPreferencesDocument saved = repository.save(document);

            // Update cache
            String cacheKey = buildCacheKey(preferences.tenantId(), preferences.subject());
            redisTemplate.opsForValue().set(cacheKey, serializeConsent(saved.toDomain()), CACHE_TTL);

            logger.info("Upserted consent: tenant={}, subject={}, terms={}, marketing={}",
                preferences.tenantId(),
                preferences.subject(),
                preferences.termsAccepted(),
                preferences.marketingEmails());

            return saved.toDomain();

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic lock failure when upserting consent: tenant={}, subject={}",
                preferences.tenantId(), preferences.subject(), e);
            throw new RuntimeException("Concurrent modification detected", e);
        } catch (Exception e) {
            logger.error("Error upserting consent: tenant={}, subject={}",
                preferences.tenantId(), preferences.subject(), e);
            throw new RuntimeException("Failed to upsert consent", e);
        }
    }

    private String buildCacheKey(String tenantId, String subject) {
        return CACHE_PREFIX + tenantId + ":" + subject;
    }

    private String serializeConsent(ConsentPreferences consent) {
        return String.format("%s|%s|%b|%b|%s",
            consent.tenantId(),
            consent.subject(),
            consent.termsAccepted(),
            consent.marketingEmails(),
            consent.updatedAt().toString());
    }

    private Optional<ConsentPreferences> deserializeConsent(String serialized) {
        try {
            String[] parts = serialized.split("\\|");
            if (parts.length != 5) {
                logger.warn("Invalid cached consent format: {}", serialized);
                return Optional.empty();
            }

            return Optional.of(new ConsentPreferences(
                parts[0],
                parts[1],
                Boolean.parseBoolean(parts[2]),
                Boolean.parseBoolean(parts[3]),
                Instant.parse(parts[4])
            ));
        } catch (Exception e) {
            logger.warn("Failed to deserialize cached consent: {}", serialized, e);
            return Optional.empty();
        }
    }
}