# Shared CORS Configuration Library

## Overview

This library provides centralized, properties-based CORS configuration for all Gogidix RapidAssist services, eliminating code duplication across 37+ infrastructure services.

## Problem Solved

Previously, each service had identical CORS configuration code copy-pasted into:
- `CorsConfig.java` (15 services)
- `WebCorsConfiguration.java` (22 services)

This created a massive code duplication violation with 37 copies of the exact same logic.

## Solution

This library provides:
1. **Auto-configuration**: Automatically activates when added to classpath
2. **Properties-based configuration**: Customize via `application.yml` or environment variables
3. **Centralized maintenance**: Change CORS policy in one place
4. **Environment variable support**: Override origins via `ALLOWED_ORIGINS` environment variable

## Usage

### Step 1: Add Dependency

Add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-cors-config</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Step 2: Remove Duplicate Configuration

**DELETE** these files from your service:
- `src/main/java/.../config/CorsConfig.java` OR
- `src/main/java/.../config/WebCorsConfiguration.java` OR
- `src/main/java/.../infrastructure/config/CorsConfig.java` OR
- `src/main/java/.../infrastructure/config/WebCorsConfiguration.java`

### Step 3: Configure (Optional)

Add to `application.yml`:

```yaml
gogidix:
  cors:
    enabled: true
    allowed-origins: "http://localhost:3000,http://localhost:8080,https://rapidassist.gogidix.com"
    allowed-methods:
      - GET
      - POST
      - PUT
      - DELETE
      - OPTIONS
      - PATCH
    allowed-headers:
      - "*"
    allow-credentials: true
    max-age: 3600
    path-patterns:
      - "/**"
```

### Step 4: Environment Override (Optional)

Override origins via environment variable:

```bash
export ALLOWED_ORIGINS="https://production.example.com,https://admin.example.com"
```

## Migration Instructions

### Before (Duplicate Code):

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        String allowedOrigins = System.getenv().getOrDefault("ALLOWED_ORIGINS", ...);
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

### After (Clean Solution):

**1. Add dependency to pom.xml** (see Step 1 above)
**2. Delete CorsConfig.java** - No configuration code needed!

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `gogidix.cors.enabled` | `true` | Enable/disable CORS configuration |
| `gogidix.cors.allowed-origins` | `http://localhost:3000,http://localhost:8080,https://rapidassist.gogidix.com` | Comma-separated allowed origins |
| `gogidix.cors.allowed-methods` | `GET,POST,PUT,DELETE,OPTIONS,PATCH` | Allowed HTTP methods |
| `gogidix.cors.allowed-headers` | `*` | Allowed headers (`*` = all headers) |
| `gogidix.cors.allow-credentials` | `true` | Allow cookies/authorization headers |
| `gogidix.cors.max-age` | `3600` | Preflight cache max age (seconds) |
| `gogidix.cors.path-patterns` | `/**` | Path patterns to apply CORS to |

## Services to Migrate (37 total)

### Category A: Using CorsConfig.java (15 services)
1. alerting-service
2. anti-fraud-rules-service
3. anti-fraud-signals-service
4. audit-correlation-service
5. billing-service
6. courier-adapter-service
7. currency-converter-service
8. onboarding-service
9. rate-limiting-service
10. session-token-service
11. template-messaging-service
12. tenant-org-service
13. user-profile-service
14. waf-policy-service
15. webhook-delivery-service

### Category B: Using WebCorsConfiguration.java (22 services)
1. access-control-service
2. api-keys-service
3. database-management-service
4. database-indexing-service
5. event-audit-service
6. geo-location-service
7. identity-access-service
8. identity-service
9. idempotency-service
10. logging-aggregation-service
11. maps-geocoding-adapter-service
12. mfa-service
13. metrics-telemetry-service
14. notification-service
15. payment-service
16. payments-adapter-service
17. policy-engine-service
18. pricing-service
19. reporting-read-model-service
20. (Plus 2 more)

### Category C: No CORS Configuration (5 services)
These services need CORS configuration added:
- api-gateway
- data-privacy-consent-service
- insurer-adapter-service
- integration-adapters-service
- request-routing-service
- service-health-monitor-service
- service-registry-discovery

## Benefits

1. **Zero Code Duplication**: Single source of truth for CORS configuration
2. **Easy Maintenance**: Update policy in one place
3. **Flexibility**: Per-service customization via properties
4. **Environment Support**: Override for different deployments
5. **Auto-activation**: No manual configuration needed
6. **Type Safety**: Configuration properties with validation

## Testing

The library includes comprehensive integration tests verifying:
- Properties binding
- Bean creation
- Default values
- Custom configuration
- Environment variable overrides

Run tests:
```bash
mvn clean test
```

## Version History

- **1.0.0** (2025-02-01): Initial release
  - Auto-configuration support
  - Properties-based configuration
  - Environment variable overrides
  - Comprehensive test coverage
