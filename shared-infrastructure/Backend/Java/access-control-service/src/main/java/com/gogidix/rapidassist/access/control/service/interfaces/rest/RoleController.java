package com.gogidix.rapidassist.access.control.service.interfaces.rest;

import com.gogidix.rapidassist.access.control.service.application.command.RoleManagementCommandHandler;
import com.gogidix.rapidassist.access.control.service.application.dto.request.AssignPermissionRequestDto;
import com.gogidix.rapidassist.access.control.service.application.dto.request.AssignRoleRequestDto;
import com.gogidix.rapidassist.access.control.service.application.dto.request.CreateRoleRequestDto;
import com.gogidix.rapidassist.access.control.service.application.dto.request.UpdateRoleRequestDto;
import com.gogidix.rapidassist.access.control.service.application.dto.response.RoleResponseDto;
import com.gogidix.rapidassist.access.control.service.domain.aggregate.RoleAggregate;
import com.gogidix.rapidassist.access.control.service.domain.model.Role;
import com.gogidix.rapidassist.access.control.service.application.query.GetRolesQueryHandler;
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
 * REST Controller: RoleController
 *
 * Handles role management operations.
 */
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Role Management", description = "APIs for managing roles")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleManagementCommandHandler commandHandler;
    private final GetRolesQueryHandler queryHandler;

    public RoleController(RoleManagementCommandHandler commandHandler,
                          GetRolesQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    @Operation(summary = "Create a role", description = "Creates a new role")
    public ResponseEntity<RoleResponseDto> create(@Valid @RequestBody CreateRoleRequestDto request) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        String createdBy = RequestContextHolder.get()
                .map(ctx -> (String) ctx.userId())
                .orElse("system");

        Role role = commandHandler.createRole(tenantId, request.name(), request.description(), createdBy);

        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponseDto.from(role));
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update a role", description = "Updates an existing role")
    public ResponseEntity<RoleResponseDto> update(@PathVariable String roleId,
                                                  @Valid @RequestBody UpdateRoleRequestDto request) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        String updatedBy = RequestContextHolder.get()
                .map(ctx -> (String) ctx.userId())
                .orElse("system");

        Role role = commandHandler.updateRole(tenantId, roleId, request.name(), request.description(), updatedBy);

        return ResponseEntity.ok(RoleResponseDto.from(role));
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete a role", description = "Deletes a role")
    public ResponseEntity<Void> delete(@PathVariable String roleId) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        commandHandler.deleteRole(tenantId, roleId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "Assign permission to role", description = "Assigns a permission to a role")
    public ResponseEntity<RoleResponseDto> assignPermission(@PathVariable String roleId,
                                                             @Valid @RequestBody AssignPermissionRequestDto request) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        RoleAggregate aggregate = commandHandler.assignPermission(tenantId, roleId, request.permissionId());

        return ResponseEntity.ok(RoleResponseDto.from(aggregate));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @Operation(summary = "Remove permission from role", description = "Removes a permission from a role")
    public ResponseEntity<RoleResponseDto> removePermission(@PathVariable String roleId,
                                                             @PathVariable String permissionId) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        RoleAggregate aggregate = commandHandler.removePermission(tenantId, roleId, permissionId);

        return ResponseEntity.ok(RoleResponseDto.from(aggregate));
    }

    @PostMapping("/assign")
    @Operation(summary = "Assign role to subject", description = "Assigns a role to a subject")
    public ResponseEntity<Void> assignRole(@Valid @RequestBody AssignRoleRequestDto request) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        commandHandler.assignRoleToSubject(tenantId, request.subjectId(), request.roleId());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "List all roles", description = "Returns all roles for the tenant")
    public ResponseEntity<List<RoleResponseDto>> getAll() {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        List<Role> roles = queryHandler.getAllByTenant(tenantId);

        return ResponseEntity.ok(roles.stream()
                .map(RoleResponseDto::from)
                .toList());
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get a role by ID", description = "Returns a specific role")
    public ResponseEntity<RoleResponseDto> getById(@PathVariable String roleId) {
        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        return queryHandler.getById(tenantId, roleId)
                .map(role -> ResponseEntity.ok(RoleResponseDto.from(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    public static class TenantContextException extends RuntimeException {
        public TenantContextException(String message) {
            super(message);
        }
    }
}
