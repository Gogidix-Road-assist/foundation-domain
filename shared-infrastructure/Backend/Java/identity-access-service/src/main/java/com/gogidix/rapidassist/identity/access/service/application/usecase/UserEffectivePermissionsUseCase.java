package com.gogidix.rapidassist.identity.access.service.application.usecase;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;
import com.gogidix.rapidassist.identity.access.service.domain.model.Role;
import com.gogidix.rapidassist.identity.access.service.domain.port.in.GetUserEffectivePermissionsQuery;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.MembershipRepository;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserEffectivePermissionsUseCase implements GetUserEffectivePermissionsQuery {

    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;

    public UserEffectivePermissionsUseCase(MembershipRepository membershipRepository, RoleRepository roleRepository) {
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<String> getPermissions(String tenantId, String userId) {
        List<Membership> memberships = membershipRepository.findByUserId(tenantId, userId);
        List<String> roleIds = memberships.stream()
                .flatMap(m -> m.roleIds().stream())
                .distinct()
                .toList();

        List<Role> roles = roleRepository.findByIds(tenantId, roleIds);
        Set<String> permissions = new HashSet<>();
        for (Role role : roles) {
            if (role.permissions() != null) {
                permissions.addAll(role.permissions());
            }
        }
        return permissions;
    }
}
