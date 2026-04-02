# Dynamic Routing Config Service - API Specification

## Base URL

```
Production: https://dynamic-routing-config-service.gogidix.com/api/v1
Development: http://localhost:8080/api/v1
```

## Authentication

All API requests require authentication via JWT bearer token or API key.

```
Authorization: Bearer <jwt-token>
```

## Endpoints

### Routing Rules

#### Create Routing Rule

Creates a new routing rule for a tenant.

```http
POST /routing-rules
Content-Type: application/json
Authorization: Bearer <token>

{
  "tenantId": "tenant-123",
  "ruleName": "api-gateway-route",
  "pattern": {
    "type": "PATH_PREFIX",
    "value": "/api/v1",
    "methods": ["GET", "POST", "PUT", "DELETE"]
  },
  "target": {
    "type": "HTTP",
    "endpoint": "http://backend-service:8080",
    "metadata": {}
  },
  "strategy": "ROUND_ROBIN",
  "priority": 10,
  "active": true,
  "environment": "production"
}
```

**Response:** 201 Created
```json
{
  "id": "route-abc-123",
  "tenantId": "tenant-123",
  "ruleName": "api-gateway-route",
  "pattern": {
    "type": "PATH_PREFIX",
    "value": "/api/v1",
    "methods": ["GET", "POST", "PUT", "DELETE"]
  },
  "target": {
    "type": "HTTP",
    "endpoint": "http://backend-service:8080",
    "metadata": {}
  },
  "strategy": "ROUND_ROBIN",
  "conditions": [],
  "config": {
    "timeoutMs": 30000,
    "retryAttempts": 3,
    "circuitBreakerEnabled": true,
    "circuitBreakerThreshold": 0.5,
    "rateLimitEnabled": false,
    "rateLimitPerSecond": 100
  },
  "priority": 10,
  "active": true,
  "environment": "production",
  "createdAt": "2024-01-21T00:00:00Z",
  "updatedAt": "2024-01-21T00:00:00Z",
  "version": 1
}
```

#### List Routing Rules

Retrieves a paginated list of routing rules for a tenant.

```http
GET /routing-rules?tenantId=tenant-123&environment=production&active=true&page=0&size=20
Authorization: Bearer <token>
```

**Response:** 200 OK
```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 45,
  "totalPages": 3,
  "first": true,
  "last": false,
  "empty": false
}
```

#### Get Routing Rule by ID

Retrieves a specific routing rule.

```http
GET /routing-rules/{ruleId}
Authorization: Bearer <token>
```

**Response:** 200 OK
```json
{
  "id": "route-abc-123",
  ...
}
```

#### Update Routing Rule

Updates an existing routing rule.

```http
PUT /routing-rules/{ruleId}
Content-Type: application/json
Authorization: Bearer <token>

{
  "target": {
    "type": "HTTP",
    "endpoint": "http://new-backend:8080",
    "metadata": {}
  },
  "priority": 5,
  "active": true,
  "reason": "Updated backend endpoint"
}
```

**Response:** 200 OK

#### Delete Routing Rule

Deletes a routing rule (must be deactivated first).

```http
DELETE /routing-rules/{ruleId}
Authorization: Bearer <token>
```

**Response:** 204 No Content

## Data Types

### RoutePattern

Represents the URL pattern to match.

```json
{
  "type": "PATH_PREFIX | PATH_REGEX | EXACT_PATH | WILDCARD",
  "value": "/api/v1/*",
  "methods": ["GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"]
}
```

### RouteTarget

Represents the target destination.

```json
{
  "type": "HTTP | SERVICE | GRPC",
  "endpoint": "http://backend-service:8080",
  "metadata": {
    "service": "backend-service"
  }
}
```

### RoutingStrategy

Enum of load balancing strategies:
- `ROUND_ROBIN` - Distribute requests sequentially
- `LEAST_CONNECTIONS` - Route to server with fewest active connections
- `IP_HASH` - Route based on client IP hash
- `HEADER_BASED` - Route based on header value
- `RANDOM` - Random selection

### Condition

Represents a routing condition.

```json
{
  "type": "HEADER | QUERY_PARAM | PATH_VARIABLE | CUSTOM",
  "key": "X-API-Version",
  "operator": "equals | contains | matches | starts_with | ends_with",
  "value": "v1"
}
```

### RouteConfig

Configuration for routing behavior.

```json
{
  "timeoutMs": 30000,
  "retryAttempts": 3,
  "circuitBreakerEnabled": true,
  "circuitBreakerThreshold": 0.5,
  "rateLimitEnabled": false,
  "rateLimitPerSecond": 100
}
```

## Error Responses

All error responses follow this format:

```json
{
  "timestamp": "2024-01-21T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/routing-rules",
  "tenantId": "tenant-123",
  "correlationId": "abc-123-def-456",
  "errors": [
    "Routing rule name cannot be blank",
    "Priority must be between 0 and 1000"
  ],
  "warnings": []
}
```

## Common HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Request successful, no content returned
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists or conflicts with state
- `500 Internal Server Error` - Server error
