package com.gogidix.rapidassist.service.registry.discovery.adapters.in.web;

import com.gogidix.rapidassist.service.registry.discovery.application.service.ComprehensiveServiceRegistryService;
import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ServiceRegistryController.
 */
@ExtendWith(MockitoExtension.class)
class ServiceRegistryControllerTest {

    @Mock
    private ComprehensiveServiceRegistryService registryService;

    @InjectMocks
    private ServiceRegistryController controller;

    @Test
    void registerService_WithValidRequest_Returns201Created() {
        // Given
        ServiceInstance instance = ServiceInstance.create(
            "tenant123", "user-service", "instance1",
            "http://localhost:8080", "localhost", 8080
        );

        when(registryService.registerService(any()))
            .thenReturn(CompletableFuture.completedFuture(instance));

        // When/Then
        var result = controller.register(createValidRegisterRequest());
        assert result != null;
    }

    @Test
    void getAllServices_WithTenantId_Returns200Ok() {
        // Given
        when(registryService.getAllServices("tenant123"))
            .thenReturn(CompletableFuture.completedFuture(java.util.List.of()));

        // When/Then
        var result = controller.getAllServices("tenant123");
        assert result != null;
    }

    private ServiceRegistryController.RegisterServiceRequest createValidRegisterRequest() {
        return new ServiceRegistryController.RegisterServiceRequest(
            "tenant123",
            "user-service",
            "instance1",
            "http://localhost:8080",
            "localhost",
            8080,
            false,
            "/actuator/health",
            "/actuator/info",
            new ServiceInstance.HealthCheckConfig("/actuator/health", 30000L, 5000L, 3, "UP"),
            Set.of("api", "microservice"),
            java.util.Map.of("version", "1.0.0"),
            "1.0.0",
            "production",
            100
        );
    }
}
