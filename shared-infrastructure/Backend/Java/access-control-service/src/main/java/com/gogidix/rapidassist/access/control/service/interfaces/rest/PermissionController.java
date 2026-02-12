package com.gogidix.rapidassist.access.control.service.interfaces.rest;

import com.gogidix.rapidassist.access.control.service.application.command.GrantPermissionCommandHandler;
import com.gogidix.rapidassist.access.control.service.application.command.RevokePermissionCommandHandler;
import com.gogidix.rapidassist.access.control.service.application.dto.request.GrantPermissionRequestDto;
import com.gogidix.rapidassist.access.control.service.application.dto.response.PermissionResponseDto;
import com.gogidix.rapidassist.access.control.service.application.query.GetPermissionsQueryHandler;
import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller: PermissionController
 *
 * Handles permission management operations.
 */
@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permission Management", description = "APIs for managing permissions")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    private final GrantPermissionCommandHandler grantCommandHandler;
    private final RevokePermissionCommandHandler revokeCommandHandler;
    private final GetPermissionsQueryHandler queryHandler;

    public PermissionController(GrantPermissionCommandHandler grantCommandHandler,
                                RevokePermissionCommandHandler revokeCommandHandler,
                                GetPermissionsQueryHandler queryHandler) {
        this.grantCommandHandler = grantCommandHandler;
        this.revokeCommandHandler = revokeCommandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping("/grant")
    @Operation(summary = "Grant a permission", description = "Grants a permission to a subject")
    public ResponseEntity<PermissionResponseDto> grant(@Valid @RequestBody GrantPermissionRequestDto request) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        String grantedBy = RequestContextHolder.get()
                .map(ctx -> (String) ctx.userId())
                .orElse("system");

        Permission permission = grantCommandHandler.grant(
                tenantId,
                request.subjectId(),
                request.subjectType(),
                request.resource(),
                request.action(),
                request.effect(),
                grantedBy,
                request.validUntil()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(PermissionResponseDto.from(permission));
    }

    @DeleteMapping("/{permissionId}")
    @Operation(summary = "Revoke a permission", description = "Revokes a permission by ID")
    public ResponseEntity<Void> revoke(@PathVariable String permissionId) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        String revokedBy = RequestContextHolder.get()
                .map(ctx -> (String) ctx.userId())
                .orElse("system");

        revokeCommandHandler.revoke(tenantId, permissionId, revokedBy);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get permissions for a subject", description = "Returns all permissions for a given subject")
    public ResponseEntity<List<PermissionResponseDto>> getBySubject(@PathVariable String subjectId) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        List<Permission> permissions = queryHandler.getBySubject(tenantId, subjectId);

        return ResponseEntity.ok(permissions.stream()
                .map(PermissionResponseDto::from)
                .toList());
    }

    @GetMapping("/{permissionId}")
    @Operation(summary = "Get a permission by ID", description = "Returns a specific permission")
    public ResponseEntity<PermissionResponseDto> getById(@PathVariable String permissionId) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        return queryHandler.getById(tenantId, permissionId)
                .map(permission -> ResponseEntity.ok(PermissionResponseDto.from(permission)))
                .orElse(ResponseEntity.notFound().build());
    }

    public static class TenantContextException extends RuntimeException {
        public TenantContextException(String message) {
            super(message);
        }
    }
}
