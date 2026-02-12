package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRuleStatus;
import com.gogidix.rapidassist.ai.pricing.domain.model.PricingStrategyType;
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
 * MongoDB Document for PriceRule.
 * Maps to price_rule collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "price_rule")
public class PriceRuleEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private PricingStrategyType strategyType;

    @Indexed
    private PriceRuleStatus status;

    private BigDecimal basePrice;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private BigDecimal currentPrice;

    @Indexed
    private String productId;

    @Indexed
    private String categoryId;

    private String parameters;

    @Indexed
    private Integer priority;

    @Indexed
    private LocalDateTime validFrom;

    @Indexed
    private LocalDateTime validUntil;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}
