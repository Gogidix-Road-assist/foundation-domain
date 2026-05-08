package com.gogidix.rapidassist.identity.access.service.domain.port.in;

import com.gogidix.rapidassist.identity.access.service.domain.model.Role;

import java.util.Optional;

public interface GetRoleQuery {
    Optional<Role> getById(String id);
}
