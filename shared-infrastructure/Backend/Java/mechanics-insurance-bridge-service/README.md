# Mechanics-Insurance Bridge Service

Cross-domain integration service connecting Mechanics and Insurance domains for claim validation and processing.

## Overview

This service acts as a bridge between the Mechanics and Insurance domains, enabling:
- **Claim Validation**: Insurance domain validates claims against mechanic service details
- **Claim Creation**: Mechanics domain creates insurance claims after service completion
- **Claim Status Tracking**: Mechanics domain tracks insurance claim status
- **Service Details Update**: Mechanics domain provides service completion details to insurance

## Port: 8096

## Tech Stack

- **Spring Boot 3.3.5** with Java 21
- **Spring WebFlux**: Reactive HTTP client
- **Resilience4j**: Circuit breaker and retry patterns
- **Eureka Client**: Service discovery

## Architecture

```
┌─────────────────────┐
│   Insurance Domain  │
│                     │
│  Claims Service     │
│  (8092)             │
└──────────┬──────────┘
           │
           │ validateClaim()
           │ createClaim()
           │ updateClaim()
           │
           ▼
┌──────────────────────────────────┐
│  Mechanics-Insurance Bridge       │
│  Service (8096)                   │
│                                    │
│  • Workshop approval validation   │
│  • Mechanic certification check   │
│  • Service cost validation        │
│  • Policy coverage validation     │
│  • Claim status tracking          │
└──────────┬───────────────────────┘
           │
           │ validateWorkshop()
           │ validateMechanic()
           │ validateCosts()
           │
           ▼
┌─────────────────────┐
│   Mechanics Domain  │
│                     │
│  Workshop Booking   │
│  (8089)             │
└─────────────────────┘
```

## Configuration

### application.yml

```yaml
server:
  port: 8096

services:
  insurance:
    claims-service: http://localhost:8092
    policy-service: http://localhost:8083

  mechanics:
    base-url: http://localhost:8089

resilience4j:
  circuitbreaker:
    instances:
      insuranceService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
      mechanicsService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
```

## API Endpoints

### Claim Validation (Called by Insurance)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bridge/mechanics-insurance/claims/validate | Validate claim with mechanics details |

### Claim Operations (Called by Mechanics)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bridge/mechanics-insurance/claims/create | Create insurance claim from mechanics booking |
| GET | /api/bridge/mechanics-insurance/claims/{id}/status | Get claim status |
| PUT | /api/bridge/mechanics-insurance/claims/{id}/service-details | Update claim with service completion details |

## API Examples

### Validate Claim (Insurance → Mechanics)

```bash
curl -X POST http://localhost:8096/api/bridge/mechanics-insurance/claims/validate \
  -H "Content-Type: application/json" \
  -d '{
    "claimId": "CLM-001",
    "policyNumber": "POL-001",
    "customerId": "CUST-001",
    "vehicleRegistration": "12-D-12345",
    "serviceType": "REPAIR",
    "workshopId": "WS-001",
    "mechanicId": "MECH-001",
    "serviceItems": [
      {
        "itemCode": "BRAKE-PAD-SET",
        "description": "Brake Pad Set (Front)",
        "quantity": 1,
        "unitPrice": 89.99,
        "totalPrice": 89.99
      },
      {
        "itemCode": "LABOR-2HRS",
        "description": "Labor - 2 hours",
        "quantity": 2,
        "unitPrice": 60.00,
        "totalPrice": 120.00
      }
    ],
    "estimatedCost": 209.99,
    "description": "Front brake replacement",
    "incidentDate": "2025-12-25T10:00:00",
    "incidentLocation": "Dublin City Center",
    "submittedBy": "WS-001"
  }'
```

**Response:**
```json
{
  "claimId": "CLM-001",
  "valid": true,
  "validationResult": {
    "policyActive": true,
    "coverageValid": true,
    "withinLimit": true,
    "workshopApproved": true,
    "coverageType": "COMPREHENSIVE",
    "coverageLimit": 5000.00,
    "remainingLimit": 4790.01,
    "deductible": 200.00
  },
  "status": "APPROVED",
  "approvedAmount": 9.99,
  "issues": [],
  "validatedAt": "2025-12-25T10:30:00"
}
```

### Create Claim from Mechanics (Mechanics → Insurance)

```bash
curl -X POST http://localhost:8096/api/bridge/mechanics-insurance/claims/create \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": "BK-001",
    "policyNumber": "POL-001",
    "claimDetails": {
      "claimId": "CLM-001",
      "customerId": "CUST-001",
      "serviceType": "REPAIR",
      "workshopId": "WS-001",
      "mechanicId": "MECH-001",
      "estimatedCost": 209.99
    }
  }'
```

### Get Claim Status

```bash
curl -X GET http://localhost:8096/api/bridge/mechanics-insurance/claims/CLM-001/status
```

**Response:** `"APPROVED"`

### Update Claim with Service Details

```bash
curl -X PUT http://localhost:8096/api/bridge/mechanics-insurance/claims/CLM-001/service-details \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": "BK-001",
    "mechanicId": "MECH-001",
    "workshopId": "WS-001",
    "servicesPerformed": [
      {
        "itemCode": "BRAKE-PAD-SET",
        "description": "Brake Pad Set (Front)",
        "quantity": 1,
        "unitPrice": 89.99,
        "totalPrice": 89.99
      }
    ],
    "totalCost": 209.99,
    "completionTime": "2025-12-25T14:00:00",
    "photoUrls": ["s3://bucket/photos/after-repair.jpg"],
    "notes": "Brake pads replaced successfully"
  }'
```

## Claim Validation Flow

```
┌─────────────┐
│   Insurance │
 │  Domain    │
 └──────┬─────┘
        │ 1. Submit claim for validation
        │
        ▼
┌─────────────────────────┐
│ Bridge Service          │
│                         │
│ • Verify workshop        │
│ • Verify mechanic        │
│ • Validate costs         │
│ • Check policy coverage  │
│ • Calculate approval     │
└──────────┬──────────────┘
           │ 2. Validation result
           │
           ▼
┌─────────────────────────┐
│  Validation Response     │
│                         │
│ • Valid/Invalid         │
│ • Approved amount       │
│ • Coverage details      │
│ • Issues (if any)       │
└─────────────────────────┘
```

## Circuit Breaker & Resilience

The service uses Resilience4j for fault tolerance:

### Circuit Breaker
- Opens after 50% failure rate
- Waits 10 seconds before attempting recovery
- Fallback returns PENDING status with error message

### Retry
- 3 attempts with 1 second delay
- Applied to all external service calls

### Fallback Behavior

When a service is unavailable:
- **validateClaim**: Returns `valid=false`, `status=PENDING`
- **createClaim**: Returns `status=PENDING` with retry message
- **getClaimStatus**: Returns `"UNKNOWN"`
- **updateClaim**: Returns `status=PENDING` with retry message

## Validation Rules

### Workshop Validation
- Workshop must be registered in Mechanics domain
- Workshop must have "APPROVED" status for insurance work

### Mechanic Validation
- Mechanic must be certified
- Mechanic must be active

### Cost Validation
- Unit price must be > 0
- Total price per item < €10,000
- Costs compared against market rates (future enhancement)

### Policy Validation
- Policy must be active
- Coverage must be valid for service type
- Cost must be within remaining coverage limit

## Claim Status Flow

```
PENDING → VALIDATING → APPROVED/REJECTED/REQUIRES_INFO
                                ↓
                         PAYMENT_PROCESSING
                                ↓
                              COMPLETED
```

## Error Handling

| Error Code | Description | HTTP Status |
|------------|-------------|-------------|
| WORKSHOP_NOT_APPROVED | Workshop not approved for insurance | 400 |
| MECHANIC_NOT_CERTIFIED | Mechanic not certified | 400 |
| COSTS_EXCEED_LIMITS | Service costs too high | 400 |
| POLICY_NOT_FOUND | Policy does not exist | 404 |
| POLICY_EXPIRED | Policy has expired | 400 |
| COVERAGE_LIMIT_EXCEEDED | Cost exceeds coverage limit | 400 |
| SERVICE_UNAVAILABLE | Bridge service down | 503 |

## Monitoring & Observability

The service integrates with:
- **Eureka**: Service registration
- **Audit Library**: All cross-domain calls logged
- **Request Context**: Distributed tracing

Metrics exposed:
- `claim_validations_total`
- `claim_creations_total`
- `claim_status_checks_total`
- `circuit_breaker_open_count`
- `retry_attempts_total`

## Building and Running

```bash
# Build
mvn clean package

# Run
java -jar target/mechanics-insurance-bridge-service-1.0.0.jar

# Or with Spring Boot Maven plugin
mvn spring-boot:run
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVICES_INSURANCE_CLAIMS-SERVICE | Insurance claims service URL | http://localhost:8092 |
| SERVICES_INSURANCE_POLICY-SERVICE | Insurance policy service URL | http://localhost:8083 |
| SERVICES_MECHANICS_BASE-URL | Mechanics service URL | http://localhost:8089 |

## Future Enhancements

- [ ] Add market rate database for cost validation
- [ ] Implement async claim validation with messaging queue
- [ ] Add claim document validation
- [ ] Support multiple workshops per claim
- [ ] Add claim estimation API
- [ ] Implement claim approval workflow
- [ ] Add analytics dashboard
- [ ] Support bulk claim validation
