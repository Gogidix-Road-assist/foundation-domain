package com.gogidix.rapidassist.identity.access.service.domain.model;

import java.util.Set;

public record Role(
        String id,
        String tenantId,
        String name,
        Set<String> permissions
) {
}
