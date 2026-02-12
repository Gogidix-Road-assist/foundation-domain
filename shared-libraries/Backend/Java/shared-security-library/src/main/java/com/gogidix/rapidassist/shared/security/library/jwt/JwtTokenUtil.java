package com.gogidix.rapidassist.shared.security.library.jwt;

import com.gogidix.rapidassist.shared.security.library.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token generation and validation.
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret:your-256-bit-secret-key-for-jwt-token-generation-and-validation-change-this-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extract tenant ID from token
     */
    public String extractTenantId(String token) {
        return extractClaim(token, claims -> claims.get("tenantId", String.class));
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("JWT", "Token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("JWT", "Unsupported token", e);
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("JWT", "Invalid token format", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("JWT", "Token is empty or null", e);
        }
    }

    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token) {
        try {
            String username = extractUsername(token);
            return username != null && !isTokenExpired(token);
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    /**
     * Generate access token
     */
    public String generateAccessToken(String username, String userId, String tenantId) {
        return generateToken(username, userId, tenantId, expiration);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username, String userId, String tenantId) {
        return generateToken(username, userId, tenantId, refreshExpiration);
    }

    /**
     * Generate token with custom expiration
     */
    public String generateToken(String username, String userId, String tenantId, Long expiration) {
        Map<String, Object> claims = Map.of(
                "userId", userId,
                "tenantId", tenantId != null ? tenantId : "default",
                "type", "access"
        );

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate token with custom claims
     */
    public String generateToken(String username, Map<String, Object> extraClaims, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get remaining time until token expires (in milliseconds)
     */
    public Long getTimeUntilExpiration(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * Get secret key for external use
     */
    public SecretKey getSecretKey() {
        return getSigningKey();
    }
}
