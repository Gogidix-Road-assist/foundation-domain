package com.gogidix.rapidassist.common.domain.models.repository;

import com.gogidix.rapidassist.common.domain.models.business.Customer;
import com.gogidix.rapidassist.common.domain.models.config.CustomerTestJpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CustomerRepository using @DataJpaTest.
 * Tests repository methods with H2 in-memory database.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(CustomerTestJpaConfig.class)
@DisplayName("Customer Repository Tests")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setUserId("user123");
        testCustomer.setCustomerNumber("CUST001");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john@example.com");
        testCustomer.setCustomerType("INDIVIDUAL");
        testCustomer.setMembershipLevel("GOLD");
        testCustomer.setStatus("ACTIVE");
        testCustomer.setTenantId("tenant123");
        testCustomer.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save customer successfully")
    void testSaveCustomer() {
        Customer savedCustomer = customerRepository.save(testCustomer);

        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
        assertEquals("John", savedCustomer.getFirstName());
        assertEquals("Doe", savedCustomer.getLastName());
    }

    @Test
    @DisplayName("Should find customer by ID")
    void testFindById() {
        Customer savedCustomer = customerRepository.save(testCustomer);

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
    }

    @Test
    @DisplayName("Should find customers by tenant ID")
    void testFindByTenantId() {
        customerRepository.save(testCustomer);

        Customer customer2 = new Customer();
        customer2.setUserId("user456");
        customer2.setCustomerNumber("CUST002");
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setTenantId("tenant123");
        customer2.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findByTenantId("tenant123");

        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Should find customer by email")
    void testFindByEmail() {
        customerRepository.save(testCustomer);

        Optional<Customer> foundCustomer = customerRepository.findByEmail("john@example.com");

        assertTrue(foundCustomer.isPresent());
        assertEquals("john@example.com", foundCustomer.get().getEmail());
    }

    @Test
    @DisplayName("Should find customer by customer number")
    void testFindByCustomerNumber() {
        customerRepository.save(testCustomer);

        Optional<Customer> foundCustomer = customerRepository.findByCustomerNumber("CUST001");

        assertTrue(foundCustomer.isPresent());
        assertEquals("CUST001", foundCustomer.get().getCustomerNumber());
    }

    @Test
    @DisplayName("Should find customer by user ID")
    void testFindByUserId() {
        customerRepository.save(testCustomer);

        Optional<Customer> foundCustomer = customerRepository.findByUserId("user123");

        assertTrue(foundCustomer.isPresent());
        assertEquals("user123", foundCustomer.get().getUserId());
    }

    @Test
    @DisplayName("Should find customers by customer type")
    void testFindByCustomerType() {
        customerRepository.save(testCustomer);

        Customer customer2 = new Customer();
        customer2.setUserId("user456");
        customer2.setCustomerNumber("CUST002");
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setCustomerType("INDIVIDUAL");
        customer2.setTenantId("tenant123");
        customer2.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findByCustomerType("INDIVIDUAL");

        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Should find customers by status")
    void testFindByStatus() {
        customerRepository.save(testCustomer);

        Customer customer2 = new Customer();
        customer2.setUserId("user456");
        customer2.setCustomerNumber("CUST002");
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setStatus("ACTIVE");
        customer2.setTenantId("tenant123");
        customer2.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findByStatus("ACTIVE");

        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Should find customers by tenant ID and status")
    void testFindByTenantIdAndStatus() {
        customerRepository.save(testCustomer);

        Customer customer2 = new Customer();
        customer2.setUserId("user456");
        customer2.setCustomerNumber("CUST002");
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setStatus("INACTIVE");
        customer2.setTenantId("tenant123");
        customer2.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer2);

        List<Customer> activeCustomers = customerRepository.findByTenantIdAndStatus("tenant123", "ACTIVE");

        assertEquals(1, activeCustomers.size());
        assertEquals("John", activeCustomers.get(0).getFirstName());
    }

    @Test
    @DisplayName("Should count customers by tenant ID")
    void testCountByTenantId() {
        customerRepository.save(testCustomer);

        Customer customer2 = new Customer();
        customer2.setUserId("user456");
        customer2.setCustomerNumber("CUST002");
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@example.com");
        customer2.setTenantId("tenant123");
        customer2.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer2);

        long count = customerRepository.countByTenantId("tenant123");

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check if customer exists by email")
    void testExistsByEmail() {
        customerRepository.save(testCustomer);

        assertTrue(customerRepository.existsByEmail("john@example.com"));
        assertFalse(customerRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should check if customer exists by customer number")
    void testExistsByCustomerNumber() {
        customerRepository.save(testCustomer);

        assertTrue(customerRepository.existsByCustomerNumber("CUST001"));
        assertFalse(customerRepository.existsByCustomerNumber("CUST999"));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomer() {
        Customer savedCustomer = customerRepository.save(testCustomer);

        customerRepository.deleteById(savedCustomer.getId());

        Optional<Customer> deletedCustomer = customerRepository.findById(savedCustomer.getId());
        assertFalse(deletedCustomer.isPresent());
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomer() {
        Customer savedCustomer = customerRepository.save(testCustomer);

        savedCustomer.setFirstName("Jane");
        savedCustomer.setMembershipLevel("PLATINUM");
        customerRepository.save(savedCustomer);

        Optional<Customer> updatedCustomer = customerRepository.findById(savedCustomer.getId());

        assertTrue(updatedCustomer.isPresent());
        assertEquals("Jane", updatedCustomer.get().getFirstName());
        assertEquals("PLATINUM", updatedCustomer.get().getMembershipLevel());
    }
}
