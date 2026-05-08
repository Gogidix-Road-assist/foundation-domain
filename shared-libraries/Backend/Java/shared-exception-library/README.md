# Shared Exception Library

A comprehensive exception handling library for Spring Boot 3.3.5+ applications. Provides standardized error responses following RFC 7807 (Problem Details for HTTP APIs) specification.

## Features

- **Custom Exception Types**: Pre-built exceptions for common HTTP error scenarios
- **RFC 7807 Support**: Standardized problem detail responses
- **Global Exception Handler**: Automatic exception handling with `@RestControllerAdvice`
- **Context Enrichment**: Automatic correlation ID and tenant ID inclusion
- **Validation Error Handling**: Structured validation error responses
- **Multi-Tenant Support**: Tenant-aware error responses

## Installation

Add the dependency to your project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-exception-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Throwing Exceptions

Simply throw the appropriate exception from your controllers or services:

```java
import com.gogidix.rapidassist.shared.exception.library.exception.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable String id) {
        return customerService.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer", id));
    }

    @PostMapping
    public Customer createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        if (customerService.existsByEmail(request.getEmail())) {
            throw new ConflictException("Customer", "email", request.getEmail());
        }
        return customerService.create(request);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable String id) {
        if (!authorizationService.canDelete(id)) {
            throw new ForbiddenException(id, "DELETE");
        }
        customerService.delete(id);
    }
}
```

### Available Exception Types

| Exception | HTTP Status | Use Case |
|-----------|-------------|----------|
| `NotFoundException` | 404 | Resource not found |
| `BadRequestException` | 400 | Invalid request data |
| `ConflictException` | 409 | Resource state conflict |
| `ForbiddenException` | 403 | Insufficient permissions |
| `UnauthorizedException` | 401 | Authentication failed |
| `ServiceUnavailableException` | 503 | Service temporarily unavailable |

## Response Format

All error responses follow RFC 7807 Problem Details format:

```json
{
  "type": "https://gogidix.com/problems/not-found",
  "title": "Resource not found",
  "status": 404,
  "detail": "Customer with id 'customer-123' was not found",
  "instance": "/api/customers/customer-123",
  "timestamp": "2026-01-12T10:30:00Z",
  "path": "/api/customers/customer-123",
  "correlationId": "abc-123-def",
  "tenantId": "tenant-001"
}
```

## Support

For issues and questions, please open an issue in the repository.
