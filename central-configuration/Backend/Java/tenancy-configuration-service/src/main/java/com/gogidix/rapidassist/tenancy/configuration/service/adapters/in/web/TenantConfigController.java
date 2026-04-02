package com.gogidix.rapidassist.tenancy.configuration.service.adapters.in.web;

import com.gogidix.rapidassist.tenancy.configuration.service.application.TenantConfigService;
import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantConfigController {

    private final TenantConfigService tenantService;

    public TenantConfigController(TenantConfigService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "tenancy-configuration-service"));
    }

    @GetMapping
    public ResponseEntity<List<TenantConfig>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants().join());
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantConfig> getTenant(@PathVariable String tenantId) {
        return tenantService.getTenant(tenantId).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TenantConfig> createTenant(@RequestBody CreateTenantRequest request) {
        TenantConfig tenant = tenantService.createTenant(request.tenantId(), request.name(), request.domain(), request.createdBy()).join();
        return ResponseEntity.created(URI.create("/api/tenants/" + tenant.tenantId())).body(tenant);
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<TenantConfig> updateTenant(@PathVariable String tenantId, @RequestBody UpdateTenantRequest request) {
        return tenantService.updateTenant(tenantId, request.name(), request.domain(), request.updatedBy()).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{tenantId}/activate")
    public ResponseEntity<TenantConfig> activateTenant(@PathVariable String tenantId, @RequestBody Map<String, String> request) {
        return tenantService.activateTenant(tenantId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{tenantId}/deactivate")
    public ResponseEntity<TenantConfig> deactivateTenant(@PathVariable String tenantId, @RequestBody Map<String, String> request) {
        return tenantService.deactivateTenant(tenantId, request.get("updatedBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String tenantId) {
        tenantService.deleteTenant(tenantId).join();
        return ResponseEntity.noContent().build();
    }

    record CreateTenantRequest(String tenantId, String name, String domain, String createdBy) {}
    record UpdateTenantRequest(String name, String domain, String updatedBy) {}
}
