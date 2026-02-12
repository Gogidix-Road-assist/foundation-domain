package com.gogidix.rapidassist.identity.access.service.domain.model;

import java.util.Set;

public record Membership(
        String id,
        String tenantId,
        String orgUnitId,
        String userId,
        Set<String> roleIds
) {
}
