package com.gogidix.rapidassist.identity.access.service.domain.port.in;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;

import java.util.List;
import java.util.Optional;

public interface GetOrgUnitQuery {

    Optional<OrgUnit> getById(String id);

    List<OrgUnit> getChildren(String tenantId, String parentId);
}
