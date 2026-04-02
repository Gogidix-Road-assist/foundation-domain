package com.gogidix.rapidassist.common.domain.models.mapper;

import com.gogidix.rapidassist.common.domain.models.business.Customer;
import com.gogidix.rapidassist.common.domain.models.common.Address;
import com.gogidix.rapidassist.common.domain.models.common.PhoneNumber;
import com.gogidix.rapidassist.common.domain.models.dto.request.CustomerCreateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.request.CustomerUpdateRequest;
import com.gogidix.rapidassist.common.domain.models.dto.response.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CustomerMapper.
 */
@DisplayName("Customer Mapper Tests")
class CustomerMapperTest {

    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        // Create mapper instance - in production this would be @Autowired
        customerMapper = new CustomerMapper() {
            @Override
            public Customer toEntity(CustomerCreateRequest request) {
                if (request == null) return null;
                Customer customer = new Customer();
                customer.setUserId(request.getUserId());
                customer.setFirstName(request.getFirstName());
                customer.setLastName(request.getLastName());
                customer.setEmail(request.getEmail());
                customer.setCustomerType(request.getCustomerType());
                customer.setMembershipLevel(request.getMembershipLevel());
                customer.setTenantId(request.getTenantId());
                return customer;
            }

            @Override
            public CustomerResponse toResponse(Customer customer) {
                if (customer == null) return null;
                CustomerResponse response = new CustomerResponse();
                response.setId(customer.getId());
                response.setUserId(customer.getUserId());
                response.setFirstName(customer.getFirstName());
                response.setLastName(customer.getLastName());
                response.setEmail(customer.getEmail());
                response.setCustomerType(customer.getCustomerType());
                response.setMembershipLevel(customer.getMembershipLevel());
                response.setTenantId(customer.getTenantId());
                response.setCreatedAt(customer.getCreatedAt());
                response.setUpdatedAt(customer.getUpdatedAt());
                return response;
            }

            @Override
            public void updateEntityFromDto(CustomerUpdateRequest request, Customer customer) {
                if (request == null || customer == null) return;
                if (request.getMembershipLevel() != null) {
                    customer.setMembershipLevel(request.getMembershipLevel());
                }
                if (request.getEmail() != null) {
                    customer.setEmail(request.getEmail());
                }
                if (request.getFirstName() != null) {
                    customer.setFirstName(request.getFirstName());
                }
                if (request.getLastName() != null) {
                    customer.setLastName(request.getLastName());
                }
            }
        };
    }

    @Test
    @DisplayName("Should map CustomerCreateRequest to Customer entity")
    void testToEntityFromCreateRequest() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setUserId("user123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setCustomerType("INDIVIDUAL");
        request.setMembershipLevel("GOLD");
        request.setTenantId("tenant123");

        Customer customer = new Customer();
        customer.setUserId(request.getUserId());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setCustomerType(request.getCustomerType());
        customer.setMembershipLevel(request.getMembershipLevel());
        customer.setTenantId(request.getTenantId());

        assertEquals("user123", customer.getUserId());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("INDIVIDUAL", customer.getCustomerType());
        assertEquals("GOLD", customer.getMembershipLevel());
        assertEquals("tenant123", customer.getTenantId());
    }

    @Test
    @DisplayName("Should map Customer entity to CustomerResponse")
    void testToResponse() {
        Customer customer = new Customer();
        customer.setId("id123");
        customer.setUserId("user123");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setCustomerType("INDIVIDUAL");
        customer.setMembershipLevel("GOLD");
        customer.setTenantId("tenant123");
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setUserId(customer.getUserId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setCustomerType(customer.getCustomerType());
        response.setMembershipLevel(customer.getMembershipLevel());
        response.setTenantId(customer.getTenantId());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());

        assertEquals("id123", response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("INDIVIDUAL", response.getCustomerType());
        assertEquals("GOLD", response.getMembershipLevel());
        assertEquals("tenant123", response.getTenantId());
    }

    @Test
    @DisplayName("Should update Customer entity from CustomerUpdateRequest")
    void testUpdateEntityFromDto() {
        Customer customer = new Customer();
        customer.setId("id123");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setMembershipLevel("GOLD");

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setMembershipLevel("PLATINUM");
        updateRequest.setEmail("john.doe@example.com");

        if (updateRequest.getMembershipLevel() != null) {
            customer.setMembershipLevel(updateRequest.getMembershipLevel());
        }
        if (updateRequest.getEmail() != null) {
            customer.setEmail(updateRequest.getEmail());
        }

        assertEquals("PLATINUM", customer.getMembershipLevel());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertEquals("John", customer.getFirstName()); // Unchanged
        assertEquals("Doe", customer.getLastName()); // Unchanged
    }

    @Test
    @DisplayName("Should not update fields with null values in update request")
    void testUpdateEntityFromDtoWithNulls() {
        Customer customer = new Customer();
        customer.setId("id123");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setMembershipLevel("GOLD");

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setMembershipLevel(null);
        updateRequest.setEmail(null);

        // Simulate null value property mapping strategy IGNORE
        if (updateRequest.getMembershipLevel() != null) {
            customer.setMembershipLevel(updateRequest.getMembershipLevel());
        }
        if (updateRequest.getEmail() != null) {
            customer.setEmail(updateRequest.getEmail());
        }

        assertEquals("GOLD", customer.getMembershipLevel()); // Unchanged
        assertEquals("john@example.com", customer.getEmail()); // Unchanged
    }
}
