package com.gogidix.rapidassist.access.control.service.application.command;

import com.gogidix.rapidassist.access.control.service.domain.event.PermissionRevokedEvent;
import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.port.in.RevokePermissionCommand;
import com.gogidix.rapidassist.access.control.service.domain.port.out.AuditEventPublisher;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Application Command Handler: RevokePermissionCommandHandler
 *
 * Handles permission revocation requests.
 *
 * This handler:
 * 1. Validates the permission exists
 * 2. Marks the permission as inactive
 * 3. Persists the change
 * 4. Publishes domain events
 */
@Component
@Transactional
public class RevokePermissionCommandHandler implements RevokePermissionCommand {

    private static final Logger log = LoggerFactory.getLogger(RevokePermissionCommandHandler.class);

    private final PermissionRepository permissionRepository;
    private final AuditEventPublisher auditEventPublisher;

    public RevokePermissionCommandHandler(PermissionRepository permissionRepository,
                                          AuditEventPublisher auditEventPublisher) {
        this.permissionRepository = permissionRepository;
        this.auditEventPublisher = auditEventPublisher;
    }

    @Override
    public boolean revoke(String tenantId, String permissionId, String revokedBy) {
        log.info("Revoking permission: tenantId={}, permissionId={}", tenantId, permissionId);

        // Get correlation ID from RequestContext
        String correlationId = RequestContextHolder.get()
                .map(c -> c.correlationId())
                .orElse(UUID.randomUUID().toString());

        // Find the permission
        Permission permission = permissionRepository.findById(permissionId, tenantId)
                .orElse(null);

        if (permission == null) {
            log.warn("Permission not found: id={}, tenantId={}", permissionId, tenantId);
            return false;
        }

        // Revoke the permission
        permission.revoke();
        permissionRepository.save(permission);

        // Publish domain event
        publishPermissionRevokedEvent(permission, revokedBy, correlationId);

        log.info("Permission revoked successfully: id={}", permissionId);

        return true;
    }

    @Override
    public int revokeAllForSubject(String tenantId, String subjectId, String revokedBy) {
        log.info("Revoking all permissions for subject: tenantId={}, subjectId={}", tenantId, subjectId);

        List<Permission> permissions = permissionRepository.findBySubjectId(subjectId, tenantId);
        int count = 0;

        String correlationId = RequestContextHolder.get()
                .map(c -> c.correlationId())
                .orElse(UUID.randomUUID().toString());

        for (Permission permission : permissions) {
            permission.revoke();
            permissionRepository.save(permission);
            publishPermissionRevokedEvent(permission, revokedBy, correlationId);
            count++;
        }

        log.info("Revoked {} permissions for subject: {}", count, subjectId);

        return count;
    }

    @Override
    public boolean revokeSpecific(String tenantId, String subjectId, String resource,
                                 String action, String revokedBy) {
        log.info("Revoking specific permission: tenantId={}, subjectId={}, resource={}, action={}",
                tenantId, subjectId, resource, action);

        List<Permission> permissions = permissionRepository.findBySubjectAndResourceAndAction(
                subjectId, resource, action, tenantId);

        String correlationId = RequestContextHolder.get()
                .map(c -> c.correlationId())
                .orElse(UUID.randomUUID().toString());

        for (Permission permission : permissions) {
            permission.revoke();
            permissionRepository.save(permission);
            publishPermissionRevokedEvent(permission, revokedBy, correlationId);
        }

        return !permissions.isEmpty();
    }

    private void publishPermissionRevokedEvent(Permission permission, String revokedBy,
                                              String correlationId) {
        try {
            PermissionRevokedEvent event = PermissionRevokedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .tenantId(permission.getTenantId())
                    .permissionId(permission.getId())
                    .subjectId(permission.getSubjectId())
                    .subjectType(permission.getSubjectType())
                    .resource(permission.getResource())
                    .action(permission.getAction())
                    .revokedBy(revokedBy)
                    .revokedAt(Instant.now())
                    .reason("Explicit revocation")
                    .correlationId(correlationId)
                    .build();

            auditEventPublisher.publish(event);
        } catch (Exception e) {
            log.warn("Failed to publish PermissionRevoked event", e);
        }
    }
}
