package com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.config;

import com.gogidix.rapidassist.shared.request.context.library.autoconfigure.SharedRequestContextAutoConfiguration;
import com.gogidix.rapidassist.shared.request.context.library.autoconfigure.SharedRequestContextAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

/**
 * Auto-configuration for tenant-aware JPA persistence.
 * <p>
 * This configuration class sets up the necessary beans and settings for
 * multi-tenancy support in JPA-based services. It automatically configures:
 * </p>
 * <ul>
 *   <li>Tenant-aware repository factory bean for automatic tenant ID propagation</li>
 *   <li>Entity scanning for tenant-aware entities</li>
 *   <li>Repository scanning for tenant-aware repositories</li>
 * </ul>
 * <p>
 * This configuration is automatically loaded via Spring Boot's auto-configuration
 * mechanism when the following conditions are met:
 * </p>
 * <ul>
 *   <li>Spring Data JPA is on the classpath</li>
 *   <li>TenantAwareJpaRepository or related classes are on the classpath</li>
 * </ul>
 * <p>
 * To customize the base packages for scanning, use the following properties:
 * </p>
 * <pre>{@code
 * spring.data.jpa.repositories.packages=com.gogidix.insurance
 * spring.data.jpa.entities.packages=com.gogidix.insurance
 * }</pre>
 * <p>
 * Or explicitly import this configuration and provide custom values:
 * </p>
 * <pre>{@code
 * @Import(TenantConfiguration.class)
 * @EnableJpaRepositories(
 *     basePackages = "com.gogidix.insurance",
 *     repositoryFactoryBeanClass = TenantRepositoryFactoryBean.class
 * )
 * @EntityScan("com.gogidix.insurance")
 * public class CustomTenantConfig {
 * }
 * }</pre>
 *
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-03-10
 */
@AutoConfiguration(after = SharedRequestContextAutoConfiguration.class)
@ConditionalOnClass({jakarta.persistence.Entity.class, org.springframework.data.jpa.repository.JpaRepository.class})
@EnableJpaRepositories(
        basePackages = "${spring.data.jpa.repositories.packages:com.gogidix}",
        repositoryFactoryBeanClass = TenantRepositoryFactoryBean.class,
        includeFilters = @ComponentScan.Filter(type = ComponentScan.FilterType.ASSIGNABLE_TYPE,
                classes = com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.repository.TenantAwareJpaRepository.class)
)
@EntityScan("${spring.data.jpa.entities.packages:com.gogidix}")
public class TenantConfiguration {

    /**
     * Default constructor for TenantConfiguration.
     * <p>
     * This configuration is automatically loaded by Spring Boot's auto-configuration
     * mechanism when the required classes are present on the classpath.
     * </p>
     */
    public TenantConfiguration() {
        // Default constructor
    }
}
