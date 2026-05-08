package com.gogidix.rapidassist.access.control.service.application.query;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.port.in.GetPermissionsQuery;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application Query Handler: GetPermissionsQueryHandler
 *
 * Handles permission retrieval queries.
 * Query handlers are read-only and do not modify state.
 */
@Component
@Transactional(readOnly = true)
public class GetPermissionsQueryHandler implements GetPermissionsQuery {

    private static final Logger log = LoggerFactory.getLogger(GetPermissionsQueryHandler.class);

    private final PermissionRepository permissionRepository;

    public GetPermissionsQueryHandler(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> getBySubject(String tenantId, String subjectId) {
        log.debug("Getting permissions for subject: tenantId={}, subjectId={}", tenantId, subjectId);
        return permissionRepository.findBySubjectId(subjectId, tenantId);
    }

    @Override
    public List<Permission> getValidBySubject(String tenantId, String subjectId) {
        log.debug("Getting valid permissions for subject: tenantId={}, subjectId={}", tenantId, subjectId);
        return permissionRepository.findValidBySubjectId(subjectId, tenantId);
    }

    @Override
    public Optional<Permission> getById(String tenantId, String permissionId) {
        log.debug("Getting permission by id: tenantId={}, permissionId={}", tenantId, permissionId);
        return permissionRepository.findById(permissionId, tenantId);
    }

    @Override
    public List<Permission> getAllByTenant(String tenantId) {
        log.debug("Getting all permissions for tenant: tenantId={}", tenantId);
        return permissionRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Permission> getBySubjectAndResourceAndAction(String tenantId, String subjectId,
                                                             String resource, String action) {
        log.debug("Getting permissions by criteria: tenantId={}, subjectId={}, resource={}, action={}",
                tenantId, subjectId, resource, action);
        return permissionRepository.findBySubjectAndResourceAndAction(subjectId, resource, action, tenantId);
    }
}
