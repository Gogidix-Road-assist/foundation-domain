package com.gogidix.rapidassist.service.registry.discovery.adapters.in.web;

import com.gogidix.rapidassist.service.registry.discovery.application.service.ComprehensiveServiceRegistryService;
import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/registry")
public class ServiceRegistryController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryController.class);

    @Autowired
    private ComprehensiveServiceRegistryService registryService;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<ServiceInstance>> register(
        @Valid @RequestBody RegisterServiceRequest request
    ) {
        logger.info("Registering service instance: {} for service: {}",
            request.instanceId(), request.serviceName());

        ComprehensiveServiceRegistryService.RegisterServiceCommand command =
            new ComprehensiveServiceRegistryService.RegisterServiceCommand(
                request.tenantId(),
                request.serviceName(),
                request.instanceId(),
                request.baseUrl(),
                request.host(),
                request.port(),
                request.secure(),
                request.healthCheckUrl(),
                request.statusUrl(),
                request.healthCheckConfig(),
                request.tags(),
                request.metadata(),
                request.version(),
                request.environment(),
                request.weight()
            );

        return registryService.registerService(command)
            .thenApply(instance -> ResponseEntity.status(HttpStatus.CREATED).body(instance));
    }

    @DeleteMapping("/{serviceName}/{instanceId}")
    public CompletableFuture<ResponseEntity<Void>> deregister(
        @PathVariable String serviceName,
        @PathVariable String instanceId,
        @RequestParam String tenantId
    ) {
        logger.info("Deregistering service instance: {} for service: {}",
            instanceId, serviceName);

        return registryService.deregisterService(tenantId, serviceName, instanceId)
            .thenApply(result -> result.isPresent()
                ? ResponseEntity.noContent().<Void>build()
                : ResponseEntity.notFound().<Void>build());
    }

    @PostMapping("/{serviceName}/{instanceId}/heartbeat")
    public CompletableFuture<ResponseEntity<ServiceInstance>> heartbeat(
        @PathVariable String serviceName,
        @PathVariable String instanceId,
        @RequestParam String tenantId
    ) {
        return registryService.sendHeartbeat(tenantId, serviceName, instanceId)
            .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{serviceName}/{instanceId}/status")
    public CompletableFuture<ResponseEntity<ServiceInstance>> updateStatus(
        @PathVariable String serviceName,
        @PathVariable String instanceId,
        @RequestParam String tenantId,
        @RequestParam ServiceInstance.ServiceStatus status
    ) {
        return registryService.updateServiceStatus(tenantId, serviceName, instanceId, status)
            .thenApply(instanceOpt -> instanceOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<List<ServiceInstance>>> bulkRegister(
        @Valid @RequestBody BulkRegisterRequest request
    ) {
        logger.info("Bulk registering {} service instances", request.services().size());

        List<ComprehensiveServiceRegistryService.RegisterServiceCommand> commands =
            request.services().stream()
                .map(req -> new ComprehensiveServiceRegistryService.RegisterServiceCommand(
                    request.tenantId(),
                    req.serviceName(),
                    req.instanceId(),
                    req.baseUrl(),
                    req.host(),
                    req.port(),
                    req.secure(),
                    req.healthCheckUrl(),
                    req.statusUrl(),
                    req.healthCheckConfig(),
                    req.tags(),
                    req.metadata(),
                    req.version(),
                    req.environment(),
                    req.weight()
                ))
                .toList();

        ComprehensiveServiceRegistryService.BulkRegisterServicesCommand command =
            new ComprehensiveServiceRegistryService.BulkRegisterServicesCommand(
                request.tenantId(), commands);

        return registryService.bulkRegisterServices(command)
            .thenApply(instances -> ResponseEntity.status(HttpStatus.CREATED).body(instances));
    }

    @GetMapping("/{serviceName}/{instanceId}")
    public CompletableFuture<ResponseEntity<ServiceInstance>> getInstance(
        @PathVariable String serviceName,
        @PathVariable String instanceId,
        @RequestParam String tenantId
    ) {
        return registryService.getServiceInstance(tenantId, serviceName, instanceId)
            .thenApply(instanceOpt -> instanceOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{serviceName}/instances")
    public CompletableFuture<ResponseEntity<List<ServiceInstance>>> getInstancesByService(
        @PathVariable String serviceName,
        @RequestParam String tenantId
    ) {
        return registryService.getAllServiceInstances(tenantId, serviceName)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<ServiceInstance>>> getAllServices(
        @RequestParam String tenantId
    ) {
        return registryService.getAllServices(tenantId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/names")
    public CompletableFuture<ResponseEntity<List<String>>> getServiceNames(
        @RequestParam String tenantId
    ) {
        return registryService.getAllServiceNames(tenantId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<ServiceInstance>>> search(
        @RequestParam String tenantId,
        @RequestParam String keyword
    ) {
        return registryService.searchServices(tenantId, keyword)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/by-tag")
    public CompletableFuture<ResponseEntity<List<ServiceInstance>>> getByTag(
        @RequestParam String tenantId,
        @RequestParam String tag
    ) {
        return registryService.getServicesByTag(tenantId, tag)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/by-tags")
    public CompletableFuture<ResponseEntity<List<ServiceInstance>>> getByTags(
        @RequestParam String tenantId,
        @RequestParam Set<String> tags
    ) {
        return registryService.getServicesByTags(tenantId, tags)
            .thenApply(ResponseEntity::ok);
    }

    // Request records
    record RegisterServiceRequest(
        String tenantId,
        String serviceName,
        String instanceId,
        String baseUrl,
        String host,
        Integer port,
        Boolean secure,
        String healthCheckUrl,
        String statusUrl,
        ServiceInstance.HealthCheckConfig healthCheckConfig,
        Set<String> tags,
        Map<String, String> metadata,
        String version,
        String environment,
        Integer weight
    ) {}

    record BulkRegisterRequest(
        String tenantId,
        List<RegisterServiceRequest> services
    ) {}
}
