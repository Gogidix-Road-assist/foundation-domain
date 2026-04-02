package com.gogidix.rapidassist.access.control.service.application.command;

import com.gogidix.rapidassist.access.control.service.domain.model.AccessDecision;
import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.model.Subject;
import com.gogidix.rapidassist.access.control.service.domain.port.in.CheckAccessCommand;
import com.gogidix.rapidassist.access.control.service.domain.port.out.AuditEventPublisher;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import com.gogidix.rapidassist.access.control.service.domain.repository.SubjectRepository;
import com.gogidix.rapidassist.access.control.service.domain.policy.AccessControlPolicy;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application Command Handler: CheckAccessCommandHandler
 *
 * Handles access control check requests by evaluating permissions
 * against the access control policy.
 *
 * This handler:
 * 1. Retrieves the subject and their permissions
 * 2. Evaluates access using the policy engine
 * 3. Publishes audit events
 * 4. Returns the access decision
 */
@Component
@Transactional(readOnly = true)
public class CheckAccessCommandHandler implements CheckAccessCommand {

    private static final Logger log = LoggerFactory.getLogger(CheckAccessCommandHandler.class);

    private final PermissionRepository permissionRepository;
    private final SubjectRepository subjectRepository;
    private final AuditEventPublisher auditEventPublisher;
    private final AccessControlPolicy policy;

    public CheckAccessCommandHandler(PermissionRepository permissionRepository,
                                     SubjectRepository subjectRepository,
                                     AuditEventPublisher auditEventPublisher,
                                     AccessControlPolicy policy) {
        this.permissionRepository = permissionRepository;
        this.subjectRepository = subjectRepository;
        this.auditEventPublisher = auditEventPublisher;
        this.policy = policy;
    }

    @Override
    public AccessDecision check(String tenantId, String subjectId, String resource, String action) {
        return checkWithContext(tenantId, subjectId, resource, action, Map.of());
    }

    @Override
    public AccessDecision checkWithContext(String tenantId, String subjectId, String resource,
                                          String action, Map<String, Object> context) {
        log.debug("Checking access: tenantId={}, subjectId={}, resource={}, action={}",
                tenantId, subjectId, resource, action);

        // Get correlation ID from RequestContext
        String correlationId = RequestContextHolder.get()
                .map(ctx -> ctx.correlationId())
                .orElse(UUID.randomUUID().toString());

        try {
            // Fetch subject if exists
            Subject subject = subjectRepository.findBySubjectKey(subjectId, tenantId)
                    .orElse(null);

            // Fetch direct permissions for the subject
            List<Permission> permissions = permissionRepository.findValidBySubjectId(subjectId, tenantId);

            // If subject has roles, fetch role-based permissions too
            if (subject != null && !subject.getRoleIds().isEmpty()) {
                for (String roleId : subject.getRoleIds()) {
                    List<Permission> rolePermissions = permissionRepository.findByRole(roleId, tenantId);
                    permissions.addAll(rolePermissions);
                }
            }

            // Evaluate access using the policy
            AccessControlPolicy.AccessDecision decision = policy.evaluate(subjectId, resource, action, permissions);

            // Publish audit event for denied access
            if (!decision.isAllowed()) {
                publishAccessDeniedEvent(tenantId, subjectId, resource, action,
                        decision.getReason(), correlationId);
            }

            log.info("Access decision: {} for tenantId={}, subjectId={}, resource={}, action={}",
                    decision.isAllowed() ? "ALLOW" : "DENY", tenantId, subjectId, resource, action);

            // Convert policy decision to domain model
            return decision.isAllowed()
                    ? AccessDecision.allowed(decision.getReason())
                    : AccessDecision.denied(decision.getReason());

        } catch (Exception e) {
            log.error("Error checking access for tenantId={}, subjectId={}, resource={}, action={}",
                    tenantId, subjectId, resource, action, e);
            // Fail secure - deny on error
            return AccessDecision.denied("Error evaluating access: " + e.getMessage());
        }
    }

    private void publishAccessDeniedEvent(String tenantId, String subjectId,
                                         String resource, String action,
                                         String reason, String correlationId) {
        try {
            var event = com.gogidix.rapidassist.access.control.service.domain.event.AccessDeniedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .tenantId(tenantId)
                    .subjectId(subjectId)
                    .subjectType("USER")
                    .resource(resource)
                    .action(action)
                    .denialReason(reason)
                    .correlationId(correlationId)
                    .build();

            auditEventPublisher.publish(event);
        } catch (Exception e) {
            log.warn("Failed to publish AccessDenied event", e);
        }
    }
}
