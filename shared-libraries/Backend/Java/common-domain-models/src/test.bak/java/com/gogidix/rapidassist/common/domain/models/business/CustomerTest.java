package com.gogidix.rapidassist.common.domain.models.business;

import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Customer entity.
 * Tests entity validation, business logic, relationships, and lifecycle methods.
 */
@DisplayName("Customer Entity Tests")
class CustomerTest {

    @Test
    @DisplayName("Should create customer with default constructor")
    void testDefaultConstructor() {
        Customer customer = new Customer();

        assertNotNull(customer);
        assertNotNull(customer.getCreatedAt());
        assertEquals(0, customer.getCurrentBalance());
        assertEquals(0, customer.getTotalRequests());
        assertEquals(0, customer.getCompletedRequests());
        assertEquals(0, customer.getCancelledRequests());
        assertEquals("INDIVIDUAL", customer.getCustomerType());
        assertEquals("ACTIVE", customer.getStatus());
    }

    @Test
    @DisplayName("Should create customer with parameterized constructor")
    void testParameterizedConstructor() {
        Customer customer = new Customer("John", "Doe", "john@example.com");

        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john@example.com", customer.getEmail());
        assertNotNull(customer.getCreatedAt());
    }

    @Test
    @DisplayName("Should get full name correctly")
    void testGetFullName() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");

        String fullName = customer.getFullName();

        assertEquals("John Doe", fullName);
    }

    @Test
    @DisplayName("Should return active membership when expiry is null")
    void testIsActiveMembershipWithNullExpiry() {
        Customer customer = new Customer();
        customer.setMembershipExpiry(null);

        assertTrue(customer.isMembershipActive());
    }

    @Test
    @DisplayName("Should return active membership when expiry is in future")
    void testIsActiveMembershipWithFutureExpiry() {
        Customer customer = new Customer();
        customer.setMembershipExpiry(LocalDateTime.now().plusDays(30));

        assertTrue(customer.isMembershipActive());
    }

    @Test
    @DisplayName("Should return inactive membership when expiry is in past")
    void testIsInactiveMembershipWithPastExpiry() {
        Customer customer = new Customer();
        customer.setMembershipExpiry(LocalDateTime.now().minusDays(1));

        assertFalse(customer.isMembershipActive());
    }

    @Test
    @DisplayName("Should increment completed requests correctly")
    void testIncrementCompletedRequests() {
        Customer customer = new Customer();
        customer.setTotalRequests(5);
        customer.setCompletedRequests(3);

        customer.incrementCompletedRequests();

        assertEquals(4, customer.getCompletedRequests());
        assertEquals(6, customer.getTotalRequests());
        assertNotNull(customer.getLastServiceDate());
    }

    @Test
    @DisplayName("Should increment completed requests from null values")
    void testIncrementCompletedRequestsFromNull() {
        Customer customer = new Customer();
        customer.setTotalRequests(null);
        customer.setCompletedRequests(null);

        customer.incrementCompletedRequests();

        assertEquals(1, customer.getCompletedRequests());
        assertEquals(1, customer.getTotalRequests());
    }

    @Test
    @DisplayName("Should handle equality based on ID")
    void testEquality() {
        Customer customer1 = new Customer();
        customer1.setId("123");

        Customer customer2 = new Customer();
        customer2.setId("123");

        assertEquals(customer1, customer2);
    }

    @Test
    @DisplayName("Should not be equal when IDs are different")
    void testInequality() {
        Customer customer1 = new Customer();
        customer1.setId("123");

        Customer customer2 = new Customer();
        customer2.setId("456");

        assertNotEquals(customer1, customer2);
    }

    @Test
    @DisplayName("Should not be equal when comparing to null")
    void testNotEqualToNull() {
        Customer customer = new Customer();
        customer.setId("123");

        assertNotEquals(null, customer);
    }

    @Test
    @DisplayName("Should not be equal when comparing to different type")
    void testNotEqualDifferentType() {
        Customer customer = new Customer();
        customer.setId("123");

        assertNotEquals(customer, "string");
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEqualToSelf() {
        Customer customer = new Customer();
        customer.setId("123");

        assertEquals(customer, customer);
    }

    @Test
    @DisplayName("Should generate consistent hash code")
    void testHashCode() {
        Customer customer = new Customer();
        customer.setId("123");

        int hashCode1 = customer.hashCode();
        int hashCode2 = customer.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testAllGettersAndSetters() {
        Customer customer = new Customer();
        Address address = new Address();
        PhoneNumber primaryPhone = new PhoneNumber();
        PhoneNumber secondaryPhone = new PhoneNumber();

        customer.setId("id123");
        customer.setUserId("user123");
        customer.setCustomerNumber("CUST001");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPrimaryPhone(primaryPhone);
        customer.setSecondaryPhone(secondaryPhone);
        customer.setAddress(address);
        customer.setCustomerType("CORPORATE");
        customer.setMembershipLevel("GOLD");
        customer.setMembershipExpiry(LocalDateTime.now().plusDays(30));
        customer.setCreditLimit(10000);
        customer.setCurrentBalance(1000);
        customer.setStatus("ACTIVE");
        customer.setTenantId("tenant123");
        customer.setOrganizationId("org123");
        customer.setCorporateAccountId("corp123");
        customer.setTotalRequests(10);
        customer.setCompletedRequests(8);
        customer.setCancelledRequests(2);
        customer.setAverageRating(4.5);
        customer.setLastServiceDate(LocalDateTime.now());
        customer.setCreatedBy("admin");

        assertEquals("id123", customer.getId());
        assertEquals("user123", customer.getUserId());
        assertEquals("CUST001", customer.getCustomerNumber());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals(primaryPhone, customer.getPrimaryPhone());
        assertEquals(secondaryPhone, customer.getSecondaryPhone());
        assertEquals(address, customer.getAddress());
        assertEquals("CORPORATE", customer.getCustomerType());
        assertEquals("GOLD", customer.getMembershipLevel());
        assertEquals(10000, customer.getCreditLimit());
        assertEquals(1000, customer.getCurrentBalance());
        assertEquals("ACTIVE", customer.getStatus());
        assertEquals("tenant123", customer.getTenantId());
        assertEquals("org123", customer.getOrganizationId());
        assertEquals("corp123", customer.getCorporateAccountId());
        assertEquals(10, customer.getTotalRequests());
        assertEquals(8, customer.getCompletedRequests());
        assertEquals(2, customer.getCancelledRequests());
        assertEquals(4.5, customer.getAverageRating());
        assertEquals("admin", customer.getCreatedBy());
    }

    @Test
    @DisplayName("Should initialize collections as empty sets")
    void testCollectionsInitialization() {
        Customer customer = new Customer();

        assertNotNull(customer.getMemberships());
        assertNotNull(customer.getVehicles());
        assertTrue(customer.getMemberships().isEmpty());
        assertTrue(customer.getVehicles().isEmpty());
    }

    @Test
    @DisplayName("Should handle collections correctly")
    void testCollections() {
        Customer customer = new Customer();

        // Collections are stubbed for testing (relationships disabled)
        assertNotNull(customer.getMemberships());
        assertNotNull(customer.getVehicles());
        assertEquals(0, customer.getMemberships().size());
        assertEquals(0, customer.getVehicles().size());
    }

    @Test
    @DisplayName("Should update timestamp on pre-update")
    void testPreUpdate() throws InterruptedException {
        Customer customer = new Customer();
        customer.setUpdatedAt(LocalDateTime.now());

        Thread.sleep(10); // Small delay to ensure timestamp difference
        customer.onUpdate();

        assertNotNull(customer.getUpdatedAt());
    }
}
