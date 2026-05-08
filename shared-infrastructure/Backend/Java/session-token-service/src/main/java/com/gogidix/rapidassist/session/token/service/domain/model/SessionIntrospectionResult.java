package com.gogidix.rapidassist.session.token.service.domain.model;

import java.time.Instant;
import java.util.List;

public record SessionIntrospectionResult(
        boolean active,
        String tenantId,
        String subject,
        Instant expiresAt,
        String reason,
        String tokenType,
        Instant issuedAt,
        List<String> scopes,
        List<String> roles
) {
    // Constructor for backward compatibility
    public SessionIntrospectionResult(boolean active, String tenantId, String subject, Instant expiresAt, String reason) {
        this(active, tenantId, subject, expiresAt, reason, null, null, null, null);
    }

    // Builder-style methods
    public SessionIntrospectionResult withTokenType(String tokenType) {
        return new SessionIntrospectionResult(active, tenantId, subject, expiresAt, reason, tokenType, issuedAt, scopes, roles);
    }

    public SessionIntrospectionResult withIssuedAt(Instant issuedAt) {
        return new SessionIntrospectionResult(active, tenantId, subject, expiresAt, reason, tokenType, issuedAt, scopes, roles);
    }

    public SessionIntrospectionResult withExpiresAt(Instant expiresAt) {
        return new SessionIntrospectionResult(active, tenantId, subject, expiresAt, reason, tokenType, issuedAt, scopes, roles);
    }

    public SessionIntrospectionResult withScopes(List<String> scopes) {
        return new SessionIntrospectionResult(active, tenantId, subject, expiresAt, reason, tokenType, issuedAt, scopes, roles);
    }

    public SessionIntrospectionResult withRoles(List<String> roles) {
        return new SessionIntrospectionResult(active, tenantId, subject, expiresAt, reason, tokenType, issuedAt, scopes, roles);
    }
}
