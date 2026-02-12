package com.gogidix.rapidassist.identity.access.service;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;
import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;
import com.gogidix.rapidassist.identity.access.service.domain.model.Role;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.MembershipRepository;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.OrgUnitRepository;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.RoleRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@TestConfiguration
public class TestRepositoryConfig {

    @Bean
    public OrgUnitRepository orgUnitRepository() {
        return new OrgUnitRepository() {
            private final Map<String, OrgUnit> store = new ConcurrentHashMap<>();

            @Override
            public OrgUnit save(OrgUnit orgUnit) {
                String id = orgUnit.id() == null || orgUnit.id().isBlank() ? UUID.randomUUID().toString() : orgUnit.id();
                OrgUnit saved = new OrgUnit(id, orgUnit.tenantId(), orgUnit.country(), orgUnit.type(), orgUnit.name(), orgUnit.parentId());
                store.put(id, saved);
                return saved;
            }

            @Override
            public Optional<OrgUnit> findById(String id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public List<OrgUnit> findChildren(String tenantId, String parentId) {
                return store.values().stream()
                        .filter(o -> Objects.equals(o.tenantId(), tenantId) && Objects.equals(o.parentId(), parentId))
                        .toList();
            }
        };
    }

    @Bean
    public RoleRepository roleRepository() {
        return new RoleRepository() {
            private final Map<String, Role> store = new ConcurrentHashMap<>();

            @Override
            public Role save(Role role) {
                String id = role.id() == null || role.id().isBlank() ? UUID.randomUUID().toString() : role.id();
                Role saved = new Role(id, role.tenantId(), role.name(), role.permissions());
                store.put(id, saved);
                return saved;
            }

            @Override
            public Optional<Role> findById(String id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public List<Role> findByIds(String tenantId, List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return List.of();
                }
                return ids.stream()
                        .map(store::get)
                        .filter(Objects::nonNull)
                        .filter(r -> Objects.equals(r.tenantId(), tenantId))
                        .toList();
            }
        };
    }

    @Bean
    public MembershipRepository membershipRepository() {
        return new MembershipRepository() {
            private final Map<String, Membership> store = new ConcurrentHashMap<>();

            @Override
            public Membership save(Membership membership) {
                String id = membership.id() == null || membership.id().isBlank() ? UUID.randomUUID().toString() : membership.id();
                Membership saved = new Membership(id, membership.tenantId(), membership.orgUnitId(), membership.userId(), membership.roleIds());
                store.put(id, saved);
                return saved;
            }

            @Override
            public List<Membership> findByUserId(String tenantId, String userId) {
                return store.values().stream()
                        .filter(m -> Objects.equals(m.tenantId(), tenantId) && Objects.equals(m.userId(), userId))
                        .toList();
            }

            @Override
            public List<Membership> findByOrgUnitId(String tenantId, String orgUnitId) {
                return store.values().stream()
                        .filter(m -> Objects.equals(m.tenantId(), tenantId) && Objects.equals(m.orgUnitId(), orgUnitId))
                        .toList();
            }
        };
    }
}
