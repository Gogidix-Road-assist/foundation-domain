package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Database implementation of IdempotencyStore.
 * Stores idempotency records in a relational database using JPA.
 *
 * <p>This implementation provides:
 * <ul>
 *   <li>Persistent storage across restarts</li>
 *   <li>Transactional support</li>
 *   <li>Distributed support via database</li>
 * </ul>
 *
 * <p>TTL is configured via idempotency.ttl property (default: 24 hours).
 * Records are automatically deleted after expiration via a scheduled cleanup job.
 */
public class DatabaseIdempotencyStore implements IdempotencyStore {

    private static final Logger log = LoggerFactory.getLogger(DatabaseIdempotencyStore.class);

    private final IdempotencyKeyRepository repository;
    private final Duration ttl;

    public DatabaseIdempotencyStore(IdempotencyKeyRepository repository, Duration ttl) {
        this.repository = repository;
        this.ttl = ttl;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> find(String key) {
        try {
            return repository.findByIdempotencyKey(key)
                    .map(this::toRecord);
        } catch (Exception e) {
            log.error("Failed to retrieve idempotency record from database: key={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public IdempotencyRecord saveNew(IdempotencyRecord record) {
        try {
            Instant expiresAt = Instant.now().plus(ttl);

            IdempotencyKeyEntity entity = new IdempotencyKeyEntity(
                    record.key(),
                    toEntityStatus(record.status()),
                    expiresAt
            );

            entity.setRequestHash(record.requestHash());
            entity.setResponseStatus(record.responseStatus());
            entity.setResponseHash(record.responseHash());

            IdempotencyKeyEntity saved = repository.save(entity);

            log.debug("Saved new idempotency record to database: key={}, status={}, expiresAt={}",
                    record.key(), record.status(), expiresAt);

            return toRecord(saved);
        } catch (Exception e) {
            log.error("Failed to save idempotency record to database: key={}", record.key(), e);
            throw new RuntimeException("Failed to save idempotency record", e);
        }
    }

    @Override
    @Transactional
    public IdempotencyRecord update(IdempotencyRecord record) {
        try {
            Optional<IdempotencyKeyEntity> existingOpt = repository.findByIdempotencyKey(record.key());

            if (existingOpt.isEmpty()) {
                log.warn("Attempted to update non-existent idempotency record: key={}", record.key());
                return saveNew(record);
            }

            IdempotencyKeyEntity entity = existingOpt.get();
            entity.setStatus(toEntityStatus(record.status()));
            entity.setRequestHash(record.requestHash());
            entity.setResponseStatus(record.responseStatus());
            entity.setResponseHash(record.responseHash());

            IdempotencyKeyEntity updated = repository.save(entity);

            log.debug("Updated idempotency record in database: key={}, status={}",
                    record.key(), record.status());

            return toRecord(updated);
        } catch (Exception e) {
            log.error("Failed to update idempotency record in database: key={}", record.key(), e);
            throw new RuntimeException("Failed to update idempotency record", e);
        }
    }

    /**
     * Delete an idempotency record.
     *
     * @param key the idempotency key
     * @return true if the record was deleted, false otherwise
     */
    @Transactional
    public boolean delete(String key) {
        try {
            Optional<IdempotencyKeyEntity> entity = repository.findByIdempotencyKey(key);
            if (entity.isPresent()) {
                repository.delete(entity.get());
                log.debug("Deleted idempotency record from database: key={}", key);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to delete idempotency record from database: key={}", key, e);
            return false;
        }
    }

    /**
     * Check if a record exists.
     *
     * @param key the idempotency key
     * @return true if the record exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean exists(String key) {
        try {
            return repository.findByIdempotencyKey(key).isPresent();
        } catch (Exception e) {
            log.error("Failed to check idempotency record existence in database: key={}", key, e);
            return false;
        }
    }

    /**
     * Delete all expired records.
     *
     * @return number of deleted records
     */
    @Transactional
    public long deleteExpired() {
        try {
            long deleted = repository.deleteByExpiresAtBefore(Instant.now());
            log.info("Deleted {} expired idempotency records", deleted);
            return deleted;
        } catch (Exception e) {
            log.error("Failed to delete expired idempotency records", e);
            return 0;
        }
    }

    private IdempotencyRecord toRecord(IdempotencyKeyEntity entity) {
        return new IdempotencyRecord(
                entity.getIdempotencyKey(),
                toStoreStatus(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRequestHash(),
                entity.getResponseStatus(),
                entity.getResponseHash()
        );
    }

    private IdempotencyKeyEntity.Status toEntityStatus(Status status) {
        return IdempotencyKeyEntity.Status.valueOf(status.name());
    }

    private Status toStoreStatus(IdempotencyKeyEntity.Status status) {
        return Status.valueOf(status.name());
    }
}
