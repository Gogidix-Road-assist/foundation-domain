package com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.port.out.BillingAccountStore;
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
public class MongoBillingAccountStore implements BillingAccountStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoBillingAccountStore.class);
    private static final String CACHE_PREFIX = "billing:account:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final MongoBillingAccountRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    public MongoBillingAccountStore(MongoBillingAccountRepository repository,
                                  RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        logger.info("MongoBillingAccountStore initialized with MongoDB and Redis caching");
    }

    @Override
    public Optional<BillingAccount> find(String tenantId) {
        try {
            // Try cache first
            String cacheKey = CACHE_PREFIX + tenantId;
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                logger.debug("Cache hit for billing account: {}", tenantId);
                return deserializeAccount(cached);
            }

            // Fetch from MongoDB
            Optional<BillingAccountDocument> document = repository.findByTenantId(tenantId);
            if (document.isPresent()) {
                BillingAccount account = document.get().toDomain();
                // Cache the result
                redisTemplate.opsForValue().set(cacheKey, serializeAccount(account), CACHE_TTL);
                logger.debug("Retrieved and cached billing account: {}", tenantId);
                return Optional.of(account);
            }

            logger.debug("Billing account not found: {}", tenantId);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error finding billing account for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to find billing account", e);
        }
    }

    @Override
    public BillingAccount upsert(BillingAccount account) {
        try {
            Optional<BillingAccountDocument> existing = repository.findByTenantId(account.tenantId());

            BillingAccountDocument document;
            if (existing.isPresent()) {
                // Update existing
                document = BillingAccountDocument.updateFromDomain(existing.get(), account);
                logger.debug("Updating existing billing account for tenant: {}", account.tenantId());
            } else {
                // Create new
                document = BillingAccountDocument.fromDomain(account);
                logger.debug("Creating new billing account for tenant: {}", account.tenantId());
            }

            // Save to MongoDB
            BillingAccountDocument saved = repository.save(document);

            // Update cache
            String cacheKey = CACHE_PREFIX + account.tenantId();
            redisTemplate.opsForValue().set(cacheKey, serializeAccount(saved.toDomain()), CACHE_TTL);

            logger.info("Upserted billing account for tenant: {}", account.tenantId());
            return saved.toDomain();

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic lock failure when upserting billing account for tenant: {}",
                account.tenantId(), e);
            throw new RuntimeException("Concurrent modification detected", e);
        } catch (Exception e) {
            logger.error("Error upserting billing account for tenant: {}", account.tenantId(), e);
            throw new RuntimeException("Failed to upsert billing account", e);
        }
    }

    private String serializeAccount(BillingAccount account) {
        return String.format("%s|%s|%s",
            account.tenantId(),
            account.plan().name(),
            account.updatedAt().toString());
    }

    private Optional<BillingAccount> deserializeAccount(String serialized) {
        try {
            String[] parts = serialized.split("\\|");
            if (parts.length != 3) {
                logger.warn("Invalid cached account format: {}", serialized);
                return Optional.empty();
            }

            return Optional.of(new BillingAccount(
                parts[0],
                BillingPlan.valueOf(parts[1]),
                Instant.parse(parts[2])
            ));
        } catch (Exception e) {
            logger.warn("Failed to deserialize cached account: {}", serialized, e);
            return Optional.empty();
        }
    }
}