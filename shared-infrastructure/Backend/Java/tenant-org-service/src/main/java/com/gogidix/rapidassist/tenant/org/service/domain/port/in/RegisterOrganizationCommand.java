package com.gogidix.rapidassist.tenant.org.service.domain.port.in;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;

public interface RegisterOrganizationCommand {

    TenantOrganization register(String tenantId, String orgId, String name);
}
