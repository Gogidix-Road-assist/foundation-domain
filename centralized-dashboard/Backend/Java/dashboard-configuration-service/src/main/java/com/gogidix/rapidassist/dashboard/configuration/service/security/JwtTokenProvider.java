package com.gogidix.rapidassist.dashboard.configuration.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT token provider for generating and validating JWT tokens.
 *
 * <p>This class provides functionality for:
 * <ul>
 *   <li>Generating JWT access tokens</li>
 *   <li>Validating JWT tokens</li>
 *   <li>Extracting claims from tokens</li>
 *   <li>Getting user details from tokens</li>
 * </ul>
 *
 * <p>Note: For production use, the signing key should be stored securely in a
 * vault or secrets management system, not in application properties.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:your-very-secure-secret-key-for-jwt-token-generation-at-least-256-bits-long}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Generates a JWT token for the given username and authorities.
     *
     * @param username    the username
     * @param authorities list of authorities/roles
     * @return the generated JWT token
     */
    public String generateToken(String username, List<String> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
            .subject(username)
            .claim("authorities", authorities)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extracts the authorities from the JWT token.
     *
     * @param token the JWT token
     * @return list of authorities
     */
    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (List<String>) claims.get("authorities");
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * Checks if the token is expired.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
