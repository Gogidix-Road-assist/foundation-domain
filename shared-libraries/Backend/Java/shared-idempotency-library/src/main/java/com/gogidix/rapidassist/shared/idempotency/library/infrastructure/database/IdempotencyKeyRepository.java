package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * Spring Data JPA repository for IdempotencyKeyEntity.
 */
@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {

    /**
     * Find an idempotency key by its key value.
     *
     * @param idempotencyKey the idempotency key
     * @return optional containing the entity, or empty if not found
     */
    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String idempotencyKey);

    /**
     * Delete all expired idempotency keys.
     *
     * @param now the current time
     * @return number of deleted records
     */
    long deleteByExpiresAtBefore(Instant now);
}
