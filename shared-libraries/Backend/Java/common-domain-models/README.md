# Common Domain Models

Shared domain entities and DTOs for the Gogidix Rapid Assist Platform.

## Overview

This library provides core domain models for the Road Assist platform, including:
- Customer entities and DTOs
- ServiceRequest entities and DTOs
- Provider entities and DTOs
- Vehicle entities and DTOs
- Value objects (Address, PhoneNumber, EmailAddress, Money, GeoLocation)
- Spring Data JPA repositories
- MapStruct mappers for entity-DTO conversion

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **MapStruct 1.5.5** for entity-DTO mapping
- **Hibernate** for ORM
- **PostgreSQL** for production
- **H2** for testing

## Installation

Add as a dependency in your Maven project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>common-domain-models</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Entity Models

### Customer

Represents a customer in the Road Assist platform.

**Key Fields:**
- `id`: Unique identifier (UUID)
- `userId`: Link to system user
- `customerNumber`: Customer number
- `firstName`, `lastName`: Customer name
- `email`: Email address
- `primaryPhone`, `secondaryPhone`: Phone numbers
- `address`: Customer address
- `customerType`: INDIVIDUAL, CORPORATE, GOVERNMENT
- `membershipLevel`: STANDARD, SILVER, GOLD, PLATINUM
- `status`: ACTIVE, INACTIVE, SUSPENDED, BLOCKED
- `tenantId`: Multi-tenant identifier

**Business Methods:**
- `getFullName()`: Returns concatenated first and last name
- `isMembershipActive()`: Checks if membership is valid
- `incrementCompletedRequests()`: Increments request counters

### ServiceRequest

Represents a roadside assistance request.

**Key Fields:**
- `id`: Unique identifier (UUID)
- `requestNumber`: Request number
- `customerId`: Customer ID
- `vehicleId`, `vehicleVin`, `vehicleRegistration`: Vehicle information
- `serviceType`: TOWING, JUMP_START, TIRE_CHANGE, FUEL_DELIVERY, LOCKOUT, etc.
- `priorityLevel`: LOW, NORMAL, HIGH, EMERGENCY
- `urgencyLevel`: NON_URGENT, URGENT, CRITICAL
- `status`: PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
- `location`, `address`: Service location
- `assignedProviderId`, `assignedDriverId`: Assignment information
- `estimatedCost`, `actualCost`, `quotedPrice`: Cost information
- `paymentStatus`: PENDING, PAID, FAILED, REFUNDED
- `tenantId`: Multi-tenant identifier

**Business Methods:**
- `isAssigned()`: Checks if provider is assigned
- `isInProgress()`: Checks if request is in progress
- `isCompleted()`: Checks if request is completed
- `isCancelled()`: Checks if request is cancelled

### Provider

Represents a service provider.

**Key Fields:**
- `id`: Unique identifier (UUID)
- `userId`: Link to system user
- `providerNumber`: Provider number
- `businessName`, `legalName`: Business information
- `contactPerson`: Primary contact
- `email`: Email address
- `primaryPhone`, `secondaryPhone`: Phone numbers
- `address`, `headquartersLocation`: Location information
- `taxId`, `licenseNumber`: Tax and license information
- `businessType`: INDIVIDUAL, PARTNERSHIP, CORPORATION, LLC
- `status`: Provider status
- `tenantId`: Multi-tenant identifier

### Vehicle

Represents a customer's vehicle.

**Key Fields:**
- `id`: Unique identifier (UUID)
- `customerId`: Customer ID
- `vin`: Vehicle identification number
- `registrationNumber`: Registration number
- `make`, `model`, `year`: Vehicle details
- `color`: Vehicle color
- `vehicleType`: SEDAN, SUV, TRUCK, VAN, MOTORCYCLE, BUS, OTHER
- `fuelType`, `transmission`: Technical specifications
- `mileage`: Current mileage
- `status`: Vehicle status
- `tenantId`: Multi-tenant identifier

## Value Objects

### Address

Represents a postal address.

**Fields:**
- `streetAddress`, `streetAddressLine2`: Street information
- `city`: City name
- `state`: State/Province
- `postalCode`: Postal/ZIP code
- `country`: Country name
- `latitude`, `longitude`: GPS coordinates

### PhoneNumber

Represents a phone number with country code.

**Fields:**
- `countryCode`: Country code (e.g., +1)
- `areaCode`: Area code
- `phoneNumber`: Phone number
- `extension`: Extension (optional)
- `phoneType`: MOBILE, HOME, WORK, etc.

### Money

Represents a monetary amount.

**Fields:**
- `amount`: Amount in smallest currency unit (cents)
- `currency`: Currency code (e.g., USD)

### GeoLocation

Represents a GPS location.

**Fields:**
- `latitude`: Latitude coordinate
- `longitude`: Longitude coordinate
- `altitude`: Altitude (optional)

## DTOs

### Request DTOs

Used for creating and updating entities:

- `CustomerCreateRequest`, `CustomerUpdateRequest`
- `ServiceRequestCreateRequest`, `ServiceRequestUpdateRequest`
- `ProviderCreateRequest`
- `VehicleCreateRequest`

All request DTOs include validation annotations:
- `@NotBlank`: Required fields
- `@Email`: Email validation
- `@Pattern`: Enum pattern validation
- `@Size`: Length constraints

### Response DTOs

Used for API responses:

- `CustomerResponse`
- `ServiceRequestResponse`
- `ProviderResponse`
- `VehicleResponse`

All response DTOs:
- Include JSON formatting for dates
- Use builder pattern where applicable
- Exclude sensitive internal fields

## Repositories

Spring Data JPA repositories are provided for all entities:

### CustomerRepository

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByTenantId(String tenantId);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByCustomerNumber(String customerNumber);
    Optional<Customer> findByUserId(String userId);
    List<Customer> findByCustomerType(String customerType);
    List<Customer> findByMembershipLevel(String membershipLevel);
    List<Customer> findByStatus(String status);
    List<Customer> findByTenantIdAndStatus(String tenantId, String status);
    List<Customer> findExpiredMemberships();
    long countByTenantId(String tenantId);
    boolean existsByEmail(String email);
    boolean existsByCustomerNumber(String customerNumber);
}
```

### ServiceRequestRepository

```java
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, String> {
    List<ServiceRequest> findByCustomerId(String customerId);
    List<ServiceRequest> findByStatus(String status);
    List<ServiceRequest> findByServiceType(String serviceType);
    List<ServiceRequest> findByPriorityLevel(String priorityLevel);
    List<ServiceRequest> findByTenantId(String tenantId);
    List<ServiceRequest> findByAssignedProviderId(String providerId);
    List<ServiceRequest> findPendingRequestsOrderByPriority();
    List<ServiceRequest> findUrgentRequests();
    long countByCustomerId(String customerId);
    long countByStatus(String status);
}
```

### VehicleRepository

```java
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByCustomerId(String customerId);
    Optional<Vehicle> findByVin(String vin);
    List<Vehicle> findByMakeAndModel(String make, String model);
    List<Vehicle> findByVehicleType(String vehicleType);
    List<Vehicle> findByTenantId(String tenantId);
    long countByCustomerId(String customerId);
    boolean existsByVin(String vin);
}
```

### ProviderRepository

```java
@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    List<Provider> findByTenantId(String tenantId);
    Optional<Provider> findByUserId(String userId);
    Optional<Provider> findByProviderNumber(String providerNumber);
    Optional<Provider> findByEmail(String email);
    List<Provider> findByStatus(String status);
    List<Provider> findByTenantIdAndStatus(String tenantId, String status);
    List<Provider> searchByBusinessName(String businessName);
    List<Provider> findActiveProvidersWithMinRating(Double minRating);
    long countByTenantId(String tenantId);
    boolean existsByEmail(String email);
}
```

## MapStruct Mappers

Entity-DTO mapping is handled by MapStruct mappers:

### CustomerMapper

```java
@Autowired
private CustomerMapper customerMapper;

// Create entity from request
CustomerCreateRequest request = new CustomerCreateRequest();
request.setFirstName("John");
request.setLastName("Doe");
request.setEmail("john@example.com");

Customer customer = customerMapper.toEntity(request);

// Create response from entity
CustomerResponse response = customerMapper.toResponse(customer);

// Update entity from request
CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
updateRequest.setMembershipLevel("PLATINUM");

customerMapper.updateEntityFromDto(updateRequest, customer);
```

### ServiceRequestMapper

```java
@Autowired
private ServiceRequestMapper serviceRequestMapper;

ServiceRequest request = serviceRequestMapper.toEntity(createRequest);
ServiceRequestResponse response = serviceRequestMapper.toResponse(request);
serviceRequestMapper.updateEntityFromDto(updateRequest, request);
```

### VehicleMapper

```java
@Autowired
private VehicleMapper vehicleMapper;

Vehicle vehicle = vehicleMapper.toEntity(createRequest);
VehicleResponse response = vehicleMapper.toResponse(vehicle);
```

### ProviderMapper

```java
@Autowired
private ProviderMapper providerMapper;

Provider provider = providerMapper.toEntity(createRequest);
ProviderResponse response = providerMapper.toResponse(provider);
```

## Usage Examples

### Creating a Customer

```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        // Validate request (automatic with @Valid)
        // Convert to entity
        Customer customer = customerMapper.toEntity(request);

        // Save to database
        Customer savedCustomer = customerRepository.save(customer);

        // Convert to response
        return customerMapper.toResponse(savedCustomer);
    }
}
```

### Finding Customers by Tenant

```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerResponse> findCustomersByTenant(String tenantId) {
        List<Customer> customers = customerRepository.findByTenantId(tenantId);
        return customers.stream()
            .map(customerMapper::toResponse)
            .collect(Collectors.toList());
    }
}
```

### Creating a Service Request

```java
@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ServiceRequestMapper serviceRequestMapper;

    public ServiceRequestResponse createServiceRequest(ServiceRequestCreateRequest request) {
        ServiceRequest serviceRequest = serviceRequestMapper.toEntity(request);
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        return serviceRequestMapper.toResponse(saved);
    }
}
```

### Checking Customer Business Logic

```java
@Service
public class MembershipService {

    @Autowired
    private CustomerRepository customerRepository;

    public boolean isMembershipValid(String customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Customer not found"));

        return customer.isMembershipActive();
    }
}
```

## Multi-Tenancy

All entities support multi-tenancy through the `tenantId` field. When querying data, always filter by tenant:

```java
@Service
public class TenantAwareService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getTenantCustomers(String tenantId) {
        return customerRepository.findByTenantId(tenantId);
    }
}
```

## Testing

The library includes comprehensive tests:

- **Unit Tests**: Entity business logic, value objects
- **Repository Tests**: @DataJpaTest with H2 database
- **Coverage**: Target 85%+ code coverage

Run tests:

```bash
mvn test
```

Run tests with coverage:

```bash
mvn test jacoco:report
```

View coverage report:

```bash
open target/site/jacoco/index.html
```

## Database Schema

Entities use standard JPA annotations:

- `@Entity`: Marks entity classes
- `@Table`: Table name and indexes
- `@Id`: Primary key with UUID generation
- `@Column`: Column configuration
- `@OneToMany`: One-to-many relationships
- `@Embedded`: Embedded value objects
- `@PreUpdate`: Lifecycle callbacks

## Validation

Request DTOs use Jakarta Bean Validation:

```java
@Service
public class CustomerService {

    public CustomerResponse createCustomer(@Valid CustomerCreateRequest request) {
        // Validation is automatic
        // Throws MethodArgumentNotValidException on validation errors
    }
}
```

## Best Practices

1. **Always use DTOs** for API requests/responses, never expose entities directly
2. **Filter by tenantId** in all queries for multi-tenant isolation
3. **Use repository methods** instead of custom JPQL when possible
4. **Leverage MapStruct** for entity-DTO conversion
5. **Validate input** using Bean Validation annotations
6. **Use business methods** on entities (e.g., `isMembershipActive()`)
7. **Handle nulls** properly with Optional and null checks

## Contributing

When contributing to this library:

1. Follow Java 21 and Spring Boot 3.3.5 best practices
2. Add comprehensive tests for new features
3. Maintain 85%+ code coverage
4. Update this README with new entities or features
5. Use MapStruct for all entity-DTO mappings
6. Add JavaDoc to all public APIs

## License

Copyright (c) 2026 Gogidix. All rights reserved.
