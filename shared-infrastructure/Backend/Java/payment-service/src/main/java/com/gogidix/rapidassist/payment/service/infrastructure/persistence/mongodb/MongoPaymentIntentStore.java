package com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.port.out.PaymentIntentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class MongoPaymentIntentStore implements PaymentIntentStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoPaymentIntentStore.class);
    private static final String CACHE_PREFIX = "payment:intent:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final PaymentIntentRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public MongoPaymentIntentStore(PaymentIntentRepository repository,
                                  RedisTemplate<String, String> redisTemplate,
                                  ObjectMapper objectMapper) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        logger.info("MongoPaymentIntentStore initialized with MongoDB and Redis caching");
    }

    @Override
    public Optional<PaymentIntent> find(String tenantId, String intentId) {
        try {
            // Try cache first
            String cacheKey = buildCacheKey(tenantId, intentId);
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                logger.debug("Cache hit for payment intent: tenant={}, intentId={}", tenantId, intentId);
                return deserializeIntent(cached);
            }

            // Fetch from MongoDB
            Optional<PaymentIntentDocument> document = repository.findByTenantIdAndIntentId(tenantId, intentId);
            if (document.isPresent()) {
                PaymentIntent intent = document.get().toDomain();
                // Cache the result
                redisTemplate.opsForValue().set(cacheKey, serializeIntent(intent), CACHE_TTL);
                logger.debug("Retrieved and cached payment intent: tenant={}, intentId={}", tenantId, intentId);
                return Optional.of(intent);
            }

            logger.debug("Payment intent not found: tenant={}, intentId={}", tenantId, intentId);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error finding payment intent: tenant={}, intentId={}", tenantId, intentId, e);
            throw new RuntimeException("Failed to find payment intent", e);
        }
    }

    @Override
    public PaymentIntent save(PaymentIntent intent) {
        try {
            Optional<PaymentIntentDocument> existing = repository.findByTenantIdAndIntentId(
                intent.tenantId(),
                intent.intentId()
            );

            PaymentIntentDocument document;
            if (existing.isPresent()) {
                // Update existing
                document = PaymentIntentDocument.updateFromDomain(existing.get(), intent);
                logger.debug("Updating existing payment intent: tenant={}, intentId={}",
                    intent.tenantId(), intent.intentId());
            } else {
                // Create new
                document = PaymentIntentDocument.fromDomain(intent);
                logger.debug("Creating new payment intent: tenant={}, intentId={}",
                    intent.tenantId(), intent.intentId());
            }

            // Save to MongoDB
            PaymentIntentDocument saved = repository.save(document);

            // Update cache
            String cacheKey = buildCacheKey(intent.tenantId(), intent.intentId());
            redisTemplate.opsForValue().set(cacheKey, serializeIntent(saved.toDomain()), CACHE_TTL);

            logger.info("Saved payment intent: tenant={}, intentId={}, status={}",
                intent.tenantId(),
                intent.intentId(),
                intent.status());
            return saved.toDomain();

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic lock failure when saving payment intent: tenant={}, intentId={}",
                intent.tenantId(), intent.intentId(), e);
            throw new RuntimeException("Concurrent modification detected", e);
        } catch (Exception e) {
            logger.error("Error saving payment intent: tenant={}, intentId={}",
                intent.tenantId(), intent.intentId(), e);
            throw new RuntimeException("Failed to save payment intent", e);
        }
    }

    private String buildCacheKey(String tenantId, String intentId) {
        return CACHE_PREFIX + tenantId + ":" + intentId;
    }

    private String serializeIntent(PaymentIntent intent) {
        try {
            return objectMapper.writeValueAsString(intent);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize payment intent", e);
            throw new RuntimeException("Serialization failed", e);
        }
    }

    private Optional<PaymentIntent> deserializeIntent(String serialized) {
        try {
            PaymentIntent intent = objectMapper.readValue(serialized, PaymentIntent.class);
            return Optional.of(intent);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to deserialize cached payment intent: {}", serialized, e);
            return Optional.empty();
        }
    }
}