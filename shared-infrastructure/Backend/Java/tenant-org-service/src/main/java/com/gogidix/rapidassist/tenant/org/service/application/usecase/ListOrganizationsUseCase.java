package com.gogidix.rapidassist.tenant.org.service.application.usecase;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import com.gogidix.rapidassist.tenant.org.service.domain.port.in.ListOrganizationsQuery;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ListOrganizationsUseCase implements ListOrganizationsQuery {

    private final TenantOrganizationStore store;

    public ListOrganizationsUseCase(TenantOrganizationStore store) {
        this.store = store;
    }

    @Override
    public List<TenantOrganization> list(String tenantId) {
        return store.findByTenant(tenantId).join();
    }
}
