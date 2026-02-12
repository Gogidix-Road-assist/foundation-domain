package com.gogidix.rapidassist.orchestration.fleetorganization.application.service;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.CreateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.UpdateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.OrganizationResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.mapper.OrganizationMapper;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository.OrganizationRepository;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.exception.ValidationException;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for Organization entity
 * Handles business logic and coordinates between domain and infrastructure layers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Transactional
    public OrganizationResponseDto createOrganization(CreateOrganizationRequestDto request) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Creating organization for tenant: {}", tenantId);

        // Map DTO to entity
        Organization organization = mapper.toOrganization(request);
        organization.setTenantId(tenantId);

        // Validate entity
        try {
            organization.validate();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid organization data: " + e.getMessage(), e);
        }

        // Set level if root
        if (organization.getOrganizationType() == Organization.OrganizationType.ROOT) {
            organization.setLevel(0);
            organization.setParentId(null);
            organization.setPath("/" + organization.getOrganizationId());
        } else if (organization.getParentId() != null) {
            // Set path based on parent
            repository.findByIdAndTenantId(organization.getParentId(), tenantId)
                    .ifPresentOrElse(parent -> {
                        organization.setLevel(parent.getLevel() + 1);
                        organization.setPath(parent.getPath() + "/" + organization.getOrganizationId());
                    }, () -> {
                        throw new NotFoundException("Parent organization not found: " + organization.getParentId());
                    });
        } else {
            throw new ValidationException("Non-root organizations must have a parent");
        }

        // Save and return
        Organization saved = repository.save(organization);
        log.info("Created organization: {} for tenant: {}", saved.getOrganizationId(), tenantId);
        return mapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public OrganizationResponseDto getOrganization(String organizationId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching organization: {} for tenant: {}", organizationId, tenantId);

        return repository.findByIdAndTenantId(organizationId, tenantId)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new NotFoundException("Organization", organizationId));
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponseDto> getAllOrganizations() {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching all organizations for tenant: {}", tenantId);

        return repository.findByTenantId(tenantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponseDto> getOrganizationsByType(Organization.OrganizationType type) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching organizations by type: {} for tenant: {}", type, tenantId);

        return repository.findByTypeAndTenantId(type, tenantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponseDto> getChildOrganizations(String parentId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching child organizations for: {} for tenant: {}", parentId, tenantId);

        return repository.findByParentIdAndTenantId(parentId, tenantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrganizationResponseDto updateOrganization(String organizationId, UpdateOrganizationRequestDto request) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Updating organization: {} for tenant: {}", organizationId, tenantId);

        Organization existing = repository.findByIdAndTenantId(organizationId, tenantId)
                .orElseThrow(() -> new NotFoundException("Organization", organizationId));

        mapper.updateOrganizationFromDto(request, existing);
        existing.validate();

        Organization updated = repository.save(existing);
        log.info("Updated organization: {} for tenant: {}", updated.getOrganizationId(), tenantId);
        return mapper.toResponseDto(updated);
    }

    @Transactional
    public void deleteOrganization(String organizationId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Deleting organization: {} for tenant: {}", organizationId, tenantId);

        if (!repository.existsByIdAndTenantId(organizationId, tenantId)) {
            throw new NotFoundException("Organization", organizationId);
        }

        repository.deleteByIdAndTenantId(organizationId, tenantId);
        log.info("Deleted organization: {} for tenant: {}", organizationId, tenantId);
    }
}
