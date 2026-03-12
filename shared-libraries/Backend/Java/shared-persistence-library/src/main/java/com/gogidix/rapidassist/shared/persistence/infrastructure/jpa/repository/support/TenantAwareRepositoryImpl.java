package com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.repository.support;

import com.gogidix.rapidassist.shared.request.context.library.jpa.JpaTenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Custom implementation of JPA repository that automatically sets tenant ID on entities.
 * <p>
 * This class extends SimpleJpaRepository to provide automatic tenant ID propagation
 * from the request context to entities before persistence operations. It ensures
 * that all entities saved through repositories using this implementation have their
 * tenantId field automatically populated.
 * </p>
 * <p>
 * The implementation uses reflection to set the tenant ID on entities that have
 * a setTenantId method. If the entity doesn't have this method, the operation
 * proceeds without error, allowing the implementation to work with non-tenant-aware
 * entities as well.
 * </p>
 * <p>
 * This class should not be used directly. Instead, use TenantRepositoryFactoryBean
 * to configure Spring Data JPA to use this implementation for all repositories
 * extending TenantAwareJpaRepository.
 * </p>
 *
 * @param <T>  the entity type managed by this repository
 * @param <ID> the type of the entity's identifier
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-03-10
 */
public class TenantAwareRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

    private final EntityManager entityManager;

    /**
     * Creates a new TenantAwareRepositoryImpl.
     *
     * @param entityInformation metadata for the entity
     * @param entityManager     the JPA entity manager
     */
    public TenantAwareRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * Creates a new TenantAwareRepositoryImpl.
     *
     * @param domainClass the entity class
     * @param em          the JPA entity manager
     */
    public TenantAwareRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    /**
     * Returns the current tenant ID from the request context.
     *
     * @return the current tenant ID, or default tenant ID (1L) if not present
     */
    protected Long getCurrentTenantId() {
        return JpaTenantContext.getTenantIdOrDefault();
    }

    /**
     * Saves an entity, automatically setting the tenant ID if the entity supports it.
     * <p>
     * This method intercepts all save operations and ensures that the tenant ID
     * is set on the entity before persistence. The tenant ID is obtained from the
     * current request context via JpaTenantContext.
     * </p>
     *
     * @param entity the entity to save
     * @param <S>    the entity type
     * @return the saved entity
     */
    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        setTenantIdIfPresent(entity);
        return super.save(entity);
    }

    /**
     * Saves all entities, automatically setting the tenant ID if the entities support it.
     *
     * @param entities the entities to save
     * @param <S>      the entity type
     * @return the saved entities
     */
    @Override
    @Transactional
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::setTenantIdIfPresent);
        return super.saveAll(entities);
    }

    /**
     * Sets the tenant ID on an entity if it has a setTenantId method.
     * <p>
     * This method uses reflection to check if the entity has a setTenantId method
     * that accepts a Long parameter. If it does, it invokes the method with the
     * current tenant ID from the request context.
     * </p>
     * <p>
     * If the entity doesn't have a setTenantId method, or if invoking it fails,
     * the operation proceeds without error. This allows the implementation to
     * work with both tenant-aware and non-tenant-aware entities.
     * </p>
     *
     * @param entity the entity on which to set the tenant ID
     */
    protected void setTenantIdIfPresent(Object entity) {
        if (entity == null) {
            return;
        }

        try {
            // Try to invoke setTenantId method with Long parameter
            entity.getClass().getMethod("setTenantId", Long.class).invoke(entity, getCurrentTenantId());
        } catch (NoSuchMethodException e) {
            // Entity doesn't have setTenantId method, ignore
            // This allows the implementation to work with non-tenant-aware entities
        } catch (Exception e) {
            // Log warning but don't fail the operation
            // In production, you might want to log this
        }
    }

    /**
     * Returns the entity manager associated with this repository.
     *
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
