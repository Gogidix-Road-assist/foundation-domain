package com.gogidix.rapidassist.identity.access.service.domain.port.in;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;

public interface CreateOrgUnitCommand {
    OrgUnit create(OrgUnit orgUnit);
}
