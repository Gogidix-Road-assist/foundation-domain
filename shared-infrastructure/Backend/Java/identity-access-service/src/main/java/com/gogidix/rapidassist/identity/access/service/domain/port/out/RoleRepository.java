package com.gogidix.rapidassist.identity.access.service.domain.port.out;

import com.gogidix.rapidassist.identity.access.service.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(String id);

    List<Role> findByIds(String tenantId, List<String> ids);
}
