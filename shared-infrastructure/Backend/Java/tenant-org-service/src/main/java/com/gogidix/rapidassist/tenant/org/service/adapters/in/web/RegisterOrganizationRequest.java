package com.gogidix.rapidassist.tenant.org.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record RegisterOrganizationRequest(
        @NotBlank String orgId,
        @NotBlank String name
) {
}
