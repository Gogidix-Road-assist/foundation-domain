# Shared Security Library

Comprehensive security utilities for the Gogidix Rapid Assist Platform.

## Overview

This library provides security-related functionality used across all services:
- JWT token generation and validation
- Password hashing and verification with BCrypt
- Multi-Factor Authentication (MFA) with TOTP
- Role-Based Access Control (RBAC)
- API key generation and validation
- Security context management
- CORS and CSRF configuration

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Security 6**
- **JJWT 0.12.6** for JWT handling
- **Google Authenticator 1.5.0** for MFA
- **BCrypt** for password hashing

## Installation

Add as a dependency in your Maven project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-security-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

Add to your `application.yml`:

```yaml
jwt:
  secret: your-256-bit-secret-key-change-in-production
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days

supabase:
  jwt:
    secret: your-supabase-jwt-secret
    audience: your-api-audience
```

## JWT Token Management

### JwtTokenUtil

The `JwtTokenUtil` class provides comprehensive JWT token operations.

#### Generate Access Token

```java
@Autowired
private JwtTokenUtil jwtTokenUtil;

String token = jwtTokenUtil.generateAccessToken(
    "username",    // subject/username
    "user-123",    // user ID
    "tenant-456"   // tenant ID
);
```

#### Generate Refresh Token

```java
String refreshToken = jwtTokenUtil.generateRefreshToken(
    "username",
    "user-123",
    "tenant-456"
);
```

#### Validate Token

```java
Boolean isValid = jwtTokenUtil.validateToken(token);
if (isValid) {
    // Token is valid
}
```

#### Extract Claims

```java
String username = jwtTokenUtil.extractUsername(token);
String userId = jwtTokenUtil.extractUserId(token);
String tenantId = jwtTokenUtil.extractTenantId(token);
Date expiration = jwtTokenUtil.extractExpiration(token);
```

#### Check Token Expiration

```java
Boolean isExpired = jwtTokenUtil.isTokenExpired(token);
Long timeRemaining = jwtTokenUtil.getTimeUntilExpiration(token);
```

#### Custom Claims

```java
Map<String, Object> extraClaims = Map.of(
    "role", "ADMIN",
    "permissions", List.of("read", "write")
);

String token = jwtTokenUtil.generateToken("username", extraClaims, 3600000L);
```

## Password Management

### PasswordUtil

BCrypt-based password hashing and verification.

```java
@Autowired
private PasswordUtil passwordUtil;

// Hash password
String hashedPassword = passwordUtil.hashPassword("plain-text-password");

// Verify password
Boolean matches = passwordUtil.checkPassword("plain-text-password", hashedPassword);

// Generate random password
String randomPassword = passwordUtil.generateRandomPassword(16);
```

## Multi-Factor Authentication

### MFAUtil

TOTP-based MFA using Google Authenticator compatible apps.

#### Generate MFA Secret

```java
@Autowired
private MFAUtil mfaUtil;

String secret = mfaUtil.generateSecret();
// Store this secret securely for the user
```

#### Generate QR Code URL

```java
String issuerName = "Gogidix RapidAssist";
String accountName = "user@example.com";

String qrUrl = mfaUtil.getQRCodeUrl(issuerName, accountName, secret);
// Use this URL to generate QR code for user to scan
```

#### Verify TOTP Code

```java
// Get 6-digit code from authenticator app
Integer code = 123456;

Boolean isValid = mfaUtil.verifyCode(secret, code);
if (isValid) {
    // MFA verification successful
}
```

#### Verify with Exception

```java
try {
    mfaUtil.verifyCodeOrThrow(secret, code, "user-123");
    // Verification successful
} catch (MFAVerificationException e) {
    // Handle invalid MFA code
}
```

#### Generate Backup Codes

```java
String[] backupCodes = mfaUtil.generateBackupCodes(10);
// Store these securely for account recovery
```

#### Generate Verification Codes

```java
String smsCode = mfaUtil.generateSmsCode(6);
String emailCode = mfaUtil.generateEmailCode(6);
String alphaCode = mfaUtil.generateAlphanumericCode(8);
```

## Role-Based Access Control (RBAC)

### PermissionChecker

Check user permissions for resource access.

```java
@Autowired
private PermissionChecker permissionChecker;

// Check if user has permission
Boolean canRead = permissionChecker.hasPermission("user-123", "customers:read");
Boolean canWrite = permissionChecker.hasPermission("user-123", "customers:write");

// Check if user has role
Boolean isAdmin = permissionChecker.hasRole("user-123", "ADMIN");

// Check multiple permissions
Boolean hasAll = permissionChecker.hasAllPermissions("user-123",
    List.of("customers:read", "customers:write"));

// Check any permission
Boolean hasAny = permissionChecker.hasAnyPermission("user-123",
    List.of("customers:read", "providers:read"));
```

## API Key Management

### ApiKeyUtil

Generate and validate API keys.

```java
@Autowired
private ApiKeyUtil apiKeyUtil;

// Generate API key
String apiKey = apiKeyUtil.generateApiKey();

// Validate API key format
Boolean isValidFormat = apiKeyUtil.isValidApiKeyFormat(apiKey);

// Extract prefix
String prefix = apiKeyUtil.extractApiKeyPrefix(apiKey);
```

## Security Context

### SecurityContext

Thread-local storage for security information.

```java
// Set security context
SecurityContext.setUserId("user-123");
SecurityContext.setUsername("testuser");
SecurityContext.setTenantId("tenant-456");
SecurityContext.setRoles(List.of("USER", "ADMIN"));

// Get from context
String userId = SecurityContext.getUserId();
String tenantId = SecurityContext.getTenantId();
List<String> roles = SecurityContext.getRoles();

// Clear context (typically in filter)
SecurityContext.clear();
```

## Exception Handling

The library provides custom exceptions:

- `AuthenticationException`: General authentication failures
- `AuthorizationException`: Authorization/permission failures
- `InvalidTokenException`: Invalid or expired tokens
- `MFAVerificationException`: MFA code verification failures
- `SecurityException`: General security exceptions

Usage:

```java
try {
    // Security operation
} catch (InvalidTokenException e) {
    // Handle invalid token
} catch (MFAVerificationException e) {
    // Handle MFA failure
}
```

## Auto-Configuration

The library auto-configures Spring Security:

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Auto-configured components:
- `JwtTokenUtil`: JWT token operations
- `PasswordUtil`: Password hashing
- `MFAUtil`: MFA operations
- `PermissionChecker`: RBAC checks
- `ApiKeyUtil`: API key operations

## CORS Configuration

The library provides CORS configuration:

```yaml
cors:
  allowed-origins: https://example.com,https://api.example.com
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
  max-age: 3600
```

## CSRF Configuration

CSRF protection can be configured:

```yaml
csrf:
  enabled: false # Disable for API-only applications
```

## Testing

### Unit Tests

```java
@SpringBootTest
class SecurityLibraryTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtTokenUtil.generateAccessToken("user", "user-123", "tenant-456");
        assertTrue(jwtTokenUtil.validateToken(token));
        assertEquals("user", jwtTokenUtil.extractUsername(token));
    }
}
```

### Test Coverage

Run tests with coverage:

```bash
mvn test jacoco:report
```

View coverage report:

```bash
open target/site/jacoco/index.html
```

## Security Best Practices

1. **Secret Keys**: Never use default secret keys in production
2. **Token Expiration**: Use appropriate expiration times (access: 15-60 min, refresh: 7-30 days)
3. **HTTPS**: Always use HTTPS in production
4. **Password Strength**: Enforce strong password policies
5. **MFA**: Require MFA for sensitive operations
6. **Rate Limiting**: Implement rate limiting on authentication endpoints
7. **Audit Logging**: Log all security events
8. **Secure Storage**: Store MFA secrets and backup codes securely
9. **Token Rotation**: Rotate tokens periodically
10. **Input Validation**: Validate all user input

## Multi-Tenancy

All security utilities support multi-tenancy:

```java
// Include tenant ID in JWT
String token = jwtTokenUtil.generateAccessToken("user", "user-123", "tenant-456");

// Check permissions with tenant context
permissionChecker.hasPermission("user-123", "tenant-456", "customers:read");
```

## Integration Examples

### Authentication Controller

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Validate credentials
        User user = userService.findByUsername(request.getUsername());

        if (!passwordUtil.checkPassword(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtTokenUtil.generateAccessToken(
            user.getUsername(),
            user.getId(),
            user.getTenantId()
        );

        String refreshToken = jwtTokenUtil.generateRefreshToken(
            user.getUsername(),
            user.getId(),
            user.getTenantId()
        );

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }
}
```

### MFA Setup Controller

```java
@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    @Autowired
    private MFAUtil mfaUtil;

    @PostMapping("/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa(@RequestBody MfaSetupRequest request) {
        // Generate secret
        String secret = mfaUtil.generateSecret();

        // Generate QR URL
        String qrUrl = mfaUtil.getQRCodeUrl(
            "Gogidix RapidAssist",
            request.getEmail(),
            secret
        );

        // Generate backup codes
        String[] backupCodes = mfaUtil.generateBackupCodes(10);

        // Save to user
        userService.updateMfaSecret(request.getUserId(), secret, backupCodes);

        return ResponseEntity.ok(new MfaSetupResponse(qrUrl, backupCodes));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyMfa(@RequestBody MfaVerifyRequest request) {
        User user = userService.findById(request.getUserId());

        mfaUtil.verifyCodeOrThrow(
            user.getMfaSecret(),
            request.getCode(),
            user.getId()
        );

        // Enable MFA for user
        userService.enableMfa(user.getId());

        return ResponseEntity.ok().build();
    }
}
```

## Contributing

When contributing to this library:

1. Follow security best practices
2. Add comprehensive tests
3. Update documentation
4. Use strong cryptographic algorithms
5. Handle edge cases
6. Add proper exception handling

## License

Copyright (c) 2026 Gogidix. All rights reserved.
