package com.gogidix.rapidassist.billing.service.adapters.in.web;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.port.in.GetBillingAccountQuery;
import com.gogidix.rapidassist.billing.service.domain.port.in.SetBillingPlanCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/billing")
@Tag(name = "Billing", description = "Billing account and plan management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BillingController {

    private final GetBillingAccountQuery getBillingAccountQuery;
    private final SetBillingPlanCommand setBillingPlanCommand;

    public BillingController(GetBillingAccountQuery getBillingAccountQuery, SetBillingPlanCommand setBillingPlanCommand) {
        this.getBillingAccountQuery = getBillingAccountQuery;
        this.setBillingPlanCommand = setBillingPlanCommand;
    }

    @GetMapping("/account")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get billing account", description = "Retrieves the billing account information for the authenticated tenant")
    public BillingAccountResponse getAccount() {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        BillingAccount account = getBillingAccountQuery.get(tenantId);
        return new BillingAccountResponse(account.plan(), account.updatedAt());
    }

    @PostMapping("/account/plan")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Set billing plan", description = "Updates the billing plan for the authenticated tenant account")
    public BillingAccountResponse setPlan(@Valid @RequestBody SetPlanRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        BillingAccount account = setBillingPlanCommand.setPlan(tenantId, request.plan());
        return new BillingAccountResponse(account.plan(), account.updatedAt());
    }
}
