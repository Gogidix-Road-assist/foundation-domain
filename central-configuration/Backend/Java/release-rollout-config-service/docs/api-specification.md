# Release Rollout Configuration Service - API Specification

## Base URL

- Development: `http://localhost:8082/api/v1`
- Production: `https://api.gogidix.com/rollout/api/v1`

## Authentication

All endpoints require Bearer token authentication:

```
Authorization: Bearer <token>
```

## Endpoints

### Rollout Operations

#### List Rollouts

```http
GET /rollouts
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `tenantId` | string | Yes | Tenant ID |
| `releaseId` | string | No | Filter by release ID |
| `status` | string | No | Filter by status (PLANNED, IN_PROGRESS, PAUSED, COMPLETED, ROLLED_BACK, FAILED) |
| `strategy` | string | No | Filter by strategy (BLUE_GREEN, CANARY, GRADUAL, BIG_BANG, AB_TESTING) |
| `environment` | string | No | Filter by environment |
| `page` | integer | No | Page number (default: 0) |
| `size` | integer | No | Page size (default: 20, max: 100) |

**Response:**

```json
{
  "content": [
    {
      "id": "507f1f77bcf86cd799439011",
      "tenantId": "tenant-123",
      "releaseId": "auth-service-1.0.0",
      "version": "1.0.0",
      "strategy": "CANARY",
      "config": {
        "batchSize": 10,
        "batchIntervalMinutes": 30,
        "initialPercentage": 10,
        "maxPercentage": 100,
        "targetSegments": ["all-users"],
        "criteria": null,
        "autoPromote": false,
        "requireApproval": false
      },
      "status": "PLANNED",
      "environment": "production",
      "createdBy": "user-123",
      "createdAt": "2025-01-21T10:00:00Z",
      "updatedBy": "user-123",
      "updatedAt": "2025-01-21T10:00:00Z",
      "recordVersion": 1
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

#### Create Rollout

```http
POST /rollouts
```

**Request Body:**

```json
{
  "tenantId": "tenant-123",
  "releaseId": "auth-service-1.0.0",
  "version": "1.0.0",
  "strategy": "CANARY",
  "config": {
    "batchSize": 10,
    "batchIntervalMinutes": 30,
    "initialPercentage": 10,
    "maxPercentage": 100,
    "targetSegments": ["all-users"],
    "criteria": null,
    "autoPromote": false,
    "requireApproval": false
  },
  "environment": "production",
  "createdBy": "user-123",
  "reason": "Initial rollout"
}
```

**Response:** `201 Created`

Returns the created rollout object (same format as List Rollouts response).

#### Get Rollout by ID

```http
GET /rollouts/{id}
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | string | Yes | Rollout ID |

**Response:** `200 OK`

Returns the rollout object.

#### Update Rollout Status

```http
PUT /rollouts/{id}
```

**Request Body:**

```json
{
  "newStatus": "IN_PROGRESS",
  "updatedBy": "user-123",
  "reason": "Starting canary deployment"
}
```

**Response:** `200 OK`

Returns the updated rollout object.

#### Delete Rollout

```http
DELETE /rollouts/{id}
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | string | Yes | Rollout ID |

**Response:** `204 No Content`

### Health Check

```http
GET /health
```

**Response:**

```json
{
  "status": "UP"
}
```

## Error Responses

All error responses follow this format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "Release ID is required",
    "Version must follow semantic versioning"
  ],
  "warnings": [],
  "path": "/api/v1/rollouts",
  "timestamp": "2025-01-21T10:00:00Z"
}
```

## HTTP Status Codes

| Code | Description |
|------|-------------|
| `200` | Success |
| `201` | Created |
| `204` | No Content |
| `400` | Bad Request |
| `401` | Unauthorized |
| `404` | Not Found |
| `500` | Internal Server Error |

## Rollout Strategies

### Blue-Green Deployment

Two identical production environments. Traffic switches from blue to green.

```json
{
  "strategy": "BLUE_GREEN",
  "config": {
    "batchSize": 100,
    "batchIntervalMinutes": 0,
    "initialPercentage": 0,
    "maxPercentage": 0,
    "targetSegments": [],
    "criteria": null,
    "autoPromote": false,
    "requireApproval": true
  }
}
```

### Canary Deployment

Gradually roll out to a small percentage of users.

```json
{
  "strategy": "CANARY",
  "config": {
    "batchSize": 10,
    "batchIntervalMinutes": 30,
    "initialPercentage": 10,
    "maxPercentage": 100,
    "targetSegments": ["all-users"],
    "criteria": null,
    "autoPromote": false,
    "requireApproval": false
  }
}
```

### Gradual Rollout

Progressive rollout with configurable batch sizes.

```json
{
  "strategy": "GRADUAL",
  "config": {
    "batchSize": 20,
    "batchIntervalMinutes": 60,
    "initialPercentage": 20,
    "maxPercentage": 100,
    "targetSegments": ["all-users"],
    "criteria": null,
    "autoPromote": false,
    "requireApproval": false
  }
}
```

### Big-Bang Deployment

Instant rollout to all users.

```json
{
  "strategy": "BIG_BANG",
  "config": {
    "batchSize": 100,
    "batchIntervalMinutes": 0,
    "initialPercentage": 100,
    "maxPercentage": 100,
    "targetSegments": [],
    "criteria": null,
    "autoPromote": true,
    "requireApproval": true
  }
}
```

### A/B Testing

Split traffic between versions based on segments.

```json
{
  "strategy": "AB_TESTING",
  "config": {
    "batchSize": 50,
    "batchIntervalMinutes": 0,
    "initialPercentage": 50,
    "maxPercentage": 50,
    "targetSegments": ["segment-a", "segment-b"],
    "criteria": "conversion_rate > 0.05",
    "autoPromote": false,
    "requireApproval": false
  }
}
```
