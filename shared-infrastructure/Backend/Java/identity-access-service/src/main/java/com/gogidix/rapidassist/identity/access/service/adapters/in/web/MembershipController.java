package com.gogidix.rapidassist.identity.access.service.adapters.in.web;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.AssignMembershipCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/memberships")
public class MembershipController {

    private final AssignMembershipCommand assignMembershipCommand;

    public MembershipController(AssignMembershipCommand assignMembershipCommand) {
        this.assignMembershipCommand = assignMembershipCommand;
    }

    @PostMapping
    public ResponseEntity<Membership> assign(@Valid @RequestBody Membership membership) {
        RequestContext ctx = RequestContextHolder.get()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (membership.tenantId() != null && ctx.tenantId() != null && !membership.tenantId().equals(ctx.tenantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context mismatch");
        }

        Membership normalized = new Membership(membership.id(), ctx.tenantId(), membership.orgUnitId(), membership.userId(), membership.roleIds());
        return ResponseEntity.ok(assignMembershipCommand.assign(normalized));
    }
}
