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
 * MongoDB Document for CompetitivePrice.
 * Maps to competitive_price collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "competitive_price")
public class CompetitivePriceEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String productId;

    @Indexed
    private String competitorName;

    private String competitorUrl;

    private BigDecimal competitorPrice;

    private BigDecimal ourPrice;

    private BigDecimal priceDifference;

    private BigDecimal percentageDifference;

    private String competitorProductName;

    @Indexed
    private boolean inStock;

    @Indexed
    private LocalDateTime lastChecked;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private Long version;
}
