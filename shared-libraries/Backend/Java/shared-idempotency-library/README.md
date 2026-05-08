# Shared Idempotency Library

A Spring Boot library that provides HTTP idempotency support for APIs. Idempotency ensures that multiple identical requests have the same effect as a single request, preventing duplicate operations and improving reliability.

## Features

- **Multiple Storage Backends**: Support for Redis, Database, and No-Op implementations
- **Automatic Idempotency Key Management**: Servlet filter for automatic key extraction
- **Configurable TTL**: Time-to-live for idempotency records (default: 24 hours)
- **Request Hash Validation**: Optional validation of request content to prevent misuse
- **Comprehensive Metrics**: Micrometer metrics for monitoring idempotency operations
- **Status Tracking**: Track requests as IN_PROGRESS, COMPLETED, or FAILED
- **Automatic Cleanup**: Scheduled cleanup of expired database records
- **Spring Boot 3.3+**: Built for Spring Boot 3.3.5 and Java 21

## Installation

Add the dependency to your project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-idempotency-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

### Basic Configuration

```yaml
gogidix:
  idempotency:
    # Store type: redis, database, or omit for no-op (default)
    store: redis

    # HTTP header name for idempotency key (default: Idempotency-Key)
    key-header: Idempotency-Key

    # Whether idempotency key is required (default: false)
    required: false

    # Whether to validate request hash (default: false)
    enforce-request-hash: false

    # HTTP status code for conflict responses (default: 409)
    conflict-status-code: 409

    # HTTP status code for in-progress responses (default: 409)
    in-progress-status-code: 409

    # Time-to-live for idempotency records (default: 24h)
    ttl: 24h
```

### Redis Configuration

When using Redis as the idempotency store:

```yaml
gogidix:
  idempotency:
    store: redis
    ttl: 24h

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Database Configuration

When using Database as the idempotency store:

```yaml
gogidix:
  idempotency:
    store: database
    ttl: 24h
    database:
      # Enable automatic cleanup of expired records (default: true)
      auto-cleanup: true

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

The library will automatically create the `idempotency_keys` table:

```sql
CREATE TABLE idempotency_keys (
    id VARCHAR(255) PRIMARY KEY,
    idempotency_key VARCHAR(500) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    request_hash VARCHAR(64),
    response_status INTEGER,
    response_hash VARCHAR(64),
    response_body TEXT,
    response_headers TEXT,
    expires_at TIMESTAMP
);

CREATE INDEX idx_idempotency_key ON idempotency_keys(idempotency_key);
CREATE INDEX idx_idempotency_created_at ON idempotency_keys(created_at);
CREATE INDEX idx_idempotency_expires_at ON idempotency_keys(expires_at);
```

## Usage

### Using the Idempotency Filter

The library automatically registers a servlet filter that extracts the idempotency key from the configured header:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Idempotency-Key: unique-request-key-123" \
  -H "Content-Type: application/json" \
  -d '{"customerId": "cust-123", "items": [...]}' \
```

### Programmatic Usage

You can also use the idempotency store programmatically:

```java
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IdempotencyStore idempotencyStore;
    private final IdempotencyRequestHasher requestHasher;

    public PaymentController(IdempotencyStore idempotencyStore,
                            IdempotencyRequestHasher requestHasher) {
        this.idempotencyStore = idempotencyStore;
        this.requestHasher = requestHasher;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        // Check for existing idempotency record
        Optional<IdempotencyRecord> existing = idempotencyStore.find(idempotencyKey);

        if (existing.isPresent()) {
            IdempotencyRecord record = existing.get();
            if (record.status() == Status.IN_PROGRESS) {
                return ResponseEntity.status(409).build();
            }
            // Return cached response
            return ResponseEntity.ok(deserializeResponse(record.responseHash()));
        }

        // Create new idempotency record
        String requestHash = requestHasher.hash(request);
        IdempotencyRecord newRecord = new IdempotencyRecord(
            idempotencyKey,
            Status.IN_PROGRESS,
            Instant.now(),
            Instant.now(),
            requestHash,
            null,
            null
        );
        idempotencyStore.saveNew(newRecord);

        try {
            // Process payment
            PaymentResponse response = paymentService.process(request);

            // Update record with response
            IdempotencyRecord completed = new IdempotencyRecord(
                idempotencyKey,
                Status.COMPLETED,
                newRecord.createdAt(),
                Instant.now(),
                requestHash,
                200,
                serializeResponse(response)
            );
            idempotencyStore.update(completed);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Mark as failed
            IdempotencyRecord failed = new IdempotencyRecord(
                idempotencyKey,
                Status.FAILED,
                newRecord.createdAt(),
                Instant.now(),
                requestHash,
                500,
                null
            );
            idempotencyStore.update(failed);
            throw e;
        }
    }
}
```

## Storage Backend Selection

### Redis Store

**Pros:**
- Fast in-memory operations
- Automatic expiration via TTL
- Distributed support
- Low latency

**Cons:**
- Requires Redis infrastructure
- Data loss if Redis fails (unless using persistence)

**Use when:**
- High throughput is required
- Low latency is critical
- Redis is already in use
- Temporary idempotency is acceptable

### Database Store

**Pros:**
- Persistent across restarts
- ACID guarantees
- No additional infrastructure
- Durable storage

**Cons:**
- Higher latency than Redis
- Database load increases
- Requires scheduled cleanup

**Use when:**
- Long-term idempotency is required
- Durability is critical
- Database is already in use
- Operations are not extremely high-frequency

### No-Op Store

**Pros:**
- No external dependencies
- Zero overhead

**Cons:**
- No actual idempotency
- Only for testing/disabled scenarios

**Use when:**
- Idempotency is disabled
- Testing scenarios
- Development environment

## Idempotency Behavior

### Request Lifecycle

1. **New Request**: Client provides unique idempotency key
2. **IN_PROGRESS**: Request is being processed
3. **COMPLETED**: Request completed successfully, response cached
4. **FAILED**: Request failed, allows retry

### Status Codes

- **200 OK**: Successful response (new or cached)
- **409 Conflict**: Request with same key already IN_PROGRESS or has different content

### TTL Behavior

- Records automatically expire after configured TTL
- Redis uses native TTL functionality
- Database uses scheduled cleanup job (runs hourly)
- Expired records are treated as non-existent

## Metrics

The library exposes the following Micrometer metrics:

| Metric Name | Type | Description |
|-------------|------|-------------|
| `idempotency.hit` | Counter | Number of cached responses returned |
| `idempotency.miss` | Counter | Number of new requests processed |
| `idempotency.expired` | Counter | Number of expired keys encountered |
| `idempotency.in_progress` | Counter | Number of requests rejected due to IN_PROGRESS status |
| `idempotency.conflict` | Counter | Number of requests rejected due to hash mismatch |
| `idempotency.response.duration` | Timer | Duration of idempotent request processing |

### Example Metrics Output (Prometheus format)

```
idempotency_hit_total{type="idempotency"} 1250.0
idempotency_miss_total{type="idempotency"} 342.0
idempotency_expired_total{type="idempotency"} 15.0
idempotency_in_progress_total{type="idempotency"} 8.0
idempotency_conflict_total{type="idempotency"} 3.0
idempotency_response_duration_seconds_count{type="idempotency"} 1592.0
idempotency_response_duration_seconds_sum{type="idempotency"} 423.5
```

## Best Practices

### 1. Use Unique Keys

Generate unique, non-repeating idempotency keys:

```java
String idempotencyKey = UUID.randomUUID().toString();
```

Or use a business identifier:

```java
String idempotencyKey = "order-" + orderId + "-" + timestamp;
```

### 2. Include Request Validation

Enable request hash validation to prevent misuse:

```yaml
gogidix:
  idempotency:
    enforce-request-hash: true
```

### 3. Set Appropriate TTL

Configure TTL based on your use case:

- Short-lived operations: 1 hour
- Payment processing: 24-48 hours
- Long-running workflows: 7+ days

```yaml
gogidix:
  idempotency:
    ttl: 48h
```

### 4. Monitor Metrics

Track idempotency metrics to detect issues:

- High `in_progress` count: May indicate stuck requests
- High `conflict` count: May indicate client issues
- High `expired` count: TTL may be too short

### 5. Handle Timeouts

Set appropriate timeouts for idempotent operations:

```yaml
spring:
  mvc:
    async:
      request-timeout: 30s
```

## Advanced Configuration

### Custom Idempotency Store

Implement your own idempotency store:

```java
@Component
public class CustomIdempotencyStore implements IdempotencyStore {

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        // Custom implementation
    }

    @Override
    public IdempotencyRecord saveNew(IdempotencyRecord record) {
        // Custom implementation
    }

    @Override
    public IdempotencyRecord update(IdempotencyRecord record) {
        // Custom implementation
    }
}
```

### Custom Request Hasher

Implement custom request hashing logic:

```java
@Component
public class CustomRequestHasher implements IdempotencyRequestHasher {

    @Override
    public String hash(Object request) {
        // Custom hashing logic
        return DigestUtils.sha256Hex(customSerialize(request));
    }
}
```

### Custom Idempotency Policy

Implement custom idempotency decision logic:

```java
@Component
public class CustomIdempotencyPolicy implements IdempotencyPolicy {

    @Override
    public IdempotencyDecision decide(IdempotencyRecord existing, String requestHash) {
        // Custom decision logic
        return IdempotencyDecision.acceptNew();
    }
}
```

## Testing

The library includes comprehensive tests:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RedisIdempotencyStoreTest

# Run with coverage
mvn test jacoco:report
```

## Troubleshooting

### Issue: Idempotency not working

**Solution**: Verify configuration:
```yaml
gogidix:
  idempotency:
    store: redis  # or database
```

### Issue: Records not expiring

**Solution**: For database, ensure cleanup is enabled:
```yaml
gogidix:
  idempotency:
    database:
      auto-cleanup: true
```

### Issue: High memory usage

**Solution**: Reduce TTL or switch to Redis:
```yaml
gogidix:
  idempotency:
    ttl: 1h
    store: redis
```

## License

This library is part of the Gogidix RapidAssist platform.

## Support

For issues, questions, or contributions, please contact the Gogidix development team.
