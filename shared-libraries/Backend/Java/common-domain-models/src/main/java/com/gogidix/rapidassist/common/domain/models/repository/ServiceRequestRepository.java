package com.gogidix.rapidassist.common.domain.models.repository;

import com.gogidix.rapidassist.common.domain.models.business.ServiceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data MongoDB repository for ServiceRequest entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface ServiceRequestRepository extends MongoRepository<ServiceRequest, String> {

    /**
     * Finds service requests by customer ID.
     * @param customerId the customer ID
     * @return list of service requests for the customer
     */
    List<ServiceRequest> findByCustomerId(String customerId);

    /**
     * Finds service requests by vehicle ID.
     * @param vehicleId the vehicle ID
     * @return list of service requests for the vehicle
     */
    List<ServiceRequest> findByVehicleId(String vehicleId);

    /**
     * Finds service requests by status.
     * @param status the status (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)
     * @return list of service requests with the specified status
     */
    List<ServiceRequest> findByStatus(String status);

    /**
     * Finds service requests by service type.
     * @param serviceType the service type (TOWING, JUMP_START, TIRE_CHANGE, etc.)
     * @return list of service requests with the specified type
     */
    List<ServiceRequest> findByServiceType(String serviceType);

    /**
     * Finds service requests by priority level.
     * @param priorityLevel the priority level (LOW, NORMAL, HIGH, EMERGENCY)
     * @return list of service requests with the specified priority
     */
    List<ServiceRequest> findByPriorityLevel(String priorityLevel);

    /**
     * Finds service requests by tenant ID.
     * @param tenantId the tenant ID
     * @return list of service requests belonging to the tenant
     */
    List<ServiceRequest> findByTenantId(String tenantId);

    /**
     * Finds service requests by tenant ID and status.
     * @param tenantId the tenant ID
     * @param status the status
     * @return list of service requests matching both criteria
     */
    List<ServiceRequest> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Finds service requests by assigned provider ID.
     * @param providerId the provider ID
     * @return list of service requests assigned to the provider
     */
    List<ServiceRequest> findByAssignedProviderId(String providerId);

    /**
     * Finds service requests by assigned driver ID.
     * @param driverId the driver ID
     * @return list of service requests assigned to the driver
     */
    List<ServiceRequest> findByAssignedDriverId(String driverId);

    /**
     * Finds service requests created within a date range.
     * @param startDate the start date
     * @param endDate the end date
     * @return list of service requests created in the date range
     */
    List<ServiceRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds pending service requests ordered by priority and creation date.
     * @return list of pending service requests
     */
    List<ServiceRequest> findByStatusOrderByPriorityDescCreatedAtAsc(String status);

    /**
     * Finds service requests that need attention (high priority or urgent).
     * @return list of urgent service requests
     */
    List<ServiceRequest> findByPriorityLevelAndStatusNotIn(String priorityLevel, List<String> statuses);

    /**
     * Finds service requests by payment status.
     * @param paymentStatus the payment status (PENDING, PAID, FAILED, REFUNDED)
     * @return list of service requests with the specified payment status
     */
    List<ServiceRequest> findByPaymentStatus(String paymentStatus);

    /**
     * Counts service requests by customer ID.
     * @param customerId the customer ID
     * @return the count of service requests
     */
    long countByCustomerId(String customerId);

    /**
     * Counts service requests by status.
     * @param status the status
     * @return the count of service requests
     */
    long countByStatus(String status);

    /**
     * Counts service requests by tenant ID.
     * @param tenantId the tenant ID
     * @return the count of service requests
     */
    long countByTenantId(String tenantId);

    /**
     * Finds service requests by request number.
     * @param requestNumber the request number
     * @return list of service requests with the matching number
     */
    List<ServiceRequest> findByRequestNumber(String requestNumber);
}
