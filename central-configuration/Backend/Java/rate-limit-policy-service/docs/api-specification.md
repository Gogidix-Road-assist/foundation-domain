# Rate Limit Policy Service API Specification

## Base URL
```
/api/v1
```

## Authentication
All endpoints require JWT authentication with tenant context.

```
Authorization: Bearer <token>
X-Tenant-ID: <tenant-id>
```

---

## Rate Limit Policy Endpoints

### Create Rate Limit Policy

```http
POST /api/v1/rate-limit-policies
Content-Type: application/json

{
  "tenantId": "tenant-123",
  "policyKey": "api.rate-limit.standard",
  "name": "Standard API Rate Limit",
  "description": "Standard rate limit for API endpoints",
  "limitType": "API_KEY_BASED",
  "config": {
    "requestsPerMinute": 60,
    "requestsPerHour": 1000,
    "requestsPerDay": 10000,
    "burstCapacity": 10,
    "windowSizeMs": 60000,
    "algorithm": "token-bucket"
  },
  "scope": {
    "endpoints": ["/api/*"],
    "userIds": [],
    "apiKeys": [],
    "attributes": {}
  },
  "enabled": true,
  "environment": "production"
}
```

**Response:** `201 Created`

```json
{
  "id": "507f1f77bcf86cd799439011",
  "tenantId": "tenant-123",
  "policyKey": "api.rate-limit.standard",
  "name": "Standard API Rate Limit",
  "description": "Standard rate limit for API endpoints",
  "limitType": "API_KEY_BASED",
  "config": {
    "requestsPerMinute": 60,
    "requestsPerHour": 1000,
    "requestsPerDay": 10000,
    "burstCapacity": 10,
    "windowSizeMs": 60000,
    "algorithm": "token-bucket"
  },
  "scope": {
    "endpoints": ["/api/*"],
    "userIds": [],
    "apiKeys": [],
    "attributes": {}
  },
  "enabled": true,
  "environment": "production",
  "version": 1,
  "createdAt": "2024-01-20T10:00:00Z",
  "updatedAt": "2024-01-20T10:00:00Z"
}
```

### Get Rate Limit Policy

```http
GET /api/v1/rate-limit-policies/{policyId}
```

**Response:** `200 OK`

```json
{
  "id": "507f1f77bcf86cd799439011",
  "tenantId": "tenant-123",
  "policyKey": "api.rate-limit.standard",
  "name": "Standard API Rate Limit",
  "limitType": "API_KEY_BASED",
  "enabled": true,
  "environment": "production",
  "version": 1
}
```

### List Rate Limit Policies

```http
GET /api/v1/rate-limit-policies?page=0&size=20&environment=production&enabled=true
```

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 20) - Page size
- `environment` (optional) - Filter by environment
- `enabled` (optional) - Filter by enabled status
- `limitType` (optional) - Filter by limit type

**Response:** `200 OK`

```json
{
  "content": [...],
  "currentPage": 0,
  "pageSize": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false,
  "empty": false
}
```

### Update Rate Limit Policy

```http
PUT /api/v1/rate-limit-policies/{policyId}
Content-Type: application/json

{
  "name": "Updated API Rate Limit",
  "description": "Updated description",
  "config": {
    "requestsPerMinute": 120,
    "requestsPerHour": 2000,
    "requestsPerDay": 20000,
    "burstCapacity": 20,
    "windowSizeMs": 60000,
    "algorithm": "token-bucket"
  },
  "scope": {
    "endpoints": ["/api/v2/*"],
    "userIds": [],
    "apiKeys": [],
    "attributes": {}
  }
}
```

**Response:** `200 OK`

### Delete Rate Limit Policy

```http
DELETE /api/v1/rate-limit-policies/{policyId}
```

**Response:** `204 No Content`

---

## Limit Types

| Type | Description |
|------|-------------|
| IP_BASED | Rate limit by client IP address |
| USER_BASED | Rate limit by user ID |
| API_KEY_BASED | Rate limit by API key |
| TENANT_BASED | Rate limit by tenant |
| GLOBAL | Global rate limit across all clients |

## Rate Limit Algorithms

| Algorithm | Description |
|-----------|-------------|
| token-bucket | Token bucket algorithm |
| leaky-bucket | Leaky bucket algorithm |
| fixed-window | Fixed time window |
| sliding-window | Sliding time window |
| sliding-log | Sliding log algorithm |

---

## Error Responses

### 400 Bad Request

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    "Policy key cannot be blank",
    "Requests per minute must be positive"
  ],
  "timestamp": "2024-01-20T10:00:00Z"
}
```

### 404 Not Found

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Rate limit policy not found",
  "timestamp": "2024-01-20T10:00:00Z"
}
```

### 409 Conflict

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Policy with key 'api.rate.limit' already exists",
  "timestamp": "2024-01-20T10:00:00Z"
}
```

---

## Health Check

```http
GET /api/v1/actuator/health
```

**Response:** `200 OK`

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "kafka": { "status": "UP" }
  }
}
```
