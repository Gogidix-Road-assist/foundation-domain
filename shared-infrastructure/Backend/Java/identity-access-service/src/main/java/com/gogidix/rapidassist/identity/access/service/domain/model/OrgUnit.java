package com.gogidix.rapidassist.identity.access.service.domain.model;

public record OrgUnit(
        String id,
        String tenantId,
        String country,
        OrgUnitType type,
        String name,
        String parentId
) {
}
