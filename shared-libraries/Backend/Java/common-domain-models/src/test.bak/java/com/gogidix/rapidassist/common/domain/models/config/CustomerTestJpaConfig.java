package com.gogidix.rapidassist.common.domain.models.config;

import com.gogidix.rapidassist.common.domain.models.business.Customer;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Minimal test configuration for Customer repository tests.
 * Only scans Customer entity and CustomerRepository to avoid
 * Hibernate mapping issues with other entities.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.gogidix.rapidassist.common.domain.models.repository")
@EntityScan(basePackageClasses = {Customer.class, PhoneNumber.class, Address.class})
public class CustomerTestJpaConfig {
}
