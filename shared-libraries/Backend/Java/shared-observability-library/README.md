# Shared Observability Library

Comprehensive observability utilities for the Gogidix Rapid Assist Platform.

## Overview

This library provides observability features used across all services:
- Metrics collection with Micrometer (Prometheus compatible)
- Distributed tracing with OpenTelemetry/Brave
- Automatic instrumentation with AOP aspects
- Log correlation with MDC
- Health checks and actuator integration

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Micrometer** for metrics
- **Prometheus** for metrics registry
- **OpenTelemetry/Brave** for distributed tracing
- **Spring AOP** for aspects
- **SLF4J/Logback** for logging

## Installation

Add as a dependency in your Maven project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-observability-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

Add to your `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5,0.95,0.99

# Logging configuration
logging:
  pattern:
    level: "%5p [traceId=%X{traceId},spanId=%X{spanId}] %logger{36} - %msg%n"
```

## Metrics Service

### MetricsService

Centralized metrics collection using Micrometer.

#### Record Counters

```java
@Autowired
private MetricsService metricsService;

// Increment counter by 1
metricsService.incrementCounter(
    MetricsService.MetricsNames.HTTP_REQUESTS,
    "endpoint", "/api/customers",
    "method", "GET",
    MetricsService.TagNames.STATUS, "200"
);

// Increment counter by custom amount
metricsService.incrementCounter(
    "payment.amount",
    100.50,
    "currency", "USD"
);
```

#### Record Timers

```java
// Using sample-based timing
Timer.Sample sample = metricsService.startTimer();
try {
    // Operation to time
    performOperation();
} finally {
    metricsService.stopTimer(sample,
        MetricsService.MetricsNames.DB_LATENCY,
        "query", "getCustomer",
        "tenant_id", "tenant-123"
    );
}

// Record duration directly
metricsService.recordTimer(
    MetricsService.MetricsNames.SERVICE_LATENCY,
    150L, // milliseconds
    "service", "payment-service",
    "operation", "process"
);

// Time a runnable
metricsService.recordRunnable(
    "batch.processing",
    () -> processBatch(),
    "batch_type", "payment"
);
```

#### Record Distribution Summaries

```java
// Record value distribution (e.g., request sizes, response times)
metricsService.recordDistributionSummary(
    "http.response.size",
    2048.0,
    "endpoint", "/api/customers"
);

metricsService.recordDistributionSummary(
    "payment.amount",
    5000L,
    "currency", "USD"
);
```

#### Register Gauges

```java
// Gauge for current value (e.g., queue size, active connections)
class ConnectionPool {
    public int getActiveConnections() {
        return activeConnections;
    }
}

ConnectionPool pool = new ConnectionPool();
metricsService.registerGauge(
    "db.connections.active",
    pool,
    ConnectionPool::getActiveConnections,
    "pool", "main"
);
```

### Common Metrics Names

Pre-defined metric names:

```java
// HTTP metrics
MetricsService.MetricsNames.HTTP_REQUESTS
MetricsService.MetricsNames.HTTP_RESPONSES
MetricsService.MetricsNames.HTTP_ERRORS
MetricsService.MetricsNames.HTTP_LATENCY

// Service metrics
MetricsService.MetricsNames.SERVICE_INVOCATIONS
MetricsService.MetricsNames.SERVICE_ERRORS
MetricsService.MetricsNames.SERVICE_LATENCY

// Database metrics
MetricsService.MetricsNames.DB_QUERIES
MetricsService.MetricsNames.DB_ERRORS
MetricsService.MetricsNames.DB_LATENCY

// Business metrics
MetricsService.MetricsNames.SERVICE_REQUESTS_CREATED
MetricsService.MetricsNames.SERVICE_REQUESTS_COMPLETED
MetricsService.MetricsNames.PAYMENTS_PROCESSED
MetricsService.MetricsNames.PAYMENTS_FAILED
```

### Common Tag Names

Pre-defined tag names:

```java
MetricsService.TagNames.SERVICE
MetricsService.TagNames.OPERATION
MetricsService.TagNames.STATUS
MetricsService.TagNames.ERROR
MetricsService.TagNames.TENANT_ID
MetricsService.TagNames.ORGANIZATION_ID
MetricsService.TagNames.USER_ID
MetricsService.TagNames.METHOD
MetricsService.TagNames.URI
MetricsService.TagNames.EXCEPTION
```

## Tracing Service

### TracingService

Distributed tracing with OpenTelemetry/Brave.

```java
@Autowired
private TracingService tracingService;

// Create a new span
Span span = tracingService.createSpan("operation-name");

// Add tags
tracingService.addTag(span, "tenant_id", "tenant-123");
tracingService.addTag(span, "user_id", "user-456");

// Record span
tracingService.recordSpan(span, Span.Status.OK);
```

## AOP Aspects

### @Monitored Annotation

Automatic metrics collection for methods.

```java
@Service
public class CustomerService {

    @Monitored(
        value = "customer.get",
        extraTags = {"service", "customer-service"}
    )
    public Customer getCustomer(String id) {
        // Method automatically timed and tracked
        // Metrics recorded:
        // - service.invocations (counter)
        // - service.latency (timer)
        return repository.findById(id);
    }
}
```

### @Logged Annotation

Automatic entry/exit logging.

```java
@Service
public class PaymentService {

    @Logged(
        logArgs = true,
        logResult = true,
        logErrors = true
    )
    public PaymentResult processPayment(PaymentRequest request) {
        // Method entry logged with arguments
        // Method exit logged with result
        // Errors logged with stack trace
        return paymentProcessor.process(request);
    }
}
```

### @Traced Annotation

Automatic distributed tracing.

```java
@Service
public class OrderService {

    @Traced(
        operationName = "order.create",
        tagTenant = true,
        tagUser = true
    )
    public Order createOrder(CreateOrderRequest request) {
        // Span automatically created
        // Tags: tenant_id, user_id
        // Child spans for downstream calls
        return orderRepository.save(request);
    }
}
```

### @Timed Annotation

Micrometer-based timing.

```java
@Service
public class DataProcessingService {

    @Timed(
        value = "data.processing",
        histogram = true,
        percentiles = {0.5, 0.95, 0.99}
    )
    public void processData(Data data) {
        // Method timing recorded to Micrometer
        // Histogram enabled for percentile calculation
    }
}
```

## Log Correlation

### LoggingCorrelation

Automatic trace ID propagation in logs.

```java
@Component
public class RequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        // Extract or generate trace ID
        String traceId = LoggingCorrelation.getOrGenerateTraceId(request);

        // Add to MDC
        LoggingCorrelation.putToMdc(traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            LoggingCorrelation.clearMdc();
        }
    }
}
```

## Multi-Tenancy Support

All observability features support multi-tenancy:

```java
// Metrics with tenant tagging
metricsService.incrementCounter(
    MetricsService.MetricsNames.SERVICE_REQUESTS_CREATED,
    MetricsService.TagNames.TENANT_ID, tenantContext.getTenantId(),
    "request_type", "towing"
);

// Tracing with tenant context
tracingService.addTag(span, "tenant_id", tenantContext.getTenantId());
```

## Actuator Endpoints

The library integrates with Spring Boot Actuator:

```bash
# Health check
GET /actuator/health

# Metrics in Prometheus format
GET /actuator/prometheus

# All metrics
GET /actuator/metrics

# Specific metric
GET /actuator/metrics/http.server.requests
```

## Testing

### Unit Tests

```java
@SpringBootTest
class ObservabilityLibraryTest {

    @Autowired
    private MetricsService metricsService;

    @Test
    void testCounterIncrement() {
        metricsService.incrementCounter("test.counter");
        // Verify counter incremented
    }
}
```

### Test Coverage

Run tests with coverage:

```bash
mvn test jacoco:report
```

## Metrics Dashboard

Import the provided Grafana dashboard (when created):

```bash
# Import to Grafana
grafana/dashboard/import --file grafana/rapid-assist-dashboard.json
```

Dashboard includes:
- HTTP request rate
- HTTP error rate
- Request latency (p50, p95, p99)
- JVM metrics
- Custom business metrics

## Best Practices

1. **Naming Conventions**: Use dot-separated names (e.g., `http.requests`)
2. **Tag Cardinality**: Keep tag values low-cardinality
3. **Percentiles**: Track p50, p95, p99 for latency
4. **Histograms**: Enable for distribution metrics
5. **Span Names**: Use operation names, not URLs
6. **Tag Propagation**: Include tenant_id and user_id in all metrics
7. **Error Tags**: Add error type to error metrics
8. **Metric Cleanup**: Remove unused metrics periodically

## Observability Keys

Pre-defined constants for consistent metric naming:

```java
import com.gogidix.rapidassist.shared.observability.library.domain.ObservabilityKeys;

// Use predefined keys
metricsService.incrementCounter(
    ObservabilityKeys.METRIC_REQUEST_COUNT,
    ObservabilityKeys.TAG_STATUS, "200"
);
```

## Integration Examples

### Service Layer with Observability

```java
@Service
public class CustomerService {

    @Autowired
    private MetricsService metricsService;

    @Monitored(value = "customer.create", extraTags = {"layer", "service"})
    @Traced(operationName = "customer.create")
    @Logged(logArgs = true, logResult = false)
    public Customer createCustomer(CustomerCreateRequest request) {
        try {
            Customer customer = repository.save(request);

            metricsService.incrementCounter(
                MetricsService.MetricsNames.SERVICE_REQUESTS_CREATED,
                MetricsService.TagNames.TENANT_ID, request.getTenantId(),
                "customer_type", request.getCustomerType()
            );

            return customer;
        } catch (Exception e) {
            metricsService.incrementCounter(
                MetricsService.MetricsNames.SERVICE_ERRORS,
                MetricsService.TagNames.EXCEPTION, e.getClass().getSimpleName()
            );
            throw e;
        }
    }
}
```

## Contributing

When contributing to this library:

1. Add comprehensive tests
2. Update documentation
3. Use consistent naming
4. Follow OpenTelemetry standards
5. Add JavaDoc for all public APIs

## License

Copyright (c) 2026 Gogidix. All rights reserved.
