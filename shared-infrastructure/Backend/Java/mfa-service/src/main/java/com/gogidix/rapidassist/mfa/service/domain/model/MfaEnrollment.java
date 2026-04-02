package com.gogidix.rapidassist.mfa.service.domain.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record MfaEnrollment(
        String tenantId,
        String subject,
        String method,
        String enrollmentId,
        Instant enrolledAt,
        String secret,
        Map<String, Object> metadata
) {
    // Constructor for backward compatibility
    public MfaEnrollment(String tenantId, String subject, String method, String enrollmentId, Instant enrolledAt) {
        this(tenantId, subject, method, enrollmentId, enrolledAt, null, new HashMap<>());
    }

    // Builder-style methods
    public MfaEnrollment withSecret(String secret) {
        return new MfaEnrollment(tenantId, subject, method, enrollmentId, enrolledAt, secret, metadata);
    }

    public MfaEnrollment withMetadata(String key, Object value) {
        Map<String, Object> newMetadata = new HashMap<>(metadata);
        newMetadata.put(key, value);
        return new MfaEnrollment(tenantId, subject, method, enrollmentId, enrolledAt, secret, newMetadata);
    }
}
