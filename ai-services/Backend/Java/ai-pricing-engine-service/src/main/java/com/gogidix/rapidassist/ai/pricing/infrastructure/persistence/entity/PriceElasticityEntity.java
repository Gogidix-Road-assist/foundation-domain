package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for PriceElasticity.
 * Maps to price_elasticity collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "price_elasticity")
public class PriceElasticityEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String productId;

    @Indexed
    private String categoryId;

    private BigDecimal elasticityCoefficient;

    @Indexed
    private String elasticityType;

    private BigDecimal originalPrice;

    private BigDecimal newPrice;

    private BigDecimal priceChangePercent;

    private Integer originalDemand;

    private Integer newDemand;

    private BigDecimal demandChangePercent;

    @Indexed
    private LocalDateTime periodStart;

    @Indexed
    private LocalDateTime periodEnd;

    @Indexed
    private LocalDateTime createdAt;

    private Long version;
}
