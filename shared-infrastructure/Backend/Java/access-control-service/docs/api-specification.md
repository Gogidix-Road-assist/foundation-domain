# Access Control Service - API Specification

## Base URL

- Development: `http://localhost:8080/api/v1`
- Production: `https://api.rapidassist.com/access-control/api/v1`

## Authentication

All requests require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

## Tenant Context

All requests require tenant context provided via the `X-Tenant-ID` header:

```
X-Tenant-ID: tenant-123
```

## Endpoints

### Access Control

#### Check Access

Check if a subject is authorized to perform an action on a resource.

```http
POST /access/check
```

**Request Body:**
```json
{
  "subjectId": "user-456",
  "resource": "/api/v1/users",
  "action": "READ",
  "context": {}
}
```

**Response:**
```json
{
  "allowed": true,
  "reason": "ALLOW permission found: perm-123",
  "evaluatedAt": "2026-01-23T10:00:00Z",
  "tenantId": "tenant-123",
  "subjectId": "user-456",
  "resource": "/api/v1/users",
  "action": "READ"
}
```

### Permissions

#### Grant Permission

```http
POST /permissions/grant
```

**Request Body:**
```json
{
  "subjectId": "user-456",
  "subjectType": "USER",
  "resource": "/api/v1/users",
  "action": "READ",
  "effect": "ALLOW",
  "validUntil": null
}
```

#### Get Permissions for Subject

```http
GET /permissions/subject/{subjectId}
```

### Roles

#### Create Role

```http
POST /roles
```

**Request Body:**
```json
{
  "name": "ADMIN",
  "description": "Administrator role"
}
```

#### List Roles

```http
GET /roles
```

#### Assign Permission to Role

```http
POST /roles/{roleId}/permissions
```

**Request Body:**
```json
{
  "permissionId": "perm-123"
}
```

## Error Responses

| Status | Code | Description |
|--------|------|-------------|
| 400 | VALIDATION_ERROR | Invalid request parameters |
| 401 | UNAUTHORIZED | Missing or invalid credentials |
| 403 | FORBIDDEN | Insufficient permissions |
| 404 | NOT_FOUND | Resource not found |
| 500 | INTERNAL_ERROR | Server error |
