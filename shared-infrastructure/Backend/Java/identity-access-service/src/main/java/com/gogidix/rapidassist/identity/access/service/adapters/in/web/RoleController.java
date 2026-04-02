package com.gogidix.rapidassist.identity.access.service.adapters.in.web;

import com.gogidix.rapidassist.identity.access.service.domain.model.Role;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.CreateRoleCommand;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetRoleQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final CreateRoleCommand createRoleCommand;
    private final GetRoleQuery getRoleQuery;

    public RoleController(CreateRoleCommand createRoleCommand, GetRoleQuery getRoleQuery) {
        this.createRoleCommand = createRoleCommand;
        this.getRoleQuery = getRoleQuery;
    }

    @PostMapping
    public ResponseEntity<Role> create(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
            @RequestParam(value = "tenantId", required = false) String tenantIdParam,
            @Valid @RequestBody Role role
    ) {
        RequestContext ctx = RequestContextHolder.get()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        String providedTenantId = HeaderSupport.pick(tenantIdHeader, HeaderSupport.pick(tenantIdParam, role.tenantId()));
        if (providedTenantId != null && ctx.tenantId() != null && !providedTenantId.equals(ctx.tenantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context mismatch");
        }

        Role normalized = new Role(role.id(), ctx.tenantId(), role.name(), role.permissions());
        return ResponseEntity.ok(createRoleCommand.create(normalized));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable String id) {
        return getRoleQuery.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
