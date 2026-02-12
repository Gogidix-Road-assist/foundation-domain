# Shared Request Context Library

A Spring Boot library for managing request context propagation in microservices architectures. Provides automatic extraction of tenant, user, and tracing context from HTTP headers and JWT tokens, with support for async thread propagation.

## Features

- **Automatic Context Extraction**: Extracts tenant, user, and tracing information from HTTP headers
- **JWT Token Integration**: Supports extracting context from JWT claims
- **Thread-Local Storage**: Thread-safe context storage using ThreadLocal
- **Async Propagation**: Automatic context propagation to async threads
- **Validation**: Built-in validation for tenant ID and request ID formats
- **MDC Integration**: Automatic SLF4J MDC population for logging
- **Multi-Tenancy Support**: First-class support for multi-tenant applications
- **Customizable Headers**: Configure custom header names for context propagation

## Installation

Add the dependency to your Spring Boot project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-request-context-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### 1. Add Required Headers

Send these headers with your HTTP requests:

```
X-Tenant-Id: tenant-123
X-User-Id: user-456
X-Request-Id: 123e4567-e89b-12d3-a456-426614174000
X-Correlation-Id: your-correlation-id
X-Country: US
```

### 2. Access Context in Your Code

```java
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;

@Service
public class MyService {

    public void doSomething() {
        RequestContextHolder.get().ifPresent(context -> {
            String tenantId = context.tenantId();
            String userId = context.userId();
            String requestId = context.requestId();
            String country = context.country();

            // Use context for business logic
            logger.info("Processing request for tenant: {}", tenantId);
        });
    }
}
```

### 3. Enable Async Support (Optional)

To enable async context propagation, add this to your `application.yml`:

```yaml
gogidix:
  request-context:
    async:
      enabled: true
```

Then use `@Async` with the configured executor:

```java
@Service
public class AsyncService {

    @Async("requestContextAsyncExecutor")
    public CompletableFuture<Void> asyncMethod() {
        RequestContextHolder.get().ifPresent(context -> {
            // Context is automatically available in async thread
            logger.info("Async processing for tenant: {}", context.tenantId());
        });
        return CompletableFuture.completedFuture(null);
    }
}
```

## Configuration

### Application Properties

Configure the library in your `application.yml`:

```yaml
gogidix:
  request-context:
    # Header names
    correlation-id-header: "X-Correlation-Id"
    tenant-id-header: "X-Tenant-Id"
    user-id-header: "X-User-Id"
    request-id-header: "X-Request-Id"
    country-header: "X-Country"

    # Required fields
    require-tenant-id: true
    require-user-id: false
    require-country: false

    # JWT integration
    prefer-jwt-claims: true
    enforce-header-jwt-match: true

    # JWT claim names
    tenant-id-claim: "tenant_id"
    tenant-id-claim-fallback: "tenantId"
    user-id-claim: "user_id"
    user-id-claim-fallback: "userId"
    country-claim: "country"
    country-claim-fallback: "country_code"

    # Async support
    async:
      enabled: false
```

### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `gogidix.request-context.correlation-id-header` | `X-Correlation-Id` | Header name for correlation ID |
| `gogidix.request-context.tenant-id-header` | `X-Tenant-Id` | Header name for tenant ID |
| `gogidix.request-context.user-id-header` | `X-User-Id` | Header name for user ID |
| `gogidix.request-context.request-id-header` | `X-Request-Id` | Header name for request ID |
| `gogidix.request-context.country-header` | `X-Country` | Header name for country |
| `gogidix.request-context.require-tenant-id` | `true` | Whether tenant ID is required |
| `gogidix.request-context.require-user-id` | `false` | Whether user ID is required |
| `gogidix.request-context.require-country` | `false` | Whether country is required |
| `gogidix.request-context.prefer-jwt-claims` | `true` | Prefer JWT claims over headers |
| `gogidix.request-context.enforce-header-jwt-match` | `true` | Enforce header/JWT match |
| `gogidix.request-context.async.enabled` | `false` | Enable async executor |

## Request Context Fields

The `RequestContext` contains the following fields:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `correlationId` | String | No | Correlation ID for distributed tracing |
| `tenantId` | String | Yes | Tenant identifier for multi-tenancy |
| `userId` | String | No | User identifier |
| `requestId` | String | No | Request identifier (must be valid UUID) |
| `country` | String | No | Country code |

## Header Requirements

### Standard Headers

```http
GET /api/resource HTTP/1.1
Host: api.example.com
X-Tenant-Id: tenant-123
X-User-Id: user-456
X-Request-Id: 123e4567-e89b-12d3-a456-426614174000
X-Correlation-Id: corr-abc-123
X-Country: US
```

### Header Validation

- **X-Tenant-Id**: Required (when `require-tenant-id: true`), non-empty string
- **X-Request-Id**: Optional, must be valid UUID format if provided
- **X-User-Id**: Optional (when `require-user-id: false`), string
- **X-Correlation-Id**: Optional, auto-generated if not provided
- **X-Country**: Optional, ISO country code

### UUID Format for Request ID

The `X-Request-Id` header must follow standard UUID format:

```
Valid:   123e4567-e89b-12d3-a456-426614174000
Invalid: 123, abc-123, not-a-uuid
```

## JWT Integration

When `prefer-jwt-claims: true`, the library extracts context from JWT tokens:

```json
{
  "tenant_id": "tenant-123",
  "user_id": "user-456",
  "country": "US"
}
```

### JWT Claim Configuration

```yaml
gogidix:
  request-context:
    prefer-jwt-claims: true
    enforce-header-jwt-match: true
    tenant-id-claim: "tenant_id"
    tenant-id-claim-fallback: "tenantId"
    user-id-claim: "user_id"
    user-id-claim-fallback: "userId"
    country-claim: "country"
    country-claim-fallback: "country_code"
```

### Header/JWT Enforcement

When `enforce-header-jwt-match: true`:
- Headers must match JWT claims if both are present
- Returns `400 Bad Request` if there's a mismatch

## Async Context Propagation

### Setup

1. Enable async support in configuration:

```yaml
gogidix:
  request-context:
    async:
      enabled: true
```

2. Use the configured executor:

```java
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "requestContextAsyncExecutor")
    public Executor requestContextAsyncExecutor() {
        // Auto-configured by the library
        return null;
    }
}
```

### Usage with @Async

```java
@Service
public class DocumentService {

    @Async("requestContextAsyncExecutor")
    public CompletableFuture<Document> processDocument() {
        RequestContextHolder.get().ifPresent(context -> {
            // Context is available here!
            String tenantId = context.tenantId();
            // Process document for tenant
        });
        return CompletableFuture.completedFuture(document);
    }
}
```

### Manual Task Decorator

You can also manually decorate tasks:

```java
import com.gogidix.rapidassist.shared.request.context.library.async.RequestContextTaskDecorator;

@Service
public class ManualAsyncService {

    private final RequestContextTaskDecorator decorator = new RequestContextTaskDecorator();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void submitTask() {
        Runnable task = () -> {
            RequestContextHolder.get().ifPresent(context -> {
                // Context available in worker thread
                System.out.println("Tenant: " + context.tenantId());
            });
        };

        executor.submit(decorator.decorate(task));
    }
}
```

## Validation

### RequestContext Validation

The `RequestContext` validates:

1. **Tenant ID**: Must not be null or blank
2. **Request ID**: If provided, must be valid UUID format

```java
RequestContext context = RequestContext.builder()
        .tenantId("tenant-123")
        .userId("user-456")
        .requestId(UUID.randomUUID().toString())
        .build(); // Throws IllegalStateException if validation fails
```

### Validation Errors

Common validation errors:

```
IllegalStateException: TenantId is required and cannot be blank
IllegalStateException: RequestId must be a valid UUID format: invalid-uuid
```

## MDC Integration

The library automatically populates SLF4J MDC with context values:

```java
import org.slf4j.MDC;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    public void logWithMdc() {
        // MDC is automatically populated
        logger.info("Processing request - tenant: {}, user: {}, correlation: {}",
            MDC.get("tenantId"),
            MDC.get("userId"),
            MDC.get("correlationId")
        );
    }
}
```

### Available MDC Keys

- `correlationId`: Correlation ID
- `tenantId`: Tenant ID
- `userId`: User ID
- `requestId`: Request ID
- `country`: Country code

## Testing

### Unit Tests

Test your services with mock context:

```java
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;

@Test
void testWithContext() {
    RequestContext context = RequestContext.builder()
            .tenantId("test-tenant")
            .userId("test-user")
            .build();

    RequestContextHolder.set(context);

    // Test your service
    myService.doSomething();

    RequestContextHolder.clear();
}
```

### Integration Tests with MockMvc

```java
@WebMvcTest(MyController.class)
class MyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWithContext() throws Exception {
        mockMvc.perform(get("/api/resource")
                .header("X-Tenant-Id", "tenant-123")
                .header("X-User-Id", "user-456"))
            .andExpect(status().isOk());
    }
}
```

## Error Handling

### Missing Required Context

```
HTTP 400 Bad Request
{
  "error": "Missing required tenant context"
}
```

### Invalid UUID Format

```
HTTP 400 Bad Request
{
  "error": "X-Request-Id must be a valid UUID"
}
```

### Header/JWT Mismatch

```
HTTP 400 Bad Request
{
  "error": "X-Tenant-Id does not match authenticated tenant"
}
```

## Best Practices

### 1. Always Clear Context

The library automatically clears context after requests, but for manual operations:

```java
try {
    RequestContextHolder.set(context);
    // Do work
} finally {
    RequestContextHolder.clear();
}
```

### 2. Use Optional Pattern

```java
RequestContextHolder.get().ifPresent(context -> {
    // Safely use context
});
```

### 3. Validation for User Input

```java
RequestContext context = RequestContextHolder.get()
    .orElseThrow(() -> new IllegalStateException("No request context"));
```

### 4. Async Operations

Always use the configured executor for async operations:

```java
@Async("requestContextAsyncExecutor")
public void asyncOperation() {
    // Context is automatically propagated
}
```

## Architecture

### Components

- **RequestContextFilter**: Servlet filter extracting context from headers/JWT
- **RequestContext**: Immutable record holding context data
- **RequestContextHolder**: ThreadLocal storage for context
- **RequestContextTaskDecorator**: Decorator for async context propagation
- **AsyncConfig**: Configuration for async executor

### Hexagonal Architecture

The library follows hexagonal architecture principles:

```
domain/
  ├── RequestContext.java          # Domain model
  └── RequestContextHolder.java    # State management

autoconfigure/
  ├── RequestContextFilter.java    # Input adapter
  ├── RequestContextProperties.java # Configuration
  └── AsyncConfig.java             # Async configuration

async/
  └── RequestContextTaskDecorator.java # Async support
```

## Migration Guide

### From Manual Context Management

Before:
```java
@RequestMapping("/api/resource")
public ResponseEntity<?> getResource(
        @RequestHeader("X-Tenant-Id") String tenantId) {
    // Manual parameter passing
    service.doSomething(tenantId);
}
```

After:
```java
@RequestMapping("/api/resource")
public ResponseEntity<?> getResource() {
    // Automatic context extraction
    service.doSomething();
}
```

### From Custom Async Solutions

Before:
```java
@Async
public void asyncMethod(String tenantId, String userId) {
    // Manual parameter passing
}
```

After:
```java
@Async("requestContextAsyncExecutor")
public void asyncMethod() {
    RequestContextHolder.get().ifPresent(context -> {
        String tenantId = context.tenantId();
        String userId = context.userId();
    });
}
```

## Troubleshooting

### Context Not Available

Problem: `RequestContextHolder.get()` returns empty

Solutions:
1. Ensure `X-Tenant-Id` header is sent
2. Check that `RequestContextFilter` is registered
3. Verify `gogidix.request-context.require-tenant-id` setting

### Context Not Propagated to Async Threads

Problem: Context not available in `@Async` methods

Solutions:
1. Enable async support: `gogidix.request-context.async.enabled=true`
2. Use `requestContextAsyncExecutor` bean
3. Apply `RequestContextTaskDecorator` to custom executors

### Validation Errors

Problem: `IllegalStateException: TenantId is required`

Solutions:
1. Ensure `X-Tenant-Id` header is present
2. Check header name matches configuration
3. Verify JWT claims if `prefer-jwt-claims: true`

## Contributing

See project contribution guidelines.

## License

Copyright © Gogidix. All rights reserved.
