package com.gogidix.rapidassist.access.control.service.application.command;

import com.gogidix.rapidassist.access.control.service.domain.event.PermissionGrantedEvent;
import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.port.in.GrantPermissionCommand;
import com.gogidix.rapidassist.access.control.service.domain.port.in.GrantPermissionRequest;
import com.gogidix.rapidassist.access.control.service.domain.port.out.AuditEventPublisher;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Application Command Handler: GrantPermissionCommandHandler
 *
 * Handles permission grant requests.
 *
 * This handler:
 * 1. Validates the request parameters
 * 2. Creates the permission entity
 * 3. Persists to the repository
 * 4. Publishes domain events
 */
@Component
@Transactional
public class GrantPermissionCommandHandler implements GrantPermissionCommand {

    private static final Logger log = LoggerFactory.getLogger(GrantPermissionCommandHandler.class);

    private final PermissionRepository permissionRepository;
    private final AuditEventPublisher auditEventPublisher;

    public GrantPermissionCommandHandler(PermissionRepository permissionRepository,
                                         AuditEventPublisher auditEventPublisher) {
        this.permissionRepository = permissionRepository;
        this.auditEventPublisher = auditEventPublisher;
    }

    @Override
    public Permission grant(String tenantId, String subjectId, String subjectType,
                           String resource, String action, String effect,
                           String grantedBy, Instant validUntil) {

        log.info("Granting permission: tenantId={}, subjectId={}, resource={}, action={}, effect={}",
                tenantId, subjectId, resource, action, effect);

        // Get correlation ID from RequestContext
        String correlationId = RequestContextHolder.get()
                .map(c -> c.correlationId())
                .orElse(UUID.randomUUID().toString());

        // Create the permission
        Permission permission = Permission.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .subjectId(subjectId)
                .subjectType(subjectType)
                .resource(resource)
                .action(action)
                .effect(effect)
                .grantedAt(Instant.now())
                .grantedBy(grantedBy)
                .validUntil(validUntil)
                .active(true)
                .build();

        // Save to repository
        Permission saved = permissionRepository.save(permission);

        // Publish domain event
        publishPermissionGrantedEvent(saved, correlationId);

        log.info("Permission granted successfully: id={}", saved.getId());

        return saved;
    }

    @Override
    public Permission grant(GrantPermissionRequest request) {
        return grant(
                request.getTenantId(),
                request.getSubjectId(),
                request.getSubjectType(),
                request.getResource(),
                request.getAction(),
                request.getEffect(),
                request.getGrantedBy(),
                request.getValidUntil()
        );
    }

    private void publishPermissionGrantedEvent(Permission permission, String correlationId) {
        try {
            PermissionGrantedEvent event = PermissionGrantedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .tenantId(permission.getTenantId())
                    .permissionId(permission.getId())
                    .subjectId(permission.getSubjectId())
                    .subjectType(permission.getSubjectType())
                    .resource(permission.getResource())
                    .action(permission.getAction())
                    .effect(permission.getEffect())
                    .grantedBy(permission.getGrantedBy())
                    .grantedAt(permission.getGrantedAt())
                    .correlationId(correlationId)
                    .build();

            auditEventPublisher.publish(event);
        } catch (Exception e) {
            log.warn("Failed to publish PermissionGranted event", e);
        }
    }
}
