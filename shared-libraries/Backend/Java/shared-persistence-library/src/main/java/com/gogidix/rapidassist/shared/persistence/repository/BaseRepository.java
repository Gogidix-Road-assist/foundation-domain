package com.gogidix.rapidassist.shared.persistence.repository;

import com.gogidix.rapidassist.shared.persistence.domain.TenantAwareEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for entities extending BaseEntity.
 * Provides common CRUD operations with soft delete and tenant awareness.
 */
@NoRepositoryBean
public interface BaseRepository<T extends TenantAwareEntity> extends MongoRepository<T, UUID> {

    /**
     * Find an active (not soft deleted) entity by ID.
     */
    @Query("{ '_id': ?0, 'deleted': { $in: [null, false] } }")
    Optional<T> findActiveById(UUID id);

    /**
     * Check if an entity exists by ID and is not soft deleted.
     */
    @Query(value = "{ '_id': ?0, 'deleted': { $in: [null, false] } }", count = true)
    boolean existsActiveById(UUID id);
}
