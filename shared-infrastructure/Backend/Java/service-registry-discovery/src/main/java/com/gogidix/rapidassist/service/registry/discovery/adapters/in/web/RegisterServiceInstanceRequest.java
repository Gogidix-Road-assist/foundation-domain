package com.gogidix.rapidassist.service.registry.discovery.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record RegisterServiceInstanceRequest(
        @NotBlank String serviceName,
        @NotBlank String instanceId,
        @NotBlank String baseUrl
) {
}
