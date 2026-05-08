package com.gogidix.rapidassist.access.control.service.domain.model;

/**
 * Domain Model: AccessDecision
 *
 * Represents the result of an access control check.
 * This is a DOMAIN entity with ZERO framework dependencies.
 */
public record AccessDecision(
        boolean allowed,
        String reason
) {
    /**
     * Create an allowed decision.
     */
    public static AccessDecision allowed(String reason) {
        return new AccessDecision(true, reason);
    }

    /**
     * Create a denied decision.
     */
    public static AccessDecision denied(String reason) {
        return new AccessDecision(false, reason);
    }

    /**
     * Check if access is allowed.
     */
    public boolean isAllowed() {
        return allowed;
    }
}
