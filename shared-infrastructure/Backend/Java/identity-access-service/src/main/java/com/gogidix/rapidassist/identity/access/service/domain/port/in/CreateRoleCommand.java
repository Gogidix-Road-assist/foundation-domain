package com.gogidix.rapidassist.identity.access.service.domain.port.in;

import com.gogidix.rapidassist.identity.access.service.domain.model.Role;

public interface CreateRoleCommand {
    Role create(Role role);
}
