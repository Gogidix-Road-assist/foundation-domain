package com.gogidix.rapidassist.session.token.service.infrastructure.provider.jwt;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;
import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;
import com.gogidix.rapidassist.session.token.service.infrastructure.provider.SessionTokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT-based session token provider with Redis-backed token blacklist
 * and refresh token support.
 */
public class JwtSessionTokenProvider implements SessionTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtSessionTokenProvider.class);

    private final SessionTokenProperties properties;
    private final RedisTemplate<String, String> redisTemplate;
    private final Key signingKey;
    private final Key refreshKey;
    private final JwtParser jwtParser;
    private final JwtParser refreshParser;

    public JwtSessionTokenProvider(SessionTokenProperties properties,
                                  RedisTemplate<String, String> redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;

        // Initialize signing keys
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        byte[] refreshKeyBytes = (properties.getSecret() + "-refresh").getBytes(StandardCharsets.UTF_8);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);

        // Initialize parsers
        this.jwtParser = Jwts.parser()
                .setSigningKey(this.signingKey)
                .build();

        this.refreshParser = Jwts.parser()
                .setSigningKey(this.refreshKey)
                .build();

        logger.info("JwtSessionTokenProvider initialized");
    }

    @Override
    public SessionToken issue(String tenantId, String subject, Duration ttl) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);

        String jti = UUID.randomUUID().toString();

        // Build JWT claims
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .setId(jti)
                .claim("tenantId", tenantId)
                .claim("type", "access_token");

        // Add custom claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", properties.getDefaultScopes());
        claims.put("auth_time", now.getEpochSecond());

        // Add roles if provided
        List<String> roles = properties.getDefaultRoles();
        if (!roles.isEmpty()) {
            claims.put("roles", roles);
        }

        builder.addClaims(claims);

        // Sign and serialize
        String token = builder.signWith(signingKey, properties.getAlgorithm()).compact();

        // Store token metadata in Redis for tracking
        storeTokenMetadata(tenantId, jti, subject, expiresAt);

        logger.debug("Session token issued for tenant: {}, subject: {}", tenantId, subject);

        return new SessionToken(tenantId, subject, token, now, expiresAt);
    }

    @Override
    public SessionIntrospectionResult introspect(String tenantId, String token) {
        try {
            // Check if token is blacklisted
            if (isTokenBlacklisted(token)) {
                return new SessionIntrospectionResult(false, tenantId, null, null, "Token is revoked");
            }

            // Parse JWT
            Jws<Claims> jws = jwtParser.parseClaimsJws(token);
            Claims claims = jws.getBody();

            // Verify tenant
            String tokenTenantId = claims.get("tenantId", String.class);
            if (!Objects.equals(tenantId, tokenTenantId)) {
                return new SessionIntrospectionResult(false, tenantId, null, null, "Invalid tenant");
            }

            // Check expiration
            Instant now = Instant.now();
            Instant expiresAt = claims.getExpiration().toInstant();

            if (now.isAfter(expiresAt)) {
                return new SessionIntrospectionResult(false, tenantId, null, null, "Token expired");
            }

            // Build result
            String subject = claims.getSubject();
            String tokenType = claims.get("type", String.class);
            Instant issuedAt = claims.getIssuedAt().toInstant();

            @SuppressWarnings("unchecked")
            List<String> scopes = (List<String>) claims.getOrDefault("scope",
                    Arrays.asList(properties.getDefaultScopes().split(" ")));

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.getOrDefault("roles",
                    properties.getDefaultRoles());

            return new SessionIntrospectionResult(true, tenantId, subject, null, null)
                    .withTokenType(tokenType)
                    .withIssuedAt(issuedAt)
                    .withExpiresAt(expiresAt)
                    .withScopes(scopes)
                    .withRoles(roles);

        } catch (ExpiredJwtException e) {
            logger.debug("Token expired: {}", e.getMessage());
            return new SessionIntrospectionResult(false, tenantId, null, null, "Token expired");
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return new SessionIntrospectionResult(false, tenantId, null, null, "Invalid token");
        } catch (Exception e) {
            logger.error("Error introspecting token", e);
            return new SessionIntrospectionResult(false, tenantId, null, null, "Token validation failed");
        }
    }

    @Override
    public void revoke(String tenantId, String token) {
        try {
            // Parse token to get JTI
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            String jti = claims.getId();

            // Add to blacklist with expiration
            Instant expiresAt = claims.getExpiration().toInstant();
            long ttl = Duration.between(Instant.now(), expiresAt).getSeconds();

            if (ttl > 0) {
                String blacklistKey = buildBlacklistKey(tenantId, jti);
                redisTemplate.opsForValue().set(blacklistKey, token, ttl, TimeUnit.SECONDS);

                logger.info("Token revoked for tenant: {}, jti: {}", tenantId, jti);
            }

            // Remove from active tokens
            String activeKey = buildActiveTokenKey(tenantId, claims.getSubject());
            redisTemplate.opsForSet().remove(activeKey, token);

        } catch (Exception e) {
            logger.error("Failed to revoke token", e);
        }
    }

    /**
     * Issue a refresh token
     */
    public SessionToken issueRefreshToken(String tenantId, String subject, Duration ttl) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .setSubject(subject)
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .setId(jti)
                .claim("tenantId", tenantId)
                .claim("type", "refresh_token")
                .claim("auth_time", now.getEpochSecond())
                .signWith(refreshKey, properties.getAlgorithm())
                .compact();

        // Store refresh token mapping
        String refreshKey = buildRefreshTokenKey(tenantId, jti);
        redisTemplate.opsForValue().set(refreshKey, token, ttl.getSeconds(), TimeUnit.SECONDS);

        return new SessionToken(tenantId, subject, token, now, expiresAt);
    }

    /**
     * Refresh an access token using a refresh token
     */
    public Optional<SessionToken> refreshToken(String tenantId, String refreshToken) {
        try {
            // Validate refresh token
            Jws<Claims> jws = refreshParser.parseClaimsJws(refreshToken);
            Claims claims = jws.getBody();

            // Verify it's a refresh token
            if (!"refresh_token".equals(claims.get("type"))) {
                logger.warn("Token is not a refresh token");
                return Optional.empty();
            }

            // Verify tenant
            if (!Objects.equals(tenantId, claims.get("tenantId", String.class))) {
                logger.warn("Invalid tenant in refresh token");
                return Optional.empty();
            }

            // Check if refresh token is still valid
            if (Instant.now().isAfter(claims.getExpiration().toInstant())) {
                logger.warn("Refresh token expired");
                return Optional.empty();
            }

            // Check if refresh token is revoked
            String jti = claims.getId();
            String refreshKey = buildRefreshTokenKey(tenantId, jti);
            if (!redisTemplate.hasKey(refreshKey)) {
                logger.warn("Refresh token not found or revoked");
                return Optional.empty();
            }

            // Issue new access token
            String subject = claims.getSubject();
            SessionToken newToken = issue(tenantId, subject, properties.getAccessTokenTtl());

            // Optionally revoke old refresh token (one-time use)
            revokeRefreshToken(tenantId, jti);

            logger.debug("Token refreshed for tenant: {}, subject: {}", tenantId, subject);

            return Optional.of(newToken);

        } catch (Exception e) {
            logger.error("Failed to refresh token", e);
            return Optional.empty();
        }
    }

    /**
     * Revoke all tokens for a user
     */
    public void revokeAllUserTokens(String tenantId, String subject) {
        try {
            // Get all active tokens for user
            String activeKey = buildActiveTokenKey(tenantId, subject);
            Set<String> tokens = redisTemplate.opsForSet().members(activeKey);

            if (tokens != null) {
                for (String token : tokens) {
                    revoke(tenantId, token);
                }
                redisTemplate.delete(activeKey);
            }

            logger.info("All tokens revoked for user: {}, tenant: {}", subject, tenantId);

        } catch (Exception e) {
            logger.error("Failed to revoke all tokens for user", e);
        }
    }

    /**
     * Get active tokens for a user
     */
    public Set<String> getActiveTokens(String tenantId, String subject) {
        String activeKey = buildActiveTokenKey(tenantId, subject);
        Set<String> tokens = redisTemplate.opsForSet().members(activeKey);
        return tokens != null ? tokens : Collections.emptySet();
    }

    private void storeTokenMetadata(String tenantId, String jti, String subject, Instant expiresAt) {
        // Store in active tokens set
        String activeKey = buildActiveTokenKey(tenantId, subject);
        redisTemplate.opsForSet().add(activeKey, UUID.randomUUID().toString());
        redisTemplate.expire(activeKey, Duration.between(Instant.now(), expiresAt));

        // Store token metadata
        String metadataKey = buildTokenMetadataKey(tenantId, jti);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("subject", subject);
        metadata.put("issuedAt", Instant.now().toString());
        metadata.put("expiresAt", expiresAt.toString());

        redisTemplate.opsForHash().putAll(metadataKey, metadata);
        redisTemplate.expire(metadataKey, Duration.between(Instant.now(), expiresAt));
    }

    private boolean isTokenBlacklisted(String token) {
        try {
            String jti = jwtParser.parseClaimsJws(token).getBody().getId();
            String tenantId = jwtParser.parseClaimsJws(token).getBody().get("tenantId", String.class);
            String blacklistKey = buildBlacklistKey(tenantId, jti);
            return redisTemplate.hasKey(blacklistKey);
        } catch (Exception e) {
            return false; // If we can't parse, let validation handle it
        }
    }

    private void revokeRefreshToken(String tenantId, String jti) {
        String refreshKey = buildRefreshTokenKey(tenantId, jti);
        redisTemplate.delete(refreshKey);
    }

    private String buildBlacklistKey(String tenantId, String jti) {
        return String.format("%s:blacklist:%s:%s", properties.getKeyPrefix(), tenantId, jti);
    }

    private String buildRefreshTokenKey(String tenantId, String jti) {
        return String.format("%s:refresh:%s:%s", properties.getKeyPrefix(), tenantId, jti);
    }

    private String buildActiveTokenKey(String tenantId, String subject) {
        return String.format("%s:active:%s:%s", properties.getKeyPrefix(), tenantId, subject);
    }

    private String buildTokenMetadataKey(String tenantId, String jti) {
        return String.format("%s:metadata:%s:%s", properties.getKeyPrefix(), tenantId, jti);
    }
}