package com.gogidix.rapidassist.common.domain.models.business;

import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.GeoLocation;
import com.gogidix.rapidassist.common.domain.models.common.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for ServiceRequest entity.
 * Tests entity validation, business logic, and status checks.
 */
@DisplayName("ServiceRequest Entity Tests")
class ServiceRequestTest {

    @Test
    @DisplayName("Should create service request with default constructor")
    void testDefaultConstructor() {
        ServiceRequest request = new ServiceRequest();

        assertNotNull(request);
        assertNotNull(request.getCreatedAt());
        assertEquals("PENDING", request.getStatus());
        assertEquals("NORMAL", request.getPriorityLevel());
        assertEquals("PENDING", request.getPaymentStatus());
    }

    @Test
    @DisplayName("Should identify assigned requests correctly")
    void testIsAssigned() {
        ServiceRequest request = new ServiceRequest();
        request.setAssignedProviderId(null);

        assertFalse(request.isAssigned());

        request.setAssignedProviderId("provider123");
        assertTrue(request.isAssigned());
    }

    @Test
    @DisplayName("Should identify in-progress requests correctly")
    void testIsInProgress() {
        ServiceRequest request = new ServiceRequest();
        request.setStatus("IN_PROGRESS");

        assertTrue(request.isInProgress());

        request.setStatus("PENDING");
        assertFalse(request.isInProgress());
    }

    @Test
    @DisplayName("Should identify completed requests correctly")
    void testIsCompleted() {
        ServiceRequest request = new ServiceRequest();
        request.setStatus("COMPLETED");

        assertTrue(request.isCompleted());

        request.setStatus("IN_PROGRESS");
        assertFalse(request.isCompleted());
    }

    @Test
    @DisplayName("Should identify cancelled requests correctly")
    void testIsCancelled() {
        ServiceRequest request = new ServiceRequest();
        request.setStatus("CANCELLED");

        assertTrue(request.isCancelled());

        request.setStatus("PENDING");
        assertFalse(request.isCancelled());
    }

    @Test
    @DisplayName("Should handle equality based on ID")
    void testEquality() {
        ServiceRequest request1 = new ServiceRequest();
        request1.setId("123");

        ServiceRequest request2 = new ServiceRequest();
        request2.setId("123");

        assertEquals(request1, request2);
    }

    @Test
    @DisplayName("Should not be equal when IDs are different")
    void testInequality() {
        ServiceRequest request1 = new ServiceRequest();
        request1.setId("123");

        ServiceRequest request2 = new ServiceRequest();
        request2.setId("456");

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should handle all getters and setters")
    void testAllGettersAndSetters() {
        ServiceRequest request = new ServiceRequest();
        GeoLocation location = new GeoLocation();
        Address address = new Address();
        Money estimatedCost = new Money();

        request.setId("id123");
        request.setRequestNumber("REQ001");
        request.setCustomerId("customer123");
        request.setVehicleId("vehicle123");
        request.setVehicleVin("VIN123");
        request.setVehicleRegistration("REG123");
        request.setServiceType("TOWING");
        request.setPriorityLevel("HIGH");
        request.setUrgencyLevel("CRITICAL");
        request.setStatus("ASSIGNED");
        request.setLocation(location);
        request.setAddress(address);
        request.setLocationDescription("Near highway");
        request.setPhoneNumber("1234567890");
        request.setDescription("Car broke down");
        request.setNotes("Please hurry");
        request.setEstimatedArrival(LocalDateTime.now().plusHours(1));
        request.setActualArrival(LocalDateTime.now().plusHours(1));
        request.setCompletedAt(LocalDateTime.now().plusHours(2));
        request.setCancellationReason("Customer cancelled");
        request.setAssignedProviderId("provider123");
        request.setAssignedDriverId("driver123");
        request.setAssignedAt(LocalDateTime.now());
        request.setEstimatedCost(estimatedCost);
        request.setPaymentStatus("PAID");
        request.setPaymentReference("PAY123");
        request.setTenantId("tenant123");
        request.setOrganizationId("org123");
        request.setRating(5);
        request.setFeedback("Great service");
        request.setCreatedBy("admin");

        assertEquals("id123", request.getId());
        assertEquals("REQ001", request.getRequestNumber());
        assertEquals("TOWING", request.getServiceType());
        assertEquals("HIGH", request.getPriorityLevel());
        assertEquals("CRITICAL", request.getUrgencyLevel());
        assertEquals("ASSIGNED", request.getStatus());
        assertEquals("PAID", request.getPaymentStatus());
        assertTrue(request.isAssigned());
    }

    @Test
    @DisplayName("Should initialize collections as empty sets")
    void testCollectionsInitialization() {
        ServiceRequest request = new ServiceRequest();

        assertNotNull(request.getUpdates());
        assertNotNull(request.getPhotos());
        assertTrue(request.getUpdates().isEmpty());
        assertTrue(request.getPhotos().isEmpty());
    }
}
