package com.gogidix.rapidassist.identity.access.service.domain.port.in;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;

public interface AssignMembershipCommand {
    Membership assign(Membership membership);
}
