# Policy Configuration Service API Specification

## Base URL

- Development: `http://localhost:8080/api/v1`
- Production: `https://policy-configuration.gogidix.com/api/v1`

## Authentication

All API requests require:
- Header: `Authorization: Bearer <token>`
- Header: `X-Tenant-Id: <tenant-id>`

## Endpoints

### Policies

#### List Policies

```
GET /policies
```

Query Parameters:
- `page` (int, default: 0)
- `size` (int, default: 20)
- `type` (PolicyType): SECURITY, PRIVACY, BUSINESS_RULE, COMPLIANCE, RATE_LIMIT, ACCESS_CONTROL
- `status` (PolicyStatus): DRAFT, ACTIVE, INACTIVE, ARCHIVED
- `enforced` (boolean)
- `environment` (string)
- `tags` (array of strings)
- `keyword` (string)

Response:
```json
{
  "content": [
    {
      "id": "string",
      "tenantId": "string",
      "policyKey": "string",
      "name": "string",
      "description": "string",
      "type": "SECURITY",
      "scope": {
        "type": "GLOBAL",
        "entityTypes": ["string"],
        "entityIds": ["string"],
        "attributes": {}
      },
      "rules": {},
      "constraints": {
        "maxRetries": 0,
        "timeoutMs": 0,
        "requireApproval": false,
        "requiredRoles": ["string"]
      },
      "enforced": true,
      "priority": 0,
      "environment": "string",
      "status": "DRAFT",
      "tags": ["string"],
      "createdBy": "string",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedBy": "string",
      "updatedAt": "2024-01-01T00:00:00Z",
      "version": 1
    }
  ],
  "currentPage": 0,
  "pageSize": 20,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

#### Create Policy

```
POST /policies
```

Request Body:
```json
{
  "policyKey": "string",
  "name": "string",
  "description": "string",
  "type": "SECURITY",
  "rules": {},
  "scope": {
    "type": "GLOBAL",
    "entityTypes": ["string"],
    "entityIds": ["string"],
    "attributes": {}
  },
  "constraints": {
    "maxRetries": 0,
    "timeoutMs": 0,
    "requireApproval": false,
    "requiredRoles": ["string"]
  },
  "enforced": true,
  "priority": 0,
  "environment": "string",
  "tags": ["string"]
}
```

Response: `201 Created` with PolicyResponse body

#### Get Policy by ID

```
GET /policies/{id}
```

Response: `200 OK` with PolicyResponse body

#### Update Policy

```
PUT /policies/{id}
```

Request Body: (all fields optional)
```json
{
  "name": "string",
  "description": "string",
  "rules": {},
  "scope": {},
  "constraints": {},
  "enforced": true,
  "priority": 0,
  "tags": ["string"]
}
```

Response: `200 OK` with PolicyResponse body

#### Delete Policy

```
DELETE /policies/{id}
```

Response: `204 No Content`

#### Change Policy Status

```
PATCH /policies/{id}/status
```

Request Body:
```json
{
  "status": "ACTIVE",
  "reason": "string"
}
```

Response: `200 OK` with PolicyResponse body

### Health Check

#### Health Status

```
GET /health
```

Response:
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
  "errors": [
    "Field 'policyKey' is required"
  ],
  "path": "/api/v1/policies",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

Common HTTP Status Codes:
- `200 OK`: Successful request
- `201 Created`: Resource created successfully
- `204 No Content`: Successful request with no response body
- `400 Bad Request`: Invalid request parameters
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., duplicate key)
- `500 Internal Server Error`: Server error

## Rate Limiting

API requests are rate limited:
- Default: 100 requests per minute per tenant
- Headers included in response:
  - `X-RateLimit-Limit`: Request limit
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Reset timestamp

## Tenant Isolation

All API requests are scoped to the tenant specified in the `X-Tenant-Id` header. Tenants cannot access or modify policies belonging to other tenants.
