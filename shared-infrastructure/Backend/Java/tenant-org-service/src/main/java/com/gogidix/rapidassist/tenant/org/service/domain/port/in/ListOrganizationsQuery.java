package com.gogidix.rapidassist.tenant.org.service.domain.port.in;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;

import java.util.List;

public interface ListOrganizationsQuery {

    List<TenantOrganization> list(String tenantId);
}
