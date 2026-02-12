package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceHistory;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceHistoryEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between PriceHistory domain model and PriceHistoryEntity.
 */
@Component
public class PriceHistoryPersistenceMapper {

    public PriceHistoryEntity toEntity(PriceHistory domain) {
        if (domain == null) {
            return null;
        }

        return PriceHistoryEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .productId(domain.getProductId())
                .priceRuleId(domain.getPriceRuleId())
                .oldPrice(domain.getOldPrice())
                .newPrice(domain.getNewPrice())
                .priceChange(domain.getPriceChange())
                .percentageChange(domain.getPercentageChange())
                .changeReason(domain.getChangeReason())
                .changedBy(domain.getChangedBy())
                .changeSource(domain.getChangeSource())
                .createdAt(domain.getCreatedAt())
                .version(1L)
                .build();
    }

    public PriceHistory toDomain(PriceHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return PriceHistory.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .productId(entity.getProductId())
                .priceRuleId(entity.getPriceRuleId())
                .oldPrice(entity.getOldPrice())
                .newPrice(entity.getNewPrice())
                .priceChange(entity.getPriceChange())
                .percentageChange(entity.getPercentageChange())
                .changeReason(entity.getChangeReason())
                .changedBy(entity.getChangedBy())
                .changeSource(entity.getChangeSource())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
