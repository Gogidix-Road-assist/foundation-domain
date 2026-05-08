package com.gogidix.rapidassist.tenant.org.service.adapters.in.web;

import java.time.Instant;

public record TenantOrganizationResponse(
        String orgId,
        String name,
        Instant createdAt
) {
}
