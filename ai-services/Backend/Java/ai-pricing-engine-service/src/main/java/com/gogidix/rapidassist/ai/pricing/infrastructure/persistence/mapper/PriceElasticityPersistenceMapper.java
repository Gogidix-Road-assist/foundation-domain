package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceElasticity;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceElasticityEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between PriceElasticity domain model and PriceElasticityEntity.
 */
@Component
public class PriceElasticityPersistenceMapper {

    public PriceElasticityEntity toEntity(PriceElasticity domain) {
        if (domain == null) {
            return null;
        }

        return PriceElasticityEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .productId(domain.getProductId())
                .categoryId(domain.getCategoryId())
                .elasticityCoefficient(domain.getElasticityCoefficient())
                .elasticityType(domain.getElasticityType())
                .originalPrice(domain.getOriginalPrice())
                .newPrice(domain.getNewPrice())
                .priceChangePercent(domain.getPriceChangePercent())
                .originalDemand(domain.getOriginalDemand())
                .newDemand(domain.getNewDemand())
                .demandChangePercent(domain.getDemandChangePercent())
                .periodStart(domain.getPeriodStart())
                .periodEnd(domain.getPeriodEnd())
                .createdAt(domain.getCreatedAt())
                .version(1L)
                .build();
    }

    public PriceElasticity toDomain(PriceElasticityEntity entity) {
        if (entity == null) {
            return null;
        }

        return PriceElasticity.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .productId(entity.getProductId())
                .categoryId(entity.getCategoryId())
                .elasticityCoefficient(entity.getElasticityCoefficient())
                .elasticityType(entity.getElasticityType())
                .originalPrice(entity.getOriginalPrice())
                .newPrice(entity.getNewPrice())
                .priceChangePercent(entity.getPriceChangePercent())
                .originalDemand(entity.getOriginalDemand())
                .newDemand(entity.getNewDemand())
                .demandChangePercent(entity.getDemandChangePercent())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
