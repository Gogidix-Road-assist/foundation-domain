package com.gogidix.rapidassist.api.gateway.adapters.in.web;

import com.gogidix.rapidassist.api.gateway.application.service.ComprehensiveGatewayRouteService;
import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import com.gogidix.rapidassist.api.gateway.domain.port.in.GatewayRouteCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GatewayRouteController.
 */
@ExtendWith(MockitoExtension.class)
class GatewayRouteControllerTest {

    @Mock
    private ComprehensiveGatewayRouteService routeService;

    @InjectMocks
    private GatewayRouteController controller;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void createRoute_WithValidRequest_Returns201Created() {
        // Given
        GatewayRoute createdRoute = GatewayRoute.create(
            "tenant123", "route1", "/api/test", "lb://TEST-SERVICE", "user1"
        );

        when(routeService.createRoute(any(GatewayRouteCommand.CreateRouteCommand.class)))
            .thenReturn(CompletableFuture.completedFuture(createdRoute));

        // When/Then
        webTestClient.post()
            .uri("/api/gateway/routes")
            .body(BodyInserters.fromValue(createValidCreateRequest()))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(GatewayRoute.class);
    }

    @Test
    void createRoute_WithMissingTenantId_Returns400BadRequest() {
        // Given
        GatewayRouteController.CreateRouteRequest invalidRequest =
            new GatewayRouteController.CreateRouteRequest(
                "", // empty tenantId
                "route1",
                "/api/test",
                "lb://TEST-SERVICE",
                "test-service",
                Set.of(GatewayRoute.HttpMethod.GET),
                null,
                null,
                0,
                null,
                1,
                null,
                null,
                null,
                null,
                Set.of(),
                "user1"
            );

        // When/Then
        webTestClient.post()
            .uri("/api/gateway/routes")
            .body(BodyInserters.fromValue(invalidRequest))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getRoute_WithValidId_Returns200Ok() {
        // Given
        String routeId = "route1";
        String tenantId = "tenant123";
        GatewayRoute route = GatewayRoute.create(
            tenantId, routeId, "/api/test", "lb://TEST-SERVICE", "user1"
        );

        when(routeService.getRouteById(tenantId, routeId))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(route)));

        // When/Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/gateway/routes/{routeId}")
                .queryParam("tenantId", tenantId)
                .build(routeId))
            .exchange()
            .expectStatus().isOk()
            .expectBody(GatewayRoute.class);
    }

    @Test
    void deleteRoute_WithValidId_Returns204NoContent() {
        // Given
        String routeId = "route1";
        String tenantId = "tenant123";
        String deletedBy = "user1";

        when(routeService.deleteRoute(tenantId, routeId, deletedBy))
            .thenReturn(CompletableFuture.completedFuture(true));

        // When/Then
        webTestClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/gateway/routes/{routeId}")
                .queryParam("tenantId", tenantId)
                .queryParam("deletedBy", deletedBy)
                .build(routeId))
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void enableRoute_WithValidId_Returns200Ok() {
        // Given
        String routeId = "route1";
        String tenantId = "tenant123";
        String enabledBy = "user1";
        GatewayRoute route = GatewayRoute.create(
            tenantId, routeId, "/api/test", "lb://TEST-SERVICE", "user1"
        );

        when(routeService.enableRoute(tenantId, routeId, enabledBy))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(route)));

        // When/Then
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/gateway/routes/{routeId}/enable")
                .queryParam("tenantId", tenantId)
                .queryParam("enabledBy", enabledBy)
                .build(routeId))
            .exchange()
            .expectStatus().isOk()
            .expectBody(GatewayRoute.class);
    }

    private GatewayRouteController.CreateRouteRequest createValidCreateRequest() {
        return new GatewayRouteController.CreateRouteRequest(
            "tenant123",
            "route1",
            "/api/test",
            "lb://TEST-SERVICE",
            "test-service",
            Set.of(GatewayRoute.HttpMethod.GET, GatewayRoute.HttpMethod.POST),
            null,
            null,
            0,
            null,
            1,
            null,
            null,
            null,
            null,
            Set.of(),
            "user1"
        );
    }
}
