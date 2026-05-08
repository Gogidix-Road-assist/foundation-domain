# Error Code Catalog

This document provides a comprehensive list of error codes used across the Rapid Assist platform.

## Standard Error Responses

All error responses follow the RFC 7807 (Problem Details for HTTP APIs) specification:

```json
{
  "type": "https://gogidix.com/problems/error-type",
  "title": "Error title",
  "status": 400,
  "detail": "Detailed error message",
  "instance": "/api/resource/123",
  "timestamp": "2026-01-12T10:30:00Z",
  "path": "/api/resource/123",
  "correlationId": "abc-123-def",
  "tenantId": "tenant-001"
}
```

## HTTP Status Codes

| Status | Title | Description | Exception |
|--------|-------|-------------|-----------|
| 400 | Bad Request | Invalid request parameters or body | `BadRequestException` |
| 401 | Unauthorized | Authentication required or failed | `UnauthorizedException` |
| 403 | Forbidden | User lacks permission for resource | `ForbiddenException` |
| 404 | Not Found | Resource does not exist | `NotFoundException` |
| 409 | Conflict | Resource state conflict | `ConflictException` |
| 422 | Unprocessable Entity | Semantic errors in request | `BadRequestException` |
| 500 | Internal Server Error | Unexpected server error | `Exception` |
| 503 | Service Unavailable | Service temporarily unavailable | `ServiceUnavailableException` |

## Error Types

### 1. Validation Errors (400)

**Type**: `https://gogidix.com/problems/validation`

Common validation errors:

| Error Code | Description | Example |
|------------|-------------|---------|
| VALIDATION_FAILED | General validation failure | Request body validation failed |
| INVALID_FORMAT | Invalid format for field | Date must be ISO-8601 format |
| MISSING_REQUIRED_FIELD | Required field is missing | Email is required |
| INVALID_VALUE | Value is out of valid range | Age must be between 18 and 120 |
| DUPLICATE_VALUE | Value must be unique | Email already exists |

Example response:
```json
{
  "type": "https://gogidix.com/problems/validation",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields failed validation",
  "errors": {
    "email": "Email is required",
    "age": "Age must be between 18 and 120"
  }
}
```

### 2. Authentication Errors (401)

**Type**: `https://gogidix.com/problems/unauthorized`

| Error Code | Description | Solution |
|------------|-------------|----------|
| MISSING_CREDENTIALS | No auth credentials provided | Provide valid token |
| INVALID_TOKEN | Token is invalid | Refresh or re-authenticate |
| TOKEN_EXPIRED | Token has expired | Refresh token |
| TOKEN_REVOKED | Token has been revoked | Re-authenticate |

Example:
```json
{
  "type": "https://gogidix.com/problems/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Authentication token has expired",
  "errorCode": "TOKEN_EXPIRED"
}
```

### 3. Authorization Errors (403)

**Type**: `https://gogidix.com/problems/forbidden`

| Error Code | Description | Example |
|------------|-------------|---------|
| INSUFFICIENT_PERMISSIONS | User lacks required permission | Need admin role |
| RESOURCE_ACCESS_DENIED | Cannot access specific resource | Not your customer |
| ACTION_NOT_ALLOWED | Action not allowed for this resource | Cannot delete active service request |

Example:
```json
{
  "type": "https://gogidix.com/problems/forbidden",
  "title": "Access forbidden",
  "status": 403,
  "detail": "You don't have permission to delete resource 'customer-123'",
  "resourceId": "customer-123",
  "action": "DELETE",
  "requiredPermission": "customer.delete"
}
```

### 4. Not Found Errors (404)

**Type**: `https://gogidix.com/problems/not-found`

| Error Code | Description | Example |
|------------|-------------|---------|
| ENTITY_NOT_FOUND | Entity not found | Customer not found |
| RESOURCE_NOT_FOUND | General resource not found | Endpoint not found |
| USER_NOT_FOUND | User not found | User ID not found |
| TENANT_NOT_FOUND | Tenant not found | Tenant not provisioned |

Example:
```json
{
  "type": "https://gogidix.com/problems/not-found",
  "title": "Resource not found",
  "status": 404,
  "detail": "Customer with id 'customer-123' was not found",
  "entityType": "Customer",
  "entityId": "customer-123"
}
```

### 5. Conflict Errors (409)

**Type**: `https://gogidix.com/problems/conflict`

| Error Code | Description | Example |
|------------|-------------|---------|
| RESOURCE_ALREADY_EXISTS | Resource already exists | Email already registered |
| CONCURRENT_MODIFICATION | Resource modified concurrently | Optimistic lock failure |
| STATE_CONFLICT | Invalid state transition | Cannot cancel completed request |
| VERSION_CONFLICT | Version mismatch | Update with stale data |

Example:
```json
{
  "type": "https://gogidix.com/problems/conflict",
  "title": "Resource conflict",
  "status": 409,
  "detail": "Customer with email 'john@example.com' already exists",
  "entityType": "Customer",
  "field": "email",
  "value": "john@example.com"
}
```

### 6. Service Unavailable Errors (503)

**Type**: `https://gogidix.com/problems/service-unavailable`

| Error Code | Description | Retry |
|------------|-------------|-------|
| SERVICE_DOWN | Service is down | Yes, after 60s |
| SERVICE_TIMEOUT | Service timeout | Yes, after 30s |
| RATE_LIMIT_EXCEEDED | Too many requests | Yes, after retry-after |
| MAINTENANCE_MODE | Service under maintenance | Yes, after maintenance |

Example:
```json
{
  "type": "https://gogidix.com/problems/service-unavailable",
  "title": "Service unavailable",
  "status": 503,
  "detail": "Payment Gateway: Rate limit exceeded",
  "service": "Payment Gateway",
  "retryAfter": 300
}
```

## Domain-Specific Error Codes

### Customer Domain

| Error Code | Status | Description |
|------------|--------|-------------|
| CUSTOMER_NOT_FOUND | 404 | Customer not found |
| CUSTOMER_EMAIL_EXISTS | 409 | Email already registered |
| CUSTOMER_HAS_ACTIVE_REQUESTS | 409 | Cannot delete customer with active requests |
| CUSTOMER_INACTIVE | 400 | Customer account is inactive |

### Service Request Domain

| Error Code | Status | Description |
|------------|--------|-------------|
| REQUEST_NOT_FOUND | 404 | Service request not found |
| REQUEST_INVALID_STATUS | 400 | Invalid status transition |
| REQUEST_NO_PROVIDER_AVAILABLE | 409 | No providers available |
| REQUEST_ALREADY_CANCELLED | 409 | Request already cancelled |

### Provider Domain

| Error Code | Status | Description |
|------------|--------|-------------|
| PROVIDER_NOT_FOUND | 404 | Provider not found |
| PROVIDER_NOT_AVAILABLE | 409 | Provider not available |
| PROVIDER_ALREADY_ASSIGNED | 409 | Provider already assigned |
| PROVIDER_INVALID_LICENSE | 400 | Provider license expired |

## Error Code Format

Error codes follow the format: `DOMAIN_SPECIFIC_ERROR`

Examples:
- `CUSTOMER_NOT_FOUND`
- `REQUEST_INVALID_STATUS`
- `PROVIDER_LICENSE_EXPIRED`

## Best Practices

### 1. Use Specific Error Codes

Use specific error codes instead of generic ones:

```java
// Good
throw new NotFoundException("Customer", "customer-123");
// Returns: entityType=Customer, entityId=customer-123

// Avoid
throw new Exception("Not found");
```

### 2. Include Helpful Details

Provide actionable error messages:

```java
// Good
throw new ConflictException("Customer", "email", "john@example.com");
// Returns: Customer with email 'john@example.com' already exists

// Avoid
throw new ConflictException("Duplicate");
```

### 3. Use Retry-After for Transient Errors

For temporary failures, suggest retry time:

```java
throw new ServiceUnavailableException("API", "Rate limited", 60);
// Returns: Retry-After: 60 header
```

### 4. Include Correlation ID

Always include correlation ID for tracing:

```json
{
  "correlationId": "abc-123-def",
  "tenantId": "tenant-001"
}
```

## Adding New Error Codes

When adding new error codes:

1. Create exception class if needed
2. Add error code constant
3. Document in this catalog
4. Add tests for the error
5. Update API documentation

Example:
```java
public class MyCustomException extends ErrorResponseException {
    public static final String ERROR_CODE = "MY_CUSTOM_ERROR";

    public MyCustomException(String detail) {
        super(HttpStatus.BAD_REQUEST);
        this.setTitle("My Custom Error");
        this.setDetail(detail);
        this.setProperty("errorCode", ERROR_CODE);
    }
}
```

## Support

For questions about error codes or to report issues, contact the platform team.
