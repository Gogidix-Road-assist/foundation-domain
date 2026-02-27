package com.gogidix.rapidassist.identity.access.service.domain.port.out;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;

import java.util.List;
import java.util.Optional;

public interface OrgUnitRepository {

    OrgUnit save(OrgUnit orgUnit);

    Optional<OrgUnit> findById(String id);

    List<OrgUnit> findChildren(String tenantId, String parentId);
}
