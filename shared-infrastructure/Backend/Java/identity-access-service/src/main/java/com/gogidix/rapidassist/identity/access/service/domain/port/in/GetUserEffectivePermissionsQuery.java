package com.gogidix.rapidassist.identity.access.service.domain.port.in;

import java.util.Set;

public interface GetUserEffectivePermissionsQuery {
    Set<String> getPermissions(String tenantId, String userId);
}
