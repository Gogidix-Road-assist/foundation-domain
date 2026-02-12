package com.gogidix.rapidassist.insurer.adapter.service.application.service;

import com.gogidix.rapidassist.insurer.adapter.service.application.dto.CreateInsurerMappingRequest;
import com.gogidix.rapidassist.insurer.adapter.service.application.dto.InsurerMappingResponse;
import com.gogidix.rapidassist.insurer.adapter.service.application.dto.UpdateInsurerMappingRequest;
import com.gogidix.rapidassist.insurer.adapter.service.domain.model.InsurerMapping;
import com.gogidix.rapidassist.insurer.adapter.service.domain.port.out.InsurerMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InsurerMappingService {

    private final InsurerMappingRepository repository;

    public InsurerMappingService(InsurerMappingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InsurerMappingResponse> findByTenant(String tenantId) {
        return repository.findByTenantId(tenantId).stream()
                .map(InsurerMappingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InsurerMappingResponse findByTenantAndId(String tenantId, String id) {
        return repository.findByTenantIdAndId(tenantId, id)
                .map(InsurerMappingResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Insurer mapping not found"));
    }

    @Transactional(readOnly = true)
    public List<InsurerMappingResponse> findByTenantAndAdapterType(String tenantId, String adapterType) {
        return repository.findByTenantIdAndAdapterType(tenantId, adapterType).stream()
                .map(InsurerMappingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InsurerMappingResponse> findEnabledByTenant(String tenantId) {
        return repository.findByTenantIdAndEnabled(tenantId, true).stream()
                .map(InsurerMappingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InsurerMappingResponse> searchByTenant(String tenantId, String searchTerm) {
        return repository.searchByTenantId(tenantId, searchTerm).stream()
                .map(InsurerMappingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countByTenant(String tenantId) {
        return repository.countByTenantId(tenantId);
    }

    public InsurerMappingResponse create(String tenantId, CreateInsurerMappingRequest request) {
        repository.findByTenantIdAndInsurerCode(tenantId, request.getInsurerCode())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Insurer mapping with code '" + request.getInsurerCode() + "' already exists for tenant");
                });

        InsurerMapping mapping = new InsurerMapping();
        mapping.setTenantId(tenantId);
        mapping.setInsurerCode(request.getInsurerCode());
        mapping.setInsurerName(request.getInsurerName());
        mapping.setAdapterType(request.getAdapterType());
        mapping.setEndpointUrl(request.getEndpointUrl());
        mapping.setApiKey(request.getApiKey());
        mapping.setTimeoutMs(request.getTimeoutMs());
        mapping.setRetryAttempts(request.getRetryAttempts());
        mapping.setEnabled(request.getEnabled());
        mapping.setConfiguration(request.getConfiguration());

        InsurerMapping saved = repository.save(mapping);
        return InsurerMappingResponse.from(saved);
    }

    public InsurerMappingResponse update(String tenantId, String id, UpdateInsurerMappingRequest request) {
        InsurerMapping mapping = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Insurer mapping not found"));

        repository.findByTenantIdAndInsurerCodeExcludingId(tenantId, request.getInsurerCode(), id)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Insurer mapping with code '" + request.getInsurerCode() + "' already exists");
                });

        mapping.setInsurerCode(request.getInsurerCode());
        mapping.setInsurerName(request.getInsurerName());
        mapping.setAdapterType(request.getAdapterType());
        mapping.setEndpointUrl(request.getEndpointUrl());
        mapping.setApiKey(request.getApiKey());
        mapping.setTimeoutMs(request.getTimeoutMs());
        mapping.setRetryAttempts(request.getRetryAttempts());
        mapping.setEnabled(request.getEnabled());
        mapping.setConfiguration(request.getConfiguration());

        InsurerMapping updated = repository.save(mapping);
        return InsurerMappingResponse.from(updated);
    }

    public void delete(String tenantId, String id) {
        InsurerMapping mapping = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Insurer mapping not found"));

        repository.delete(mapping);
    }

    public InsurerMappingResponse enable(String tenantId, String id) {
        InsurerMapping mapping = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Insurer mapping not found"));

        mapping.setEnabled(true);
        InsurerMapping updated = repository.save(mapping);
        return InsurerMappingResponse.from(updated);
    }

    public InsurerMappingResponse disable(String tenantId, String id) {
        InsurerMapping mapping = repository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Insurer mapping not found"));

        mapping.setEnabled(false);
        InsurerMapping updated = repository.save(mapping);
        return InsurerMappingResponse.from(updated);
    }
}
