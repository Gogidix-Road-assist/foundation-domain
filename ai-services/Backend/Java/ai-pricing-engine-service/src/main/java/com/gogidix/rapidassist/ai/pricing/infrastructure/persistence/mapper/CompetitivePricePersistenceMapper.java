package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.pricing.domain.model.CompetitivePrice;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.CompetitivePriceEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between CompetitivePrice domain model and CompetitivePriceEntity.
 */
@Component
public class CompetitivePricePersistenceMapper {

    public CompetitivePriceEntity toEntity(CompetitivePrice domain) {
        if (domain == null) {
            return null;
        }

        return CompetitivePriceEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .productId(domain.getProductId())
                .competitorName(domain.getCompetitorName())
                .competitorUrl(domain.getCompetitorUrl())
                .competitorPrice(domain.getCompetitorPrice())
                .ourPrice(domain.getOurPrice())
                .priceDifference(domain.getPriceDifference())
                .percentageDifference(domain.getPercentageDifference())
                .competitorProductName(domain.getCompetitorProductName())
                .inStock(domain.isInStock())
                .lastChecked(domain.getLastChecked())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(1L)
                .build();
    }

    public CompetitivePrice toDomain(CompetitivePriceEntity entity) {
        if (entity == null) {
            return null;
        }

        return CompetitivePrice.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .productId(entity.getProductId())
                .competitorName(entity.getCompetitorName())
                .competitorUrl(entity.getCompetitorUrl())
                .competitorPrice(entity.getCompetitorPrice())
                .ourPrice(entity.getOurPrice())
                .priceDifference(entity.getPriceDifference())
                .percentageDifference(entity.getPercentageDifference())
                .competitorProductName(entity.getCompetitorProductName())
                .inStock(entity.isInStock())
                .lastChecked(entity.getLastChecked())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
