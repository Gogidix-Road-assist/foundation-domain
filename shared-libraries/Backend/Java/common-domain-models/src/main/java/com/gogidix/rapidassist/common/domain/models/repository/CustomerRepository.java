package com.gogidix.rapidassist.common.domain.models.repository;

import com.gogidix.rapidassist.common.domain.models.business.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for Customer entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    /**
     * Finds customers by tenant ID.
     * @param tenantId the tenant ID
     * @return list of customers belonging to the tenant
     */
    List<Customer> findByTenantId(String tenantId);

    /**
     * Finds a customer by email address.
     * @param email the email address
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Finds a customer by customer number.
     * @param customerNumber the customer number
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByCustomerNumber(String customerNumber);

    /**
     * Finds a customer by user ID.
     * @param userId the user ID
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByUserId(String userId);

    /**
     * Finds customers by customer type.
     * @param customerType the customer type (INDIVIDUAL, CORPORATE, GOVERNMENT)
     * @return list of customers with the specified type
     */
    List<Customer> findByCustomerType(String customerType);

    /**
     * Finds customers by membership level.
     * @param membershipLevel the membership level (STANDARD, SILVER, GOLD, PLATINUM)
     * @return list of customers with the specified membership level
     */
    List<Customer> findByMembershipLevel(String membershipLevel);

    /**
     * Finds customers by status.
     * @param status the status (ACTIVE, INACTIVE, SUSPENDED, BLOCKED)
     * @return list of customers with the specified status
     */
    List<Customer> findByStatus(String status);

    /**
     * Finds customers by tenant ID and status.
     * @param tenantId the tenant ID
     * @param status the status
     * @return list of customers matching both criteria
     */
    List<Customer> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Finds customers whose membership is expired.
     * @return list of customers with expired memberships
     */
        List<Customer> findExpiredMemberships();

    /**
     * Finds customers by organization ID.
     * @param organizationId the organization ID
     * @return list of customers belonging to the organization
     */
    List<Customer> findByOrganizationId(String organizationId);

    /**
     * Searches for customers by name (first or last name contains the search term).
     * @param firstName the first name search term
     * @param lastName the last name search term
     * @return list of matching customers
     */
        List<Customer> searchByName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    /**
     * Counts customers by tenant ID.
     * @param tenantId the tenant ID
     * @return the count of customers
     */
    long countByTenantId(String tenantId);

    /**
     * Checks if a customer exists by email.
     * @param email the email address
     * @return true if a customer with the email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a customer exists by customer number.
     * @param customerNumber the customer number
     * @return true if a customer with the number exists
     */
    boolean existsByCustomerNumber(String customerNumber);
}
