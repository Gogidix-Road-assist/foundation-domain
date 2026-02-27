package com.gogidix.rapidassist.identity.access.service.domain.port.out;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;

import java.util.List;

public interface MembershipRepository {

    Membership save(Membership membership);

    List<Membership> findByUserId(String tenantId, String userId);

    List<Membership> findByOrgUnitId(String tenantId, String orgUnitId);
}
