package com.gogidix.shared.infrastructure.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

/**
 * JWT Token Provider
 *
 * Handles JWT token validation and claims extraction
 *
 * Usage: Copy this class to each service's security package
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            // Check expiration
            if (claims.getExpirationTime() != null &&
                claims.getExpirationTime().before(new java.util.Date())) {
                log.warn("Token expired");
                return false;
            }

            // Check issuer
            String expectedIssuer = jwtProperties.getIssuer();
            if (expectedIssuer != null &&
                !expectedIssuer.equals(claims.getIssuer())) {
                log.warn("Invalid token issuer: {}", claims.getIssuer());
                return false;
            }

            return true;

        } catch (ParseException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get a specific claim from token
     */
    public String getClaimFromToken(String token, String claimName) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            return claims.getStringClaim(claimName);
        } catch (ParseException e) {
            log.error("Failed to get claim {} from token: {}", claimName, e.getMessage());
            return null;
        }
    }

    /**
     * Get roles from token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            return (List<String>) claims.getListClaim("roles");
        } catch (ParseException e) {
            log.error("Failed to get roles from token: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get subject (user ID) from token
     */
    public String getSubjectFromToken(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            return claims.getSubject();
        } catch (ParseException e) {
            log.error("Failed to get subject from token: {}", e.getMessage());
            return null;
        }
    }
}
