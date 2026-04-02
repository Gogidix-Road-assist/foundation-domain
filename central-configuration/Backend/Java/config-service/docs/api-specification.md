# Config Service API Specification

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

## Configuration Endpoints

### Create Configuration

```http
POST /api/v1/configurations
Content-Type: application/json

{
  "tenantId": "tenant-123",
  "configKey": "app.feature.enabled",
  "environment": "production",
  "namespace": "default",
  "value": true,
  "dataType": "BOOLEAN",
  "encrypted": false,
  "required": true,
  "description": "Enable new feature",
  "tags": ["feature", "ui"],
  "createdBy": "admin@tenant.com"
}
```

**Response:** `201 Created`

### Get Configuration

```http
GET /api/v1/configurations/{tenantId}/{configKey}/{environment}/{namespace}
```

**Response:** `200 OK`

```json
{
  "id": "507f1f77bcf86cd799439011",
  "tenantId": "tenant-123",
  "configKey": "app.feature.enabled",
  "environment": "production",
  "namespace": "default",
  "version": 1,
  "value": true,
  "dataType": "BOOLEAN",
  "encrypted": false,
  "required": true,
  "status": "ACTIVE",
  "createdAt": "2024-01-20T10:00:00Z"
}
```

### List Configurations by Tenant

```http
GET /api/v1/configurations/tenant/{tenantId}
```

**Response:** `200 OK`

```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

### Update Configuration

```http
PUT /api/v1/configurations/{tenantId}/{configKey}/{environment}/{namespace}
Content-Type: application/json

{
  "value": "new-value",
  "reason": "Feature flag update"
}
```

**Response:** `200 OK`

### Delete Configuration

```http
DELETE /api/v1/configurations/{tenantId}/{configKey}/{environment}/{namespace}
```

**Response:** `204 No Content`

### Bulk Update Configurations

```http
POST /api/v1/configurations/bulk-update
Content-Type: application/json

{
  "tenantId": "tenant-123",
  "updates": [
    {"configKey": "app.timeout", "environment": "prod", "namespace": "default", "value": 30},
    {"configKey": "app.maxSize", "environment": "prod", "namespace": "default", "value": 1000}
  ],
  "updatedBy": "admin@tenant.com",
  "reason": "Performance tuning"
}
```

**Response:** `200 OK`

---

## Health Endpoints

### Health Check

```http
GET /actuator/health
```

**Response:** `200 OK`

```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "redis": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

---

## Data Types

| Type | Description | Example |
|------|-------------|---------|
| STRING | Text value | `"hello"` |
| NUMBER | Numeric value | `123` or `123.45` |
| BOOLEAN | Boolean value | `true` or `false` |
| JSON | JSON object | `{"key": "value"}` |
| YAML | YAML string | `"key: value"` |
| ARRAY | Array of values | `[1, 2, 3]` |
| OBJECT | Map/Object | `{"nested": "value"}` |
| BINARY | Base64 encoded binary | `"YWJjMTIz"` |

---

## Error Responses

All errors return a consistent format:

```json
{
  "timestamp": "2024-01-20T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Configuration key is required",
  "path": "/api/v1/configurations",
  "tenantId": "tenant-123",
  "correlationId": "abc-123-def",
  "validationErrors": []
}
```

| Status | Error | Description |
|--------|-------|-------------|
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Configuration not found |
| 409 | Conflict | Configuration already exists |
| 500 | Internal Server Error | Server error |
