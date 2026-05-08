package com.gogidix.rapidassist.tenant.org.service.adapters.in.web;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.tenant.org.service.application.service.ComprehensiveTenantOrgService;
import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/organizations")
public class TenantOrgController {

    private static final Logger logger = LoggerFactory.getLogger(TenantOrgController.class);

    @Autowired
    private ComprehensiveTenantOrgService tenantOrgService;

    private String getTenantId() {
        return RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context"));
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<TenantOrganization>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {
        String tenantId = getTenantId();
        logger.info("Creating organization: {} for tenant: {}", request.orgId(), tenantId);

        ComprehensiveTenantOrgService.CreateOrganizationCommand command =
                new ComprehensiveTenantOrgService.CreateOrganizationCommand(
                        tenantId,
                        request.orgId(),
                        request.orgName(),
                        request.orgType(),
                        request.plan(),
                        request.settings(),
                        request.metadata(),
                        request.billingInfo(),
                        request.contacts(),
                        request.parentOrgId(),
                        request.tags(),
                        request.customAttributes(),
                        request.complianceInfo(),
                        request.createdBy()
                );

        return tenantOrgService.createOrganization(command)
                .thenApply(org -> ResponseEntity.status(HttpStatus.CREATED).body(org));
    }

    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> bulkCreateOrganizations(
            @Valid @RequestBody BulkCreateOrganizationsRequest request) {
        String tenantId = getTenantId();
        logger.info("Bulk creating {} organizations for tenant: {}", request.organizations().size(), tenantId);

        List<ComprehensiveTenantOrgService.CreateOrganizationCommand> commands =
                request.organizations().stream()
                        .map(req -> new ComprehensiveTenantOrgService.CreateOrganizationCommand(
                                tenantId,
                                req.orgId(),
                                req.orgName(),
                                req.orgType(),
                                req.plan(),
                                req.settings(),
                                req.metadata(),
                                req.billingInfo(),
                                req.contacts(),
                                req.parentOrgId(),
                                req.tags(),
                                req.customAttributes(),
                                req.complianceInfo(),
                                req.createdBy()
                        ))
                        .toList();

        ComprehensiveTenantOrgService.BulkCreateOrganizationsCommand command =
                new ComprehensiveTenantOrgService.BulkCreateOrganizationsCommand(tenantId, commands);

        return tenantOrgService.bulkCreateOrganizations(command)
                .thenApply(orgs -> ResponseEntity.status(HttpStatus.CREATED).body(orgs));
    }

    @GetMapping("/{orgId}")
    public CompletableFuture<ResponseEntity<TenantOrganization>> getOrganization(@PathVariable String orgId) {
        String tenantId = getTenantId();
        return tenantOrgService.getOrganization(tenantId, orgId)
                .thenApply(orgOpt -> orgOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> listAllOrganizations() {
        String tenantId = getTenantId();
        return tenantOrgService.listAllOrganizations(tenantId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/by-status/{status}")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> listByStatus(@PathVariable String status) {
        String tenantId = getTenantId();
        TenantOrganization.OrganizationStatus orgStatus =
                TenantOrganization.OrganizationStatus.valueOf(status.toUpperCase());
        return tenantOrgService.listByStatus(tenantId, orgStatus)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/by-type/{type}")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> listByType(@PathVariable String type) {
        String tenantId = getTenantId();
        TenantOrganization.OrganizationType orgType =
                TenantOrganization.OrganizationType.valueOf(type.toUpperCase());
        return tenantOrgService.listByType(tenantId, orgType)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/by-plan/{planType}")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> listByPlan(@PathVariable String planType) {
        String tenantId = getTenantId();
        TenantOrganization.SubscriptionPlan.PlanType plan =
                TenantOrganization.SubscriptionPlan.PlanType.valueOf(planType.toUpperCase());
        return tenantOrgService.listByPlan(tenantId, plan)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> searchByName(@RequestParam String keyword) {
        String tenantId = getTenantId();
        return tenantOrgService.searchByName(tenantId, keyword)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/by-tags")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> searchByTags(@RequestParam Set<String> tags) {
        String tenantId = getTenantId();
        return tenantOrgService.searchByTags(tenantId, tags)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/active")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> listActiveOrganizations() {
        String tenantId = getTenantId();
        return tenantOrgService.listActiveOrganizations(tenantId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/children/{parentOrgId}")
    public CompletableFuture<ResponseEntity<List<TenantOrganization>>> listChildOrganizations(
            @PathVariable String parentOrgId) {
        String tenantId = getTenantId();
        return tenantOrgService.listChildOrganizations(tenantId, parentOrgId)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{orgId}")
    public CompletableFuture<ResponseEntity<TenantOrganization>> updateOrganization(
            @PathVariable String orgId,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        String tenantId = getTenantId();
        logger.info("Updating organization: {} for tenant: {}", orgId, tenantId);

        ComprehensiveTenantOrgService.UpdateOrganizationCommand command =
                new ComprehensiveTenantOrgService.UpdateOrganizationCommand(
                        request.orgName(),
                        request.orgType(),
                        request.plan(),
                        request.settings(),
                        request.metadata(),
                        request.billingInfo(),
                        request.contacts(),
                        request.tags(),
                        request.customAttributes(),
                        request.complianceInfo(),
                        request.updatedBy()
                );

        return tenantOrgService.updateOrganization(tenantId, orgId, command)
                .thenApply(orgOpt -> orgOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{orgId}")
    public CompletableFuture<ResponseEntity<Void>> deleteOrganization(@PathVariable String orgId) {
        String tenantId = getTenantId();
        logger.info("Deleting organization: {} for tenant: {}", orgId, tenantId);

        return tenantOrgService.deleteOrganization(tenantId, orgId)
                .thenApply(result -> result.isPresent()
                        ? ResponseEntity.noContent().<Void>build()
                        : ResponseEntity.notFound().<Void>build());
    }

    @PatchMapping("/{orgId}/status")
    public CompletableFuture<ResponseEntity<TenantOrganization>> updateStatus(
            @PathVariable String orgId,
            @RequestParam TenantOrganization.OrganizationStatus status) {
        String tenantId = getTenantId();
        return tenantOrgService.updateOrganizationStatus(tenantId, orgId, status)
                .thenApply(orgOpt -> orgOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{orgId}/plan")
    public CompletableFuture<ResponseEntity<TenantOrganization>> updatePlan(
            @PathVariable String orgId,
            @RequestBody TenantOrganization.SubscriptionPlan plan) {
        String tenantId = getTenantId();
        return tenantOrgService.updateOrganizationPlan(tenantId, orgId, plan)
                .thenApply(orgOpt -> orgOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{orgId}/settings")
    public CompletableFuture<ResponseEntity<TenantOrganization>> updateSettings(
            @PathVariable String orgId,
            @RequestBody TenantOrganization.OrganizationSettings settings) {
        String tenantId = getTenantId();
        return tenantOrgService.updateOrganizationSettings(tenantId, orgId, settings)
                .thenApply(orgOpt -> orgOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{orgId}/deactivate")
    public CompletableFuture<ResponseEntity<Void>> deactivateOrganization(@PathVariable String orgId) {
        String tenantId = getTenantId();
        return tenantOrgService.deactivateOrganization(tenantId, orgId)
                .thenApply(success -> success
                        ? ResponseEntity.ok().<Void>build()
                        : ResponseEntity.notFound().<Void>build());
    }

    @PostMapping("/{orgId}/reactivate")
    public CompletableFuture<ResponseEntity<Void>> reactivateOrganization(@PathVariable String orgId) {
        String tenantId = getTenantId();
        return tenantOrgService.reactivateOrganization(tenantId, orgId)
                .thenApply(success -> success
                        ? ResponseEntity.ok().<Void>build()
                        : ResponseEntity.notFound().<Void>build());
    }

    // Request records
    record CreateOrganizationRequest(
            String orgId,
            String orgName,
            TenantOrganization.OrganizationType orgType,
            TenantOrganization.SubscriptionPlan plan,
            TenantOrganization.OrganizationSettings settings,
            Map<String, String> metadata,
            TenantOrganization.BillingInfo billingInfo,
            List<TenantOrganization.ContactInfo> contacts,
            String parentOrgId,
            Set<String> tags,
            Map<String, String> customAttributes,
            TenantOrganization.ComplianceInfo complianceInfo,
            String createdBy
    ) {}

    record UpdateOrganizationRequest(
            String orgName,
            TenantOrganization.OrganizationType orgType,
            TenantOrganization.SubscriptionPlan plan,
            TenantOrganization.OrganizationSettings settings,
            Map<String, String> metadata,
            TenantOrganization.BillingInfo billingInfo,
            List<TenantOrganization.ContactInfo> contacts,
            Set<String> tags,
            Map<String, String> customAttributes,
            TenantOrganization.ComplianceInfo complianceInfo,
            String updatedBy
    ) {}

    record BulkCreateOrganizationsRequest(
            List<CreateOrganizationRequest> organizations
    ) {}
}
