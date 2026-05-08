package com.gogidix.rapidassist.access.control.service.application.query;

import com.gogidix.rapidassist.access.control.service.domain.model.Role;
import com.gogidix.rapidassist.access.control.service.domain.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application Query Handler: GetRolesQueryHandler
 *
 * Handles role retrieval queries.
 * Query handlers are read-only and do not modify state.
 */
@Component
@Transactional(readOnly = true)
public class GetRolesQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetRolesQueryHandler.class);

    private final RoleRepository roleRepository;

    public GetRolesQueryHandler(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getById(String tenantId, String roleId) {
        log.debug("Getting role by id: tenantId={}, roleId={}", tenantId, roleId);
        return roleRepository.findById(roleId, tenantId);
    }

    public Optional<Role> getByName(String tenantId, String name) {
        log.debug("Getting role by name: tenantId={}, name={}", tenantId, name);
        return roleRepository.findByName(name, tenantId);
    }

    public List<Role> getAllByTenant(String tenantId) {
        log.debug("Getting all roles for tenant: tenantId={}", tenantId);
        return roleRepository.findByTenantId(tenantId);
    }

    public List<Role> getActiveByTenant(String tenantId) {
        log.debug("Getting active roles for tenant: tenantId={}", tenantId);
        return roleRepository.findActiveByTenantId(tenantId);
    }

    public List<Role> getBySubject(String tenantId, String subjectId) {
        log.debug("Getting roles for subject: tenantId={}, subjectId={}", tenantId, subjectId);
        return roleRepository.findBySubjectId(subjectId, tenantId);
    }

    public boolean existsByName(String tenantId, String name) {
        return roleRepository.existsByName(name, tenantId);
    }
}
