package com.gogidix.rapidassist.ai.moderation.infrastructure.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * JWT Token Provider
 * Generates and validates JWT tokens
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret:rapid-assist-secret-key-change-in-production}")
    private String jwtSecret;

    @Value("${security.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        keyBytes = Base64.getEncoder().encode(keyBytes);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token
     */
    public String generateToken(String userId, String tenantId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .subject(userId)
            .claim("tenantId", tenantId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    /**
     * Get user ID from token
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    /**
     * Get tenant ID from token
     */
    public String getTenantIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.get("tenantId", String.class);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}
