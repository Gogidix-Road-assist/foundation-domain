package com.gogidix.rapidassist.access.control.service.application.command;

import com.gogidix.rapidassist.access.control.service.domain.aggregate.RoleAggregate;
import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.model.Role;
import com.gogidix.rapidassist.access.control.service.domain.model.Subject;
import com.gogidix.rapidassist.access.control.service.domain.port.in.RoleManagementCommand;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import com.gogidix.rapidassist.access.control.service.domain.repository.RoleRepository;
import com.gogidix.rapidassist.access.control.service.domain.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Application Command Handler: RoleManagementCommandHandler
 *
 * Handles role management operations including:
 * - Creating, updating, deleting roles
 * - Assigning/removing permissions from roles
 * - Assigning/removing roles from subjects
 */
@Component
@Transactional
public class RoleManagementCommandHandler implements RoleManagementCommand {

    private static final Logger log = LoggerFactory.getLogger(RoleManagementCommandHandler.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final SubjectRepository subjectRepository;

    public RoleManagementCommandHandler(RoleRepository roleRepository,
                                        PermissionRepository permissionRepository,
                                        SubjectRepository subjectRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Role createRole(String tenantId, String name, String description, String createdBy) {
        log.info("Creating role: tenantId={}, name={}", tenantId, name);

        // Check if role already exists
        if (roleRepository.existsByName(name, tenantId)) {
            throw new IllegalArgumentException("Role already exists: " + name);
        }

        Role role = Role.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .createdAt(Instant.now())
                .createdBy(createdBy)
                .active(true)
                .build();

        Role saved = roleRepository.save(role);

        log.info("Role created successfully: id={}, name={}", saved.getId(), saved.getName());

        return saved;
    }

    @Override
    public Role updateRole(String tenantId, String roleId, String name,
                          String description, String updatedBy) {
        log.info("Updating role: tenantId={}, roleId={}", tenantId, roleId);

        Role role = roleRepository.findById(roleId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        // Check if new name conflicts with existing role
        if (!role.getName().equals(name) && roleRepository.existsByName(name, tenantId)) {
            throw new IllegalArgumentException("Role already exists with name: " + name);
        }

        role.update(name, description, updatedBy);

        Role saved = roleRepository.save(role);

        log.info("Role updated successfully: id={}", roleId);

        return saved;
    }

    @Override
    public boolean deleteRole(String tenantId, String roleId) {
        log.info("Deleting role: tenantId={}, roleId={}", tenantId, roleId);

        Role role = roleRepository.findById(roleId, tenantId)
                .orElse(null);

        if (role == null) {
            log.warn("Role not found for deletion: id={}", roleId);
            return false;
        }

        // Deactivate role first (soft delete)
        role.deactivate();
        roleRepository.save(role);

        // TODO: Remove role from all subjects that have it assigned

        // Hard delete
        boolean deleted = roleRepository.deleteById(roleId, tenantId);

        log.info("Role deleted: id={}, success={}", roleId, deleted);

        return deleted;
    }

    @Override
    public RoleAggregate assignPermission(String tenantId, String roleId, String permissionId) {
        log.info("Assigning permission to role: tenantId={}, roleId={}, permissionId={}",
                tenantId, roleId, permissionId);

        // Validate role exists
        Role role = roleRepository.findById(roleId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        // Validate permission exists and belongs to same tenant
        Permission permission = permissionRepository.findById(permissionId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));

        // Add permission to role
        role.addPermission(permissionId);
        roleRepository.save(role);

        // Create and return aggregate
        List<Permission> permissions = new ArrayList<>();
        permissions.add(permission);

        RoleAggregate aggregate = RoleAggregate.builder()
                .role(role)
                .permissions(permissions)
                .build();

        log.info("Permission assigned to role successfully: roleId={}, permissionId={}",
                roleId, permissionId);

        return aggregate;
    }

    @Override
    public RoleAggregate removePermission(String tenantId, String roleId, String permissionId) {
        log.info("Removing permission from role: tenantId={}, roleId={}, permissionId={}",
                tenantId, roleId, permissionId);

        Role role = roleRepository.findById(roleId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        // Remove permission from role (requires re-creating role with updated permissions)
        Role updated = Role.builder()
                .id(role.getId())
                .tenantId(role.getTenantId())
                .name(role.getName())
                .description(role.getDescription())
                .permissionIds(role.getPermissionIds().stream()
                        .filter(id -> !id.equals(permissionId))
                        .toList())
                .createdAt(role.getCreatedAt())
                .createdBy(role.getCreatedBy())
                .updatedAt(Instant.now())
                .active(role.isActive())
                .build();

        roleRepository.save(updated);

        log.info("Permission removed from role: roleId={}, permissionId={}", roleId, permissionId);

        // Return aggregate without the removed permission
        return RoleAggregate.builder()
                .role(updated)
                .permissions(List.of())
                .build();
    }

    @Override
    public boolean assignRoleToSubject(String tenantId, String subjectId, String roleId) {
        log.info("Assigning role to subject: tenantId={}, subjectId={}, roleId={}",
                tenantId, subjectId, roleId);

        // Validate role exists
        Role role = roleRepository.findById(roleId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        // Get or create subject
        Subject subject = subjectRepository.findBySubjectKey(subjectId, tenantId)
                .orElseGet(() -> {
                    Subject newSubject = Subject.builder()
                            .id(UUID.randomUUID().toString())
                            .tenantId(tenantId)
                            .subjectType("USER")
                            .subjectKey(subjectId)
                            .displayName(subjectId)
                            .createdAt(Instant.now())
                            .active(true)
                            .build();
                    return subjectRepository.save(newSubject);
                });

        // Check if subject already has this role
        if (subject.hasRole(roleId)) {
            log.debug("Subject already has role: subjectId={}, roleId={}", subjectId, roleId);
            return true;
        }

        // Assign role to subject
        subject.assignRole(roleId);
        subjectRepository.save(subject);

        log.info("Role assigned to subject successfully: subjectId={}, roleId={}",
                subjectId, roleId);

        return true;
    }

    @Override
    public boolean removeRoleFromSubject(String tenantId, String subjectId, String roleId) {
        log.info("Removing role from subject: tenantId={}, subjectId={}, roleId={}",
                tenantId, subjectId, roleId);

        Subject subject = subjectRepository.findBySubjectKey(subjectId, tenantId)
                .orElse(null);

        if (subject == null) {
            log.warn("Subject not found: subjectId={}", subjectId);
            return false;
        }

        if (!subject.hasRole(roleId)) {
            log.debug("Subject does not have role: subjectId={}, roleId={}", subjectId, roleId);
            return false;
        }

        // Create updated subject without the role
        Subject updated = Subject.builder()
                .id(subject.getId())
                .tenantId(subject.getTenantId())
                .subjectType(subject.getSubjectType())
                .subjectKey(subject.getSubjectKey())
                .displayName(subject.getDisplayName())
                .roleIds(subject.getRoleIds().stream()
                        .filter(id -> !id.equals(roleId))
                        .toList())
                .createdAt(subject.getCreatedAt())
                .lastAccessAt(subject.getLastAccessAt())
                .active(subject.isActive())
                .build();

        subjectRepository.save(updated);

        log.info("Role removed from subject: subjectId={}, roleId={}", subjectId, roleId);

        return true;
    }
}
