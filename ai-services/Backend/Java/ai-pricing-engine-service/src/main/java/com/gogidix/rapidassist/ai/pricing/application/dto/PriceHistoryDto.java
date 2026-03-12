package com.gogidix.rapidassist.ai.pricing.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for PriceHistory.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String productId;
    private String priceRuleId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal priceChange;
    private BigDecimal percentageChange;
    private String changeReason;
    private String changedBy;
    private String changeSource;
    private LocalDateTime createdAt;
}
