# Matching Service API Documentation

## Overview
The Matching Service is responsible for finding and matching providers to assistance requests using multiple algorithms.

**Base URL**: `http://localhost:8090/api`

## Matching Operations

### Create Matching Request
```http
POST /v1/matching/requests
Content-Type: application/json

{
  "incidentId": "INC-123",
  "algorithm": "BEST_FIT",
  "incidentLocation": {
    "coordinates": [-73.9857, 40.7484],
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "serviceType": "ROADSIDE_ASSISTANCE",
  "requiredCapabilities": ["TOWING", "TIRE_CHANGE"],
  "priority": 5,
  "maxDistanceKm": 50.0,
  "maxCost": 500.0,
  "minProviderRating": 4
}
```

**Response**: 201 Created
```json
{
  "requestId": "req-123",
  "incidentId": "INC-123",
  "algorithm": "BEST_FIT",
  "topProvider": {
    "providerId": "prov-123",
    "providerName": "ABC Towing",
    "score": 0.95,
    "distanceKm": 5.2,
    "estimatedCost": 150.0,
    "rating": 4.8,
    "estimatedArrival": "2026-02-06T12:30:00",
    "matchedCapabilities": ["TOWING", "TIRE_CHANGE"],
    "rank": 1
  },
  "totalProviders": 10,
  "processingTimeMs": 245.5,
  "status": "SUCCESS"
}
```

### Get Matching Result
```http
GET /v1/matching/results/{requestId}
```

### Re-score Providers
```http
POST /v1/matching/requests/{requestId}/rescore?algorithm=NEAREST
```

### Cancel Matching Request
```http
POST /v1/matching/requests/{requestId}/cancel
```

### Check Provider Availability
```http
GET /v1/matching/providers/{providerId}/availability
```

## Provider Management

### Register Provider
```http
POST /v1/providers
Content-Type: application/json

{
  "providerId": "prov-123",
  "providerName": "ABC Towing",
  "currentLocation": {
    "coordinates": [-73.9857, 40.7484]
  },
  "capabilities": ["TOWING", "TIRE_CHANGE"],
  "status": "AVAILABLE",
  "baseRate": 50.0,
  "ratePerKm": 2.0,
  "rating": 4.5,
  "maxConcurrentJobs": 5
}
```

### Update Provider Location
```http
PUT /v1/providers/{providerId}/location
Content-Type: application/json

{
  "longitude": -73.9857,
  "latitude": 40.7484
}
```

### Update Provider Status
```http
PUT /v1/providers/{providerId}/status?status=BUSY
```

### Find Nearby Providers
```http
GET /v1/providers/nearby?longitude=-73.9857&latitude=40.7484&radiusKm=50
```

### Find Providers by Capability
```http
GET /v1/providers/capability/TOWING
```

## Algorithms

### NEAREST
Finds the geographically closest providers based on distance.

### BEST_FIT
Finds providers with the best overall fit using weighted scoring across:
- Distance (30%)
- Capabilities (30%)
- Availability (20%)
- Rating (20%)

### LEAST_COST
Finds the most cost-effective providers based on estimated service cost.

### PRIORITY_BASED
Finds providers based on priority levels and overall score.

## Error Responses

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "status": 400,
  "timestamp": "2026-02-06T12:00:00",
  "details": {
    "incidentId": "Incident ID is required"
  }
}
```

## Health Check
```http
GET /v1/matching/health
```

Returns: `200 OK` with message "Matching service is healthy"
