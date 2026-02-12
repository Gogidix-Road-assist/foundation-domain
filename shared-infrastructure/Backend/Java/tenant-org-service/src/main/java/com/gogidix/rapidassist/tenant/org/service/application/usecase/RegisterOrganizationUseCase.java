package com.gogidix.rapidassist.tenant.org.service.application.usecase;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import com.gogidix.rapidassist.tenant.org.service.domain.port.in.RegisterOrganizationCommand;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationStore;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class RegisterOrganizationUseCase implements RegisterOrganizationCommand {

    private final TenantOrganizationStore store;

    public RegisterOrganizationUseCase(TenantOrganizationStore store) {
        this.store = store;
    }

    @Override
    public TenantOrganization register(String tenantId, String orgId, String name) {
        TenantOrganization org = TenantOrganization.create(
                tenantId,
                orgId,
                name,
                TenantOrganization.OrganizationType.ENTERPRISE
        );
        return store.save(org).join();
    }
}
