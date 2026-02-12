# Feature Flags Service - API Specification

## Base URL

```
Development: http://localhost:8080/api/v1
Production: https://feature-flags.gogidix.com/api/v1
```

## Authentication

All requests require:
- `Authorization: Bearer <JWT_TOKEN>` header
- `tenantId: <TENANT_ID>` header for multi-tenancy

## Endpoints

### Feature Flags

#### List Feature Flags

```http
GET /feature-flags
```

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Page size (max 100)
- `environment` (string) - Filter by environment
- `status` (string) - Filter by status (DRAFT, ACTIVE, INACTIVE, ARCHIVED)
- `type` (string) - Filter by type
- `enabled` (boolean) - Filter by enabled status
- `keyword` (string) - Search in key or name
- `sortBy` (string) - Sort field (default: updatedAt)
- `sortDirection` (string) - Sort direction (asc, desc, default: desc)

**Response:**
```json
{
  "data": [
    {
      "id": "string",
      "tenantId": "string",
      "key": "string",
      "name": "string",
      "description": "string",
      "enabled": true,
      "type": "BOOLEAN",
      "rolloutStrategy": "ALL_USERS",
      "environment": "production",
      "status": "ACTIVE",
      "createdBy": "string",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedBy": "string",
      "updatedAt": "2024-01-01T00:00:00Z",
      "version": 1
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

#### Get Feature Flag by ID

```http
GET /feature-flags/{id}
```

**Path Parameters:**
- `id` (string, required) - Feature flag ID

**Response:**
```json
{
  "id": "string",
  "tenantId": "string",
  "key": "string",
  "name": "string",
  "description": "string",
  "enabled": true,
  "type": "BOOLEAN",
  "rolloutStrategy": "ALL_USERS",
  "environment": "production",
  "status": "ACTIVE",
  "tags": ["tag1", "tag2"],
  "requiresApproval": false,
  "expiresAt": "2024-12-31T23:59:59Z",
  "createdBy": "string",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedBy": "string",
  "updatedAt": "2024-01-01T00:00:00Z",
  "version": 1
}
```

#### Get Feature Flag by Key

```http
GET /feature-flags/by-key
```

**Query Parameters:**
- `key` (string, required) - Feature flag key
- `environment` (string, required) - Environment

**Response:** Same as Get Feature Flag by ID

#### Create Feature Flag

```http
POST /feature-flags
```

**Request Body:**
```json
{
  "key": "new-feature-flag",
  "name": "New Feature Flag",
  "description": "Description of the feature flag",
  "type": "BOOLEAN",
  "rolloutStrategy": "ALL_USERS",
  "allowedTenants": ["tenant1", "tenant2"],
  "allowedUsers": ["user1", "user2"],
  "allowedCountries": ["US", "UK"],
  "percentageRollout": {
    "percentage": 50,
    "bucketingKey": "userId"
  },
  "environment": "production",
  "tags": ["experimental", "beta"],
  "requiresApproval": false,
  "expiresAt": "2024-12-31T23:59:59Z"
}
```

**Response:** `201 Created` with created feature flag object

#### Update Feature Flag

```http
PUT /feature-flags/{id}
```

**Request Body:**
```json
{
  "name": "Updated Name",
  "description": "Updated description",
  "enabled": true,
  "status": "ACTIVE",
  "rolloutStrategy": "PERCENTAGE",
  "percentageRollout": {
    "percentage": 75,
    "bucketingKey": "userId"
  },
  "tags": ["updated-tag"],
  "expiresAt": "2024-12-31T23:59:59Z"
}
```

**Response:** `200 OK` with updated feature flag object

#### Delete Feature Flag

```http
DELETE /feature-flags/{id}
```

**Response:** `204 No Content`

#### Enable/Disable Feature Flag

```http
POST /feature-flags/{id}/enable
POST /feature-flags/{id}/disable
```

**Request Body:**
```json
{
  "reason": "Enabling feature for production rollout"
}
```

**Response:** `200 OK` with updated feature flag

#### Evaluate Feature Flag

```http
POST /feature-flags/evaluate
```

**Request Body:**
```json
{
  "key": "new-feature-flag",
  "environment": "production",
  "userId": "user-123",
  "tenantId": "tenant-123",
  "countryCode": "US",
  "context": {
    "customAttribute": "value"
  }
}
```

**Response:**
```json
{
  "enabled": true,
  "reason": "User is in allowed list",
  "evaluationContext": {
    "rolloutStrategy": "SPECIFIC_USERS",
    "matched": true
  }
}
```

### Health Check

```http
GET /health
```

**Response:**
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

## Error Responses

All errors follow this format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "violations": [
    "Feature flag key is required",
    "Invalid rollout strategy for flag type"
  ],
  "path": "/api/v1/feature-flags",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Common HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created
- `204 No Content` - Deletion successful
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `422 Unprocessable Entity` - Business rule violation
- `500 Internal Server Error` - Server error

## Enums

### Feature Flag Type
- `BOOLEAN` - Simple on/off flag
- `MULTIVARIATE` - Multiple variations
- `KILL_SWITCH` - Emergency disable

### Rollout Strategy
- `ALL_USERS` - Available to everyone
- `SPECIFIC_TENANTS` - Whitelisted tenants
- `SPECIFIC_USERS` - Whitelisted users
- `PERCENTAGE` - Percentage-based rollout
- `COUNTRY_BASED` - Country-based targeting
- `GRADUAL` - Gradual rollout over time

### Feature Flag Status
- `DRAFT` - Not yet active
- `ACTIVE` - Currently active
- `INACTIVE` - Disabled but not deleted
- `ARCHIVED` - Archived and immutable
