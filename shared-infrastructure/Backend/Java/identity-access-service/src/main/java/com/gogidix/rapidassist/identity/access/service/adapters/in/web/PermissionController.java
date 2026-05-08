package com.gogidix.rapidassist.identity.access.service.adapters.in.web;

import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetUserEffectivePermissionsQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final GetUserEffectivePermissionsQuery getUserEffectivePermissionsQuery;

    public PermissionController(GetUserEffectivePermissionsQuery getUserEffectivePermissionsQuery) {
        this.getUserEffectivePermissionsQuery = getUserEffectivePermissionsQuery;
    }

    @GetMapping
    public ResponseEntity<Set<String>> getEffectivePermissions(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
            @RequestParam(value = "tenantId", required = false) String tenantIdParam,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(value = "userId", required = false) String userIdParam
    ) {
        RequestContext ctx = RequestContextHolder.get()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        String providedTenantId = HeaderSupport.pick(tenantIdHeader, tenantIdParam);
        if (providedTenantId != null && ctx.tenantId() != null && !providedTenantId.equals(ctx.tenantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context mismatch");
        }

        String userId = HeaderSupport.pick(userIdHeader, userIdParam);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required userId");
        }

        return ResponseEntity.ok(getUserEffectivePermissionsQuery.getPermissions(ctx.tenantId(), userId));
    }
}
