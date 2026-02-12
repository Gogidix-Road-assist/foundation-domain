# Implementation Guide for Remaining Services

This guide provides the exact implementation for rate-limit-policy-service, release-rollout-config-service, and tenancy-configuration-service.

## Common Files (Copy and Adapt for Each Service)

### 1. SecurityConfig.java Template

**Location**: `infrastructure/security/SecurityConfig.java`

```java
package com.gogidix.rapidassist.[service-path].infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/**/health").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
            .cors(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 2. CorsConfig.java Template

**Location**: `infrastructure/security/CorsConfig.java`

```java
package com.gogidix.rapidassist.[service-path].infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080,http://localhost:4200}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String[] allowedMethods;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(Arrays.asList(allowedMethods));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

### 3. OpenApiConfig.java Template

**Location**: `infrastructure/config/OpenApiConfig.java`

```java
package com.gogidix.rapidassist.[service-path].infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("[SERVICE-NAME] API")
                .version("1.0.0")
                .description("[SERVICE-DESCRIPTION]")
                .contact(new Contact()
                    .name("Rapid Assist Team")
                    .email("support@gogidix.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://choosealicense.com/licenses/mit/")))
            .servers(List.of(
                new Server().url("http://localhost:" + serverPort).description("Development"),
                new Server().url("https://api.gogidix.com").description("Production")
            ));
    }
}
```

### 4. CorrelationIdFilter.java Template

**Location**: `infrastructure/filter/CorrelationIdFilter.java`

```java
package com.gogidix.rapidassist.[service-path].infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        response.setHeader("X-Correlation-ID", correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

### 5. Exception Classes Template

**Location**: `adapters/in/web/exception/`

```java
// NotFoundException.java
package com.gogidix.rapidassist.[service-path].adapters.in.web.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String resourceName, String resourceId) {
        super(String.format("%s with id '%s' not found", resourceName, resourceId));
    }
}

// BadRequestException.java
package com.gogidix.rapidassist.[service-path].adapters.in.web.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

// ConflictException.java
package com.gogidix.rapidassist.[service-path].adapters.in.web.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

// ErrorResponse.java
package com.gogidix.rapidassist.[service-path].adapters.in.web.exception;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public record ErrorResponse(
    String timestamp,
    int status,
    String error,
    String message,
    Map<String, String> validationErrors,
    String path,
    String correlationId
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneId.of("UTC"));

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> validationErrors;
        private String path;
        private String correlationId;

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public Builder status(int status) {
            this.status = status;
            return this;
        }
        public Builder error(String error) {
            this.error = error;
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder validationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(
                timestamp != null ? FORMATTER.format(timestamp) : FORMATTER.format(Instant.now()),
                status, error, message, validationErrors, path, correlationId
            );
        }
    }
}

// GlobalExceptionHandler.java
package com.gogidix.rapidassist.[service-path].adapters.in.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .correlationId(getCorrelationId())
            .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        logger.error("Bad request: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .correlationId(getCorrelationId())
            .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .message("You do not have permission to access this resource")
            .path(request.getDescription(false).replace("uri=", ""))
            .correlationId(getCorrelationId())
            .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred", ex);
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getDescription(false).replace("uri=", ""))
            .correlationId(getCorrelationId())
            .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getCorrelationId() {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = java.util.UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
```

---

## Service-Specific Adaptations

### rate-limit-policy-service

**Service Path**: `com.gogidix.rapidassist.rate.limit.policy.service`

**Controller Changes**:
```java
// Remove: @CrossOrigin(origins = "*")
// Add imports:
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/rate-limits")
@Tag(name = "Rate Limit Policy", description = "APIs for managing rate limiting policies")
@SecurityRequirement(name = "bearerAuth")
public class RateLimitController {
    // ... existing code ...

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<Map<String, Object>> health() {
        // ...
    }

    // Add @Operation to all other endpoints
    // Add @Schema annotations to all request records
}
```

**Service Considerations**:
- Uses reactive programming (Project Reactor)
- Tests should use `StepVerifier` instead of assertions
- @Transactional not applicable (reactive)

---

### release-rollout-config-service

**Service Path**: `com.gogidix.rapidassist.release.rollout.config.service`

**Add @Transactional**:
```java
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReleaseRolloutService {
    // ...
}
```

---

### tenancy-configuration-service

**Service Path**: `com.gogidix.rapidassist.tenancy.configuration.service`

**Add @Transactional**:
```java
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TenancyConfigurationService {
    // ...
}
```

---

## Test File Templates

### ServiceTest.java
```java
package com.gogidix.rapidassist.[service-path].application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[ServiceName] Service Tests")
class [ServiceName]ServiceTest {

    @Mock
    private [RepositoryType] repository;

    @InjectMocks
    private [ServiceName]Service service;

    @Test
    @DisplayName("Should [operation]")
    void should[Operation]() {
        // Given
        // When
        // Then
    }
}
```

### ControllerTest.java
```java
package com.gogidix.rapidassist.[service-path].adapters.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest([ControllerName].class)
class [ControllerName]Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private [ServiceName]Service service;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetResource() throws Exception {
        mockMvc.perform(get("/api/[resource]"))
            .andExpect(status().isOk());
    }
}
```

---

## Build Commands

For each service, run:
```bash
cd Backend/Java/[service-name]
mvn clean compile
mvn clean test
mvn jacoco:report
```

---

This guide provides all the templates needed to complete the remaining 3 services.
