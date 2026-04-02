package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Resource;
import org.springframework.stereotype.Component;

/**
 * Converter: ResourceDocumentConverter
 *
 * Converts between Resource domain model and ResourceDocument.
 */
@Component
public class ResourceDocumentConverter {

    public ResourceDocument toDocument(Resource resource) {
        return new ResourceDocument(
                resource.getId(),
                resource.getTenantId(),
                resource.getResourceType(),
                resource.getResourcePath(),
                resource.getDescription(),
                resource.getOwner(),
                resource.getResourceGroup(),
                resource.getCreatedAt(),
                resource.isActive()
        );
    }

    public Resource toDomain(ResourceDocument document) {
        return Resource.builder()
                .id(document.getId())
                .tenantId(document.getTenantId())
                .resourceType(document.getResourceType())
                .resourcePath(document.getResourcePath())
                .description(document.getDescription())
                .owner(document.getOwner())
                .resourceGroup(document.getResourceGroup())
                .createdAt(document.getCreatedAt())
                .active(document.isActive())
                .build();
    }
}
