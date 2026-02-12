package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.pricing.domain.model.Discount;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.DiscountEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Discount domain model and DiscountEntity.
 */
@Slf4j
@Component
public class DiscountPersistenceMapper {

    public DiscountEntity toEntity(Discount domain) {
        if (domain == null) {
            return null;
        }

        return DiscountEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .discountType(domain.getDiscountType())
                .discountValue(domain.getDiscountValue())
                .maxDiscountAmount(domain.getMaxDiscountAmount())
                .minPurchaseAmount(domain.getMinPurchaseAmount())
                .usageLimit(domain.getUsageLimit())
                .usageCount(domain.getUsageCount())
                .productId(domain.getProductId())
                .categoryId(domain.getCategoryId())
                .customerId(domain.getCustomerId())
                .validFrom(domain.getValidFrom())
                .validUntil(domain.getValidUntil())
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .version(1L)
                .build();
    }

    public Discount toDomain(DiscountEntity entity) {
        if (entity == null) {
            return null;
        }

        return Discount.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .minPurchaseAmount(entity.getMinPurchaseAmount())
                .usageLimit(entity.getUsageLimit())
                .usageCount(entity.getUsageCount())
                .productId(entity.getProductId())
                .categoryId(entity.getCategoryId())
                .customerId(entity.getCustomerId())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
