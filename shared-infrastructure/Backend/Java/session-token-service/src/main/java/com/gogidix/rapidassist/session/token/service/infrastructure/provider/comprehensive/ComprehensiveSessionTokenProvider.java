package com.gogidix.rapidassist.session.token.service.infrastructure.provider.comprehensive;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;
import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;
import com.gogidix.rapidassist.session.token.service.infrastructure.provider.SessionTokenProperties;
import com.gogidix.rapidassist.session.token.service.infrastructure.provider.jwt.JwtSessionTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * Comprehensive session token provider that delegates to JWT implementation
 * with additional convenience methods for token management.
 */
public class ComprehensiveSessionTokenProvider implements SessionTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveSessionTokenProvider.class);

    private final JwtSessionTokenProvider jwtProvider;

    public ComprehensiveSessionTokenProvider(RedisTemplate<String, String> redisTemplate,
                                             SessionTokenProperties properties) {
        this.jwtProvider = new JwtSessionTokenProvider(properties, redisTemplate);
        logger.info("ComprehensiveSessionTokenProvider initialized");
    }

    @Override
    public SessionToken issue(String tenantId, String subject, Duration ttl) {
        return jwtProvider.issue(tenantId, subject, ttl);
    }

    @Override
    public SessionIntrospectionResult introspect(String tenantId, String token) {
        return jwtProvider.introspect(tenantId, token);
    }

    @Override
    public void revoke(String tenantId, String token) {
        jwtProvider.revoke(tenantId, token);
    }

    /**
     * Issue a refresh token
     */
    public SessionToken issueRefreshToken(String tenantId, String subject, Duration ttl) {
        return jwtProvider.issueRefreshToken(tenantId, subject, ttl);
    }

    /**
     * Refresh an access token using a refresh token
     */
    public Optional<SessionToken> refreshToken(String tenantId, String refreshToken) {
        return jwtProvider.refreshToken(tenantId, refreshToken);
    }

    /**
     * Revoke all tokens for a user
     */
    public void revokeAllUserTokens(String tenantId, String subject) {
        jwtProvider.revokeAllUserTokens(tenantId, subject);
    }

    /**
     * Get active tokens for a user
     */
    public Set<String> getActiveTokens(String tenantId, String subject) {
        return jwtProvider.getActiveTokens(tenantId, subject);
    }
}