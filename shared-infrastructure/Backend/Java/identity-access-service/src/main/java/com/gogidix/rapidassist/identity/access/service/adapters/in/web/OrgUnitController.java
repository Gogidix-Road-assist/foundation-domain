package com.gogidix.rapidassist.identity.access.service.adapters.in.web;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.CreateOrgUnitCommand;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetOrgUnitQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/org-units")
public class OrgUnitController {

    private final CreateOrgUnitCommand createOrgUnitCommand;
    private final GetOrgUnitQuery getOrgUnitQuery;

    public OrgUnitController(CreateOrgUnitCommand createOrgUnitCommand, GetOrgUnitQuery getOrgUnitQuery) {
        this.createOrgUnitCommand = createOrgUnitCommand;
        this.getOrgUnitQuery = getOrgUnitQuery;
    }

    @PostMapping
    public ResponseEntity<OrgUnit> create(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
            @RequestHeader(value = "X-Country", required = false) String countryHeader,
            @RequestParam(value = "tenantId", required = false) String tenantIdParam,
            @RequestParam(value = "country", required = false) String countryParam,
            @Valid @RequestBody OrgUnit orgUnit
    ) {
        RequestContext ctx = RequestContextHolder.get()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        String providedTenantId = HeaderSupport.pick(tenantIdHeader, HeaderSupport.pick(tenantIdParam, orgUnit.tenantId()));
        String providedCountry = HeaderSupport.pick(countryHeader, HeaderSupport.pick(countryParam, orgUnit.country()));

        if (providedTenantId != null && ctx.tenantId() != null && !providedTenantId.equals(ctx.tenantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context mismatch");
        }
        if (providedCountry != null && ctx.country() != null && !providedCountry.equals(ctx.country())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Country context mismatch");
        }

        OrgUnit normalized = new OrgUnit(orgUnit.id(), ctx.tenantId(), ctx.country(), orgUnit.type(), orgUnit.name(), orgUnit.parentId());
        return ResponseEntity.ok(createOrgUnitCommand.create(normalized));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgUnit> getById(@PathVariable String id) {
        return getOrgUnitQuery.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrgUnit>> getChildren(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdHeader,
            @RequestParam(value = "tenantId", required = false) String tenantIdParam,
            @RequestParam String parentId
    ) {
        RequestContext ctx = RequestContextHolder.get()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        String providedTenantId = HeaderSupport.pick(tenantIdHeader, tenantIdParam);
        if (providedTenantId != null && ctx.tenantId() != null && !providedTenantId.equals(ctx.tenantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context mismatch");
        }

        return ResponseEntity.ok(getOrgUnitQuery.getChildren(ctx.tenantId(), parentId));
    }
}
