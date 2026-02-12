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
 * MongoDB Document for PriceHistory.
 * Maps to price_history collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "price_history")
public class PriceHistoryEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String productId;

    @Indexed
    private String priceRuleId;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private BigDecimal priceChange;

    private BigDecimal percentageChange;

    private String changeReason;

    @Indexed
    private String changedBy;

    @Indexed
    private String changeSource;

    @Indexed
    private LocalDateTime createdAt;

    private Long version;
}
