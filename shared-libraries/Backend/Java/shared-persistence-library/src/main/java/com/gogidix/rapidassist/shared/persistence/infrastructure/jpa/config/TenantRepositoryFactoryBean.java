package com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.config;

import com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.repository.TenantAwareJpaRepository;
import com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.repository.support.TenantAwareRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Factory bean for creating tenant-aware JPA repositories.
 * <p>
 * This class extends JpaRepositoryFactoryBean to configure Spring Data JPA
 * to use the TenantAwareRepositoryImpl for all repositories that extend
 * TenantAwareJpaRepository. This enables automatic tenant ID propagation
 * from the request context to entities during persistence operations.
 * </p>
 * <p>
 * To use this factory bean in your application, configure it in your
 * JPA configuration:
 * </p>
 * <pre>{@code
 * @Configuration
 * @EnableJpaRepositories(
 *     basePackages = "com.gogidix",
 *     repositoryFactoryBeanClass = TenantRepositoryFactoryBean.class
 * )
 * public class JpaConfig {
 *     // ...
 * }
 * }</pre>
 * <p>
 * Alternatively, use the TenantConfiguration convenience class which
 * includes this configuration along with other tenant-related beans.
 * </p>
 *
 * @param <R> the repository type
 * @param <T> the entity type
 * @param <I> the ID type
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-03-10
 */
public class TenantRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I> extends JpaRepositoryFactoryBean<R, T, I> {

    /**
     * Creates a new TenantRepositoryFactoryBean for the given repository interface.
     *
     * @param repositoryInterface the repository interface class
     */
    public TenantRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Creates a repository factory that uses TenantAwareRepositoryImpl
     * as the base class for all repositories.
     *
     * @param entityManager the JPA entity manager
     * @return a configured repository factory
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager) {
            @Override
            protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
                // Use TenantAwareRepositoryImpl for all repositories
                return TenantAwareRepositoryImpl.class;
            }
        };
        return factory;
    }
}
