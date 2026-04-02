package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.adapter;

import com.gogidix.rapidassist.identity.access.service.domain.model.Role;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.RoleRepository;
import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document.RoleDocument;
import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.repository.MongoRoleSpringRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!test")
public class MongoRoleRepositoryAdapter implements RoleRepository {

    private final MongoRoleSpringRepository springRepository;

    public MongoRoleRepositoryAdapter(MongoRoleSpringRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Role save(Role role) {
        RoleDocument d = toDoc(role);
        RoleDocument saved = springRepository.save(d);
        return toDomain(saved);
    }

    @Override
    public Optional<Role> findById(String id) {
        return springRepository.findById(id).map(MongoRoleRepositoryAdapter::toDomain);
    }

    @Override
    public List<Role> findByIds(String tenantId, List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return springRepository.findByTenantIdAndIdIn(tenantId, ids).stream().map(MongoRoleRepositoryAdapter::toDomain).toList();
    }

    private static RoleDocument toDoc(Role role) {
        RoleDocument d = new RoleDocument();
        d.setId(role.id());
        d.setTenantId(role.tenantId());
        d.setName(role.name());
        d.setPermissions(role.permissions());
        return d;
    }

    private static Role toDomain(RoleDocument d) {
        return new Role(d.getId(), d.getTenantId(), d.getName(), d.getPermissions());
    }
}
