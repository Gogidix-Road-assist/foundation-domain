package com.gogidix.rapidassist.access.control.service.domain.port.in;

import com.gogidix.rapidassist.access.control.service.domain.model.AccessDecision;

/**
 * Input Port: CheckAccessCommand
 *
 * Command interface for checking access control.
 * This is a use case interface in the hexagonal architecture.
 *
 * Implementations must:
 * - Extract tenant context from RequestContext
 * - Evaluate all applicable permissions
 * - Return decision with reason
 */
public interface CheckAccessCommand {

    /**
     * Check if a subject is allowed to perform an action on a resource.
     *
     * @param tenantId The tenant context (from RequestContext)
     * @param subjectId The subject requesting access (user/service ID)
     * @param resource The resource being accessed (e.g., "/api/v1/users")
     * @param action The action being performed (e.g., "READ", "WRITE", "DELETE")
     * @return AccessDecision with allow/deny and reason
     */
    AccessDecision check(String tenantId, String subjectId, String resource, String action);

    /**
     * Check access with additional context for ABAC.
     *
     * @param tenantId The tenant context
     * @param subjectId The subject requesting access
     * @param resource The resource being accessed
     * @param action The action being performed
     * @param context Additional attributes for ABAC evaluation
     * @return AccessDecision with allow/deny and reason
     */
    AccessDecision checkWithContext(String tenantId, String subjectId, String resource,
                                    String action, java.util.Map<String, Object> context);
}
