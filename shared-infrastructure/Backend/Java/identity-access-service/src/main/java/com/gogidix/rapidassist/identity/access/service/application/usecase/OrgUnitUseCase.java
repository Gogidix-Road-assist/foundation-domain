package com.gogidix.rapidassist.identity.access.service.application.usecase;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.CreateOrgUnitCommand;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetOrgUnitQuery;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.OrgUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrgUnitUseCase implements CreateOrgUnitCommand, GetOrgUnitQuery {

    private final OrgUnitRepository orgUnitRepository;

    public OrgUnitUseCase(OrgUnitRepository orgUnitRepository) {
        this.orgUnitRepository = orgUnitRepository;
    }

    @Override
    public OrgUnit create(OrgUnit orgUnit) {
        return orgUnitRepository.save(orgUnit);
    }

    @Override
    public Optional<OrgUnit> getById(String id) {
        return orgUnitRepository.findById(id);
    }

    @Override
    public List<OrgUnit> getChildren(String tenantId, String parentId) {
        return orgUnitRepository.findChildren(tenantId, parentId);
    }
}
