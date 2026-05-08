package com.gogidix.rapidassist.identity.access.service.application.usecase;

import com.gogidix.rapidassist.identity.access.service.domain.model.Role;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.CreateRoleCommand;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetRoleQuery;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleUseCase implements CreateRoleCommand, GetRoleQuery {

    private final RoleRepository roleRepository;

    public RoleUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> getById(String id) {
        return roleRepository.findById(id);
    }
}
